package com.example.rizervi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePic;
    private TextView tvName, tvUsername, tvEmail, tvAddress, tvCar;
    private RecyclerView rvPropositions, rvReservations;
    private RideAdapter propositionsAdapter, reservationsAdapter;
    private List<Ride> propositionsList, reservationsList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initUI();
        setupRecyclerViews();
        loadUserData();
        loadUserHistory();
        setupBottomNav();
    }

    private void initUI() {
        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvName = findViewById(R.id.tvFullName);
        tvUsername = findViewById(R.id.tvUserUsername);
        tvEmail = findViewById(R.id.tvUserEmail);
        tvAddress = findViewById(R.id.tvUserAddress);
        tvCar = findViewById(R.id.tvUserCar);
        rvPropositions = findViewById(R.id.rvMyPropositions);
        rvReservations = findViewById(R.id.rvMyReservations);
        Button btnMessages = findViewById(R.id.btnViewMessages);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnMessages.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ChatListActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void setupRecyclerViews() {
        propositionsList = new ArrayList<>();
        reservationsList = new ArrayList<>();

        // Reuse RideAdapter for simplicity, pass dummy listeners
        propositionsAdapter = new RideAdapter(propositionsList, r -> {}, r -> {});
        propositionsAdapter.setShowBookButton(false);
        
        reservationsAdapter = new RideAdapter(reservationsList, r -> {}, r -> {});
        reservationsAdapter.setShowBookButton(false);

        rvPropositions.setLayoutManager(new LinearLayoutManager(this));
        rvPropositions.setAdapter(propositionsAdapter);

        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        rvReservations.setAdapter(reservationsAdapter);
    }

    private void setupBottomNav() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                finish();
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_add_ride) {
                startActivity(new Intent(this, AddRideActivity.class));
                finish();
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(doc -> {
                        User profile = doc.toObject(User.class);
                        if (profile != null) {
                            tvName.setText(profile.getFirstName() + " " + profile.getLastName());
                            tvUsername.setText("@" + profile.getUsername());
                            tvEmail.setText("Email: " + profile.getEmail());
                            tvAddress.setText("Adresse: " + profile.getAddress());
                            tvCar.setText(profile.isHasCar() ? "Voiture: " + profile.getCarBrand() : "Voiture: Non renseignée");

                            if (profile.getProfilePhotoUrl() != null) {
                                ivProfilePic.setPadding(0, 0, 0, 0);
                                Glide.with(this).load(profile.getProfilePhotoUrl()).into(ivProfilePic);
                            }
                        }
                    });
        }
    }

    private void loadUserHistory() {
        String uid = mAuth.getUid();
        if (uid == null) return;

        // Fetch Propositions (Rides created by this user as driver)
        db.collection("rides").whereEqualTo("driverId", uid).get()
                .addOnSuccessListener(querySnapshot -> {
                    propositionsList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Ride ride = doc.toObject(Ride.class);
                        ride.setId(doc.getId());
                        propositionsList.add(ride);
                    }
                    propositionsAdapter.updateList(propositionsList);
                });

        // Fetch Reservations (Rides where this user is a passenger)
        // First get the username because reservations are currently stored by userName in main.java
        // Better would be by UID, but let's stick to current logic for compatibility
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;
        
        db.collection("users").document(uid).get().addOnSuccessListener(userDoc -> {
            String userName = userDoc.getString("username");
            if (userName == null) return;

            db.collection("reservations").whereEqualTo("userName", userName).get()
                    .addOnSuccessListener(resSnapshot -> {
                        reservationsList.clear();
                        for (QueryDocumentSnapshot resDoc : resSnapshot) {
                            String rideId = resDoc.getString("rideId");
                            if (rideId != null) {
                                db.collection("rides").document(rideId).get().addOnSuccessListener(rideDoc -> {
                                    Ride ride = rideDoc.toObject(Ride.class);
                                    if (ride != null) {
                                        ride.setId(rideDoc.getId());
                                        reservationsList.add(ride);
                                        reservationsAdapter.updateList(reservationsList);
                                    }
                                });
                            }
                        }
                    });
        });
    }
}
