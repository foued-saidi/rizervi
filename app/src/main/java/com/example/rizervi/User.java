package com.example.rizervi;

public class User {
    private String uid;
    private String firstName;
    private String lastName;
    private String username;
    private String address;
    private String email;
    private boolean hasCar;
    private String carBrand;
    private String profilePhotoUrl;
    private String carPhotoUrl;

    public User() {}

    public User(String uid, String firstName, String lastName, String username, String address, String email, boolean hasCar) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.address = address;
        this.email = email;
        this.hasCar = hasCar;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isHasCar() { return hasCar; }
    public void setHasCar(boolean hasCar) { this.hasCar = hasCar; }

    public String getCarBrand() { return carBrand; }
    public void setCarBrand(String carBrand) { this.carBrand = carBrand; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public String getCarPhotoUrl() { return carPhotoUrl; }
    public void setCarPhotoUrl(String carPhotoUrl) { this.carPhotoUrl = carPhotoUrl; }
}
