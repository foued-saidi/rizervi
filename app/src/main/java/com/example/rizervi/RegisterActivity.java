package com.example.rizervi;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.location.Address;
import android.location.Geocoder;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private TextInputEditText etFirstName, etLastName, etUsername, etAddress, etEmail, etPassword, etCarBrand;
    private SwitchMaterial switchHasCar;
    private LinearLayout layoutCarDetails;
    private ImageView ivProfilePhoto, ivCarPhotoPreview;
    private Button btnRegister, btnUploadCarPhoto;

    private Uri profileImageUri, carImageUri;
    private boolean isPickingProfile = true;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    if (isPickingProfile) {
                        profileImageUri = uri;
                        ivProfilePhoto.setImageURI(uri);
                        ivProfilePhoto.setPadding(0,0,0,0);
                    } else {
                        carImageUri = uri;
                        ivCarPhotoPreview.setImageURI(uri);
                        ivCarPhotoPreview.setVisibility(View.VISIBLE);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        initUI();

        switchHasCar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutCarDetails.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        ivProfilePhoto.setOnClickListener(v -> {
            isPickingProfile = true;
            mGetContent.launch("image/*");
        });

        btnUploadCarPhoto.setOnClickListener(v -> {
            isPickingProfile = false;
            mGetContent.launch("image/*");
        });

        btnRegister.setOnClickListener(v -> handleRegistration());

        findViewById(R.id.tvBackToLogin).setOnClickListener(v -> finish());
    }

    private void initUI() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etUsername = findViewById(R.id.etUsername);
        etAddress = findViewById(R.id.etAddress);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etCarBrand = findViewById(R.id.etCarBrand);
        switchHasCar = findViewById(R.id.switchHasCar);
        layoutCarDetails = findViewById(R.id.layoutCarDetails);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        ivCarPhotoPreview = findViewById(R.id.ivCarPhotoPreview);
        btnRegister = findViewById(R.id.btnRegister);
        btnUploadCarPhoto = findViewById(R.id.btnUploadCarPhoto);
    }

    private void handleRegistration() {
        String fName = etFirstName.getText().toString().trim();
        String lName = etLastName.getText().toString().trim();
        String user = etUsername.getText().toString().trim();
        String addr = etAddress.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        boolean hasCar = switchHasCar.isChecked();
        String carBrand = etCarBrand.getText().toString().trim();

        if (fName.isEmpty() || lName.isEmpty() || user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        if (hasCar && carBrand.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer la marque de votre voiture", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Address
        validateAddressAndRegister(addr, fName, lName, user, email, pass, hasCar, carBrand);
    }

    private void validateAddressAndRegister(String addr, String fName, String lName, String user, String email, String pass, boolean hasCar, String carBrand) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(addr, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address validatedAddress = addresses.get(0);
                String fullAddress = validatedAddress.getAddressLine(0);
                
                // Continue with registration using the validated full address
                registerWithFirebase(fullAddress, fName, lName, user, email, pass, hasCar, carBrand);
            } else {
                Toast.makeText(this, "Adresse introuvable. Veuillez entrer une adresse réelle (ex: Avenue Habib Bourguiba, Tunis)", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            // Geocoder might fail if no internet or service unavailable
            Toast.makeText(this, "Erreur de validation d'adresse. Vérifiez votre connexion.", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerWithFirebase(String validatedAddr, String fName, String lName, String user, String email, String pass, boolean hasCar, String carBrand) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            uploadPhotosAndSaveUser(firebaseUser, fName, lName, user, validatedAddr, hasCar, carBrand);
                        }
                    } else {
                        Toast.makeText(this, "Erreur: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void uploadPhotosAndSaveUser(FirebaseUser firebaseUser, String fName, String lName, String username, String addr, boolean hasCar, String carBrand) {
        User newUser = new User(firebaseUser.getUid(), fName, lName, username, addr, firebaseUser.getEmail(), hasCar);
        if (hasCar) newUser.setCarBrand(carBrand);

        // This is a simplified sequential upload. In production, consider Task.whenAllSuccess
        if (profileImageUri != null) {
            uploadImage(profileImageUri, "profiles/" + firebaseUser.getUid(), url -> {
                newUser.setProfilePhotoUrl(url);
                if (hasCar && carImageUri != null) {
                    uploadImage(carImageUri, "cars/" + UUID.randomUUID().toString(), carUrl -> {
                        newUser.setCarPhotoUrl(carUrl);
                        saveUserToFirestore(newUser, firebaseUser);
                    });
                } else {
                    saveUserToFirestore(newUser, firebaseUser);
                }
            });
        } else if (hasCar && carImageUri != null) {
            uploadImage(carImageUri, "cars/" + UUID.randomUUID().toString(), carUrl -> {
                newUser.setCarPhotoUrl(carUrl);
                saveUserToFirestore(newUser, firebaseUser);
            });
        } else {
            saveUserToFirestore(newUser, firebaseUser);
        }
    }

    private void uploadImage(Uri uri, String path, OnUploadSuccessListener listener) {
        StorageReference ref = storage.getReference().child(path);
        ref.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(url -> listener.onSuccess(url.toString()));
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveUserToFirestore(User user, FirebaseUser firebaseUser) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getUsername())
                .build();
        firebaseUser.updateProfile(profileUpdates);

        db.collection("users").document(firebaseUser.getUid()).set(user)
                .addOnSuccessListener(aVoid -> {
                    firebaseUser.sendEmailVerification();
                    showVerificationDialog(user.getEmail());
                });
    }

    private void showVerificationDialog(String email) {
        new AlertDialog.Builder(this)
                .setTitle("Vérification email")
                .setMessage("Un lien de vérification a été envoyé à " + email + ". Veuillez vérifier votre boîte mail avant de vous connecter.")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    interface OnUploadSuccessListener {
        void onSuccess(String url);
    }
}
