package com.example.rizervi;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class main extends AppCompatActivity {

    private FirebaseFirestore dbFirestore;
    private RecyclerView rvRides;
    private RideAdapter adapter;
    private List<Ride> rideList;
    private List<Ride> filteredList;
    private Button btnSortPrice, btnSortRating, btnSortTime, btnSortDistance, btnSortSeats, btnSortDate;
    private android.widget.TextView tvReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore
        dbFirestore = FirebaseFirestore.getInstance();

        rvRides = findViewById(R.id.rvRides);
        btnSortPrice = findViewById(R.id.btnSortPrice);
        btnSortRating = findViewById(R.id.btnSortRating);
        btnSortTime = findViewById(R.id.btnSortTime);
        btnSortDistance = findViewById(R.id.btnSortDistance);
        btnSortSeats = findViewById(R.id.btnSortSeats);
        btnSortDate = findViewById(R.id.btnSortDate);
        tvReset = findViewById(R.id.tvReset);

        rideList = new ArrayList<>();
        filteredList = new ArrayList<>();
        
        adapter = new RideAdapter(filteredList, ride -> {
            String currentUser = getIntent().getStringExtra("CURRENT_USER");
            if (currentUser == null) currentUser = "Guest";
            
            final String finalUser = currentUser;
            checkAndReserve(ride, finalUser);
        }, ride -> {
            android.content.Intent intent = new android.content.Intent(this, DriverProfileActivity.class);
            intent.putExtra("DRIVER_NAME", ride.getDriverName());
            intent.putExtra("DRIVER_ID", ride.getDriverId());
            intent.putExtra("RATING", ride.getRating());
            intent.putExtra("LAT", ride.getLatitude());
            intent.putExtra("LNG", ride.getLongitude());
            startActivity(intent);
        });

        rvRides.setLayoutManager(new LinearLayoutManager(this));
        rvRides.setAdapter(adapter);

        // Handle Bottom Navigation
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_add_ride) {
                android.content.Intent intent = new android.content.Intent(this, AddRideActivity.class);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return false;
            } else if (id == R.id.nav_profile) {
                android.content.Intent intent = new android.content.Intent(this, ProfileActivity.class);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return false;
            }
            return false;
        });

        loadRidesFromFirestore();

        btnSortPrice.setOnClickListener(v -> {
            Collections.sort(filteredList, Comparator.comparingDouble(Ride::getPrice));
            adapter.updateList(filteredList);
        });

        btnSortRating.setOnClickListener(v -> {
            Collections.sort(filteredList, (r1, r2) -> Double.compare(r2.getRating(), r1.getRating()));
            adapter.updateList(filteredList);
        });

        btnSortTime.setOnClickListener(v -> {
            Collections.sort(filteredList, Comparator.comparing(Ride::getTime));
            adapter.updateList(filteredList);
        });

        btnSortDistance.setOnClickListener(v -> {
            Collections.shuffle(filteredList);
            adapter.updateList(filteredList);
        });

        btnSortSeats.setOnClickListener(v -> {
            Collections.sort(filteredList, (r1, r2) -> Integer.compare(r2.getAvailableSeats(), r1.getAvailableSeats()));
            adapter.updateList(filteredList);
        });

        btnSortDate.setOnClickListener(v -> {
            Collections.sort(filteredList, Comparator.comparing(Ride::getDate).thenComparing(Ride::getTime));
            adapter.updateList(filteredList);
        });

        tvReset.setOnClickListener(v -> {
            // Logic to re-populate sample data to Firestore if needed
            uploadSampleDataToFirestore();
        });
    }

    private void loadRidesFromFirestore() {
        dbFirestore.collection("rides")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Erreur Firestore: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (value != null) {
                        if (value.isEmpty()) {
                            // If empty, suggest clicking Reset or auto-upload
                            uploadSampleDataToFirestore();
                        } else {
                            rideList.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                Ride ride = doc.toObject(Ride.class);
                                ride.setId(doc.getId());
                                if (ride.getAvailableSeats() > 0) {
                                    rideList.add(ride);
                                }
                            }
                            filteredList.clear();
                            filteredList.addAll(rideList);
                            adapter.updateList(filteredList);
                        }
                    }
                });
    }

    private void checkAndReserve(Ride ride, String userName) {
        // Simple reservation for now, can be improved with multi-reservation check
        executeReservationFirestore(ride, userName);
    }

    private void executeReservationFirestore(Ride ride, String userName) {
        final DocumentReference rideRef = dbFirestore.collection("rides").document(ride.getId());

        dbFirestore.runTransaction(transaction -> {
            Ride snapshot = transaction.get(rideRef).toObject(Ride.class);
            if (snapshot != null && snapshot.getAvailableSeats() > 0) {
                transaction.update(rideRef, "availableSeats", snapshot.getAvailableSeats() - 1);
                
                // Add reservation record
                java.util.Map<String, Object> reservation = new java.util.HashMap<>();
                reservation.put("rideId", ride.getId());
                reservation.put("userName", userName);
                reservation.put("timestamp", com.google.firebase.Timestamp.now());
                
                transaction.set(dbFirestore.collection("reservations").document(), reservation);
                return null;
            } else {
                throw new com.google.firebase.firestore.FirebaseFirestoreException("Ride full", 
                        com.google.firebase.firestore.FirebaseFirestoreException.Code.ABORTED);
            }
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Réservation réussie pour " + ride.getDestination(), Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadSampleDataToFirestore() {
        // Check if collection is empty first
        dbFirestore.collection("rides").limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().isEmpty()) {
                List<Ride> samples = new ArrayList<>();
                String d1 = "2025-05-20", d2 = "2025-05-21", d3 = "2025-05-22";
                
                samples.add(new Ride(null, "Sonia", "Sfax", "Tunis", "06:00", d1, "Clio 4", 25.0, 34.7406, 10.7603, 4.8, 2));
                samples.add(new Ride(null, "Ahmed", "Sousse", "Tunis", "08:00", d1, "Polo 7", 15.0, 35.8256, 10.6084, 4.5, 2));
                samples.add(new Ride(null, "Faten", "Sousse", "Sfax", "13:45", d2, "Ford Fiesta", 12.0, 35.8256, 10.6084, 4.2, 2));
                samples.add(new Ride(null, "Youssef", "Sfax", "Sousse", "07:00", d3, "Polo 8", 12.0, 34.7406, 10.7603, 4.9, 3));

                for (Ride r : samples) {
                    dbFirestore.collection("rides").add(r);
                }
                Toast.makeText(this, "Sample data uploaded!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Data already exists or error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
