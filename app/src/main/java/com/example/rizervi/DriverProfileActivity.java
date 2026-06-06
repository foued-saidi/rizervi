package com.example.rizervi;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class DriverProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        String name = getIntent().getStringExtra("DRIVER_NAME");
        String driverId = getIntent().getStringExtra("DRIVER_ID");
        double rating = getIntent().getDoubleExtra("RATING", 0.0);
        double lat = getIntent().getDoubleExtra("LAT", 0.0);
        double lng = getIntent().getDoubleExtra("LNG", 0.0);

        TextView tvName = findViewById(R.id.tvProfileName);
        TextView tvRating = findViewById(R.id.tvProfileRating);
        TextView tvLocation = findViewById(R.id.tvLocationInfo);
        TextView tvReviews = findViewById(R.id.tvReviews);
        Button btnOpenMap = findViewById(R.id.btnOpenMap);
        Button btnChat = findViewById(R.id.btnChatWithDriver);

        tvName.setText(name);
        tvRating.setText(String.format("%.1f ★", rating));
        tvLocation.setText("Position actuelle: " + lat + ", " + lng);

        generateRandomReviews(tvReviews);

        btnOpenMap.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(" + name + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        btnChat.setOnClickListener(v -> {
            if (driverId != null) {
                Intent chatIntent = new Intent(this, ChatActivity.class);
                chatIntent.putExtra("RECEIVER_ID", driverId);
                chatIntent.putExtra("RECEIVER_NAME", name);
                startActivity(chatIntent);
            } else {
                android.widget.Toast.makeText(this, "Impossible d'ouvrir le chat", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateRandomReviews(TextView tv) {
        String[] names = {"Ahmed", "Sonia", "Khalil", "Yassine", "Meriem", "Firas", "Ines", "Hedi", "Rim", "Omar", "Amira", "Zied", "Nour", "Hamza", "Salma", "Wael", "Hajer", "Sami", "Olfa", "Walid"};
        
        String[] reviewsTounsi = {
            "Ya3tik essa7a, trajet mrigel 3al lekher.",
            "Chauffeur m-rabi w karhba ndhifa.",
            "Krahabtou tayra w yasir metrobbi.",
            "Walla bravo, thnaya kamla dho7k w jaw.",
            "Top top, rabi y-berik-lek khouya.",
            "Service impeccable, ma t-3atalech.",
            "Chauffeur mrigel w dima fel wa9t.",
            "5/5, rabi ywasel es-salem.",
            "Mouch normal, conduite tayra barcha.",
            "Jawu behi w karhbatu comfortable.",
            "Mchallah 3lik, karhba lux w chauffeur mthe9ef.",
            "Bara rabi m3ak, top service.",
            "A7la trajet m3ak ya m3alem.",
            "Karhba tayra w conduite mrigla.",
            "Mouch e5er marra nchallah.",
            "Chauffeur tayeb w karhba ndhifa.",
            "Service 5 stars, bravo.",
            "Thnaya t3adet fisa3 m3a ejaw.",
            "Chauffeur metrobbi barcha.",
            "Rabi ywasel l-salem, kol chay mrigel.",
            "Ma nensach hal trajet el bèhi.",
            "Bravo 3lik, karhba dima ndhifa.",
            "Yesser dho7k w jaw, ya3tik essa7a.",
            "Ensan raw3a w karhba comfortable.",
            "Trajet calme w mrigel.",
            "A7sen chauffeur fi Rizervi.",
            "Rabi y-a3tik ma tetmana.",
            "Kol chay kima fel description.",
            "M-rigel dima fel wa9t."
        };

        String[] reviewsFr = {
            "Excellent trajet, chauffeur très ponctuel.",
            "Voiture très propre et conduite sécurisée.",
            "Vraiment sympa, je recommande vivement.",
            "Trajet parfait, rien à dire.",
            "Chauffeur professionnel et très poli.",
            "Conduite souple et ambiance agréable.",
            "Très bonne expérience, à refaire.",
            "Ponctuel et efficace, merci beaucoup.",
            "Le meilleur chauffeur sur cet itinéraire.",
            "Voiture confortable et climatisée."
        };

        String[] reviewsEn = {
            "Great ride, very professional driver.",
            "Smooth driving and nice conversation.",
            "Perfect timing, highly recommended!",
            "Clean car and very safe driving.",
            "Awesome experience, thanks a lot.",
            "Reliable and friendly, 5 stars!",
            "The ride was very comfortable.",
            "Best carpooling experience so far.",
            "On time and very polite.",
            "Very safe and efficient trip."
        };

        String[] colors = {"#007AFF", "#34C759", "#5856D6", "#AF52DE", "#FF2D55", "#FF9500", "#5AC8FA"};
        Random rand = new Random();
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        int numberOfReviews = rand.nextInt(6) + 1; // Entre 1 et 6 avis

        for (int i = 1; i <= numberOfReviews; i++) {
            String reviewer = names[rand.nextInt(names.length)];
            String content = "";
            int type = rand.nextInt(10);
            if (type < 7) content = reviewsTounsi[rand.nextInt(reviewsTounsi.length)]; // 70% Tounsi
            else if (type < 9) content = reviewsFr[rand.nextInt(reviewsFr.length)];    // 20% Français
            else content = reviewsEn[rand.nextInt(reviewsEn.length)];                 // 10% Anglais

            String fullLine = i + ". " + reviewer + ": " + content + (i == numberOfReviews ? "" : "\n\n");
            int start = ssb.length() + String.valueOf(i).length() + 2;
            int end = start + reviewer.length();
            
            ssb.append(fullLine);
            

            String color = colors[rand.nextInt(colors.length)];
            ssb.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        tv.setText(ssb);
    }
}
