package com.example.rizervi;

import com.google.firebase.firestore.DocumentId;

public class Ride {
    @DocumentId
    private String id;
    private String driverName;
    private String departure;
    private String destination;
    private String time;
    private String date;
    private String carBrand;
    private double price;
    private double latitude;
    private double longitude;
    private double rating;
    private int availableSeats;
    private String driverId;

    // Required for Firestore
    public Ride() {}

    public Ride(String id, String driverName, String departure, String destination, String time, String date, String carBrand, double price, double latitude, double longitude, double rating, int availableSeats) {
        this.id = id;
        this.driverName = driverName;
        this.departure = departure;
        this.destination = destination;
        this.time = time;
        this.date = date;
        this.carBrand = carBrand;
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.availableSeats = availableSeats;
    }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCarBrand() { return carBrand; }
    public void setCarBrand(String carBrand) { this.carBrand = carBrand; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
}
