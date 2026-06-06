package com.example.rizervi;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddRideActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextInputEditText etDep, etDest, etDate, etTime, etSeats, etPrice;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private User currentUserProfile;
    private GoogleMap mMap;

    private LatLng departureLatLng, destinationLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ride);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etDep = findViewById(R.id.etDeparture);
        etDest = findViewById(R.id.etDestination);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etSeats = findViewById(R.id.etSeats);
        etPrice = findViewById(R.id.etPrice);
        Button btnPublish = findViewById(R.id.btnPublishRide);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fetchUserProfile();

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());

        btnPublish.setOnClickListener(v -> handlePublish());

        setupBottomNav();
    }

    private void setupBottomNav() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_add_ride);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                finish();
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_add_ride) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new android.content.Intent(this, ProfileActivity.class));
                finish();
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng tunisia = new LatLng(34.0, 9.0);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tunisia, 6));

        mMap.setOnMapLongClickListener(latLng -> {
            if (departureLatLng == null) {
                departureLatLng = latLng;
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Départ")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                etDep.setText(getAddressFromLatLng(latLng));
            } else if (destinationLatLng == null) {
                destinationLatLng = latLng;
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                etDest.setText(getAddressFromLatLng(latLng));
            } else {
                // Reset selection
                departureLatLng = latLng;
                destinationLatLng = null;
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Départ")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                etDep.setText(getAddressFromLatLng(latLng));
                etDest.setText("");
            }
        });
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latLng.latitude + ", " + latLng.longitude;
    }

    private void fetchUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(doc -> currentUserProfile = doc.toObject(User.class));
        }
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            etDate.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            etTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void handlePublish() {
        if (currentUserProfile == null) {
            Toast.makeText(this, "Chargement du profil...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!currentUserProfile.isHasCar()) {
            Toast.makeText(this, "Désolé, vous devez avoir une voiture.", Toast.LENGTH_LONG).show();
            return;
        }

        String dep = etDep.getText().toString().trim();
        String dest = etDest.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String seatsStr = etSeats.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (dep.isEmpty() || dest.isEmpty() || date.isEmpty() || time.isEmpty() || seatsStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        int seats = Integer.parseInt(seatsStr);
        if (seats > 3) {
            Toast.makeText(this, "Le maximum de places autorisées est 3", Toast.LENGTH_LONG).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        Ride newRide = new Ride(
                null, 
                currentUserProfile.getUsername(),
                dep, dest, time, date, 
                currentUserProfile.getCarBrand(),
                price, 
                departureLatLng.latitude, 
                departureLatLng.longitude, 
                5.0, seats
        );
        
        // Save UID of the driver for propositions filter
        newRide.setDriverId(mAuth.getUid());

        db.collection("rides").add(newRide)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Trajet publié avec succès !", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
