package com.example.rizervi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Rizervi_v10.db";
    public static final int DATABASE_VERSION = 10;

    public static final String TABLE_RIDES = "rides";
    public static final String COL_RIDE_ID = "ID";
    public static final String COL_DRIVER_NAME = "DRIVER_NAME";
    public static final String COL_DEPARTURE = "DEPARTURE";
    public static final String COL_DESTINATION = "DESTINATION";
    public static final String COL_TIME = "DEPARTURE_TIME";
    public static final String COL_DATE = "DEPARTURE_DATE";
    public static final String COL_CAR_BRAND = "CAR_BRAND";
    public static final String COL_PRICE = "PRICE";
    public static final String COL_LAT = "LATITUDE";
    public static final String COL_LNG = "LONGITUDE";
    public static final String COL_RATING = "RATING";
    public static final String COL_SEATS = "AVAILABLE_SEATS";

    public static final String TABLE_RESERVATIONS = "reservations";
    public static final String COL_RES_ID = "ID";
    public static final String COL_RES_RIDE_ID = "RIDE_ID";
    public static final String COL_USER_NAME = "USER_NAME";

    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "ID";
    public static final String COL_USERNAME = "USERNAME";
    public static final String COL_PASSWORD = "PASSWORD";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRidesTable = "CREATE TABLE " + TABLE_RIDES + " (" +
                COL_RIDE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DRIVER_NAME + " TEXT, " +
                COL_DEPARTURE + " TEXT, " +
                COL_DESTINATION + " TEXT, " +
                COL_TIME + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_CAR_BRAND + " TEXT, " +
                COL_PRICE + " REAL, " +
                COL_LAT + " REAL, " +
                COL_LNG + " REAL, " +
                COL_RATING + " REAL, " +
                COL_SEATS + " INTEGER)";
        
        String createReservationsTable = "CREATE TABLE " + TABLE_RESERVATIONS + " (" +
                COL_RES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RES_RIDE_ID + " INTEGER, " +
                COL_USER_NAME + " TEXT, " +
                "FOREIGN KEY(" + COL_RES_RIDE_ID + ") REFERENCES " + TABLE_RIDES + "(" + COL_RIDE_ID + "))";

        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)";

        db.execSQL(createRidesTable);
        db.execSQL(createReservationsTable);
        db.execSQL(createUsersTable);

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        String d1 = "2025-05-20";
        String d2 = "2025-05-21";
        String d3 = "2025-05-22";
        String d4 = "2025-05-23";
        String d5 = "2025-05-24";
        String d6 = "2025-05-25";
        String d7 = "2025-05-26";
        String d8 = "2025-05-27";
        String d9 = "2025-05-28";
        String d10 = "2025-05-29";
        
        insertRide(db, "Sonia", "Sfax", "Tunis", "06:00", d1, "Clio 4", 25.0, 34.7406, 10.7603, 4.8, 2);
        insertRide(db, "Mohamed", "Sfax", "Tunis", "14:30", d2, "Kia Rio", 25.0, 34.7406, 10.7603, 4.6, 2);
        insertRide(db, "Ines", "Sfax", "Monastir", "08:15", d4, "Citroën C3", 15.0, 34.7406, 10.7603, 4.3, 2);
        insertRide(db, "Amine", "Tunis", "Sfax", "05:30", d6, "Volkswagen Golf 7", 25.0, 36.8065, 10.1815, 4.7, 2);
        insertRide(db, "Nour", "Monastir", "Sfax", "15:00", d10, "Nissan Micra", 15.0, 35.7643, 10.8113, 4.4, 2);
        insertRide(db, "Khaled", "Tunis", "Sfax", "16:00", d1, "Volkswagen Passat", 25.0, 36.8065, 10.1815, 4.9, 2);
        insertRide(db, "Ahmed", "Sousse", "Tunis", "08:00", d1, "Polo 7", 15.0, 35.8256, 10.6084, 4.5, 2);
        insertRide(db, "Faten", "Sousse", "Sfax", "13:45", d2, "Ford Fiesta", 12.0, 35.8256, 10.6084, 4.2, 2);
        insertRide(db, "Wael", "Mahdia", "Sfax", "07:00", d5, "Citroën C3", 10.0, 35.5042, 11.0622, 4.2, 2);
        insertRide(db, "Hassen", "Mahdia", "Tunis", "05:15", d6, "Skoda Octavia", 22.0, 35.5042, 11.0622, 4.5, 2);
        insertRide(db, "Zied", "Monastir", "Tunis", "06:30", d9, "Seat Ibiza", 18.0, 35.7643, 10.8113, 4.1, 2);
        insertRide(db, "Rim", "Sfax", "Sousse", "10:30", d2, "Renault Clio", 12.0, 34.7406, 10.7603, 4.5, 2);
        insertRide(db, "Omar", "Sousse", "Monastir", "19:00", d3, "Peugeot 208", 5.0, 35.8256, 10.6084, 4.2, 2);
        insertRide(db, "Salma", "Monastir", "Tunis", "05:45", d4, "Citroën C4", 18.0, 35.7643, 10.8113, 4.7, 2);
        insertRide(db, "Hamza", "Tunis", "Sousse", "15:20", d5, "Kia Sportage", 15.0, 36.8065, 10.1815, 4.8, 2);

        insertRide(db, "Youssef", "Sfax", "Sousse", "07:00", d3, "Polo 8", 12.0, 34.7406, 10.7603, 4.9, 3);
        insertRide(db, "Walid", "Sfax", "Mahdia", "16:45", d5, "Dacia Duster", 10.0, 34.7406, 10.7603, 4.1, 3);
        insertRide(db, "Sarah", "Tunis", "Sousse", "09:00", d7, "Fiat 500", 15.0, 36.8065, 10.1815, 4.5, 3);
        insertRide(db, "Oussama", "Tunis", "Bizerte", "17:15", d8, "Renault Symbol", 8.0, 36.8065, 10.1815, 4.2, 3);
        insertRide(db, "Meryem", "Tunis", "Nabeul", "08:30", d9, "Hyundai i20", 10.0, 36.8065, 10.1815, 4.8, 3);
        insertRide(db, "Hedi", "Tunis", "Monastir", "11:00", d10, "Peugeot 308", 18.0, 36.8065, 10.1815, 4.4, 3);
        insertRide(db, "Sami", "Sousse", "Mahdia", "18:30", d3, "Toyota Yaris", 7.0, 35.8256, 10.6084, 4.6, 3);

        insertRide(db, "Rania", "Sousse", "Monastir", "07:30", d4, "Kia Picanto", 5.0, 35.8256, 10.6084, 4.9, 1);
        insertRide(db, "Hajer", "Monastir", "Sousse", "07:45", d8, "Fiat 500", 5.0, 35.7643, 10.8113, 4.8, 1);
    }

    public void insertRidePublic(String driver, String dep, String dest, String time, String date, String car, double price, double lat, double lng, double rating, int seats) {
        SQLiteDatabase db = this.getWritableDatabase();
        insertRide(db, driver, dep, dest, time, date, car, price, lat, lng, rating, seats);
    }

    private void insertRide(SQLiteDatabase db, String driver, String dep, String dest, String time, String date, String car, double price, double lat, double lng, double rating, int seats) {
        ContentValues cv = new ContentValues();
        cv.put(COL_DRIVER_NAME, driver);
        cv.put(COL_DEPARTURE, dep);
        cv.put(COL_DESTINATION, dest);
        cv.put(COL_TIME, time);
        cv.put(COL_DATE, date);
        cv.put(COL_CAR_BRAND, car);
        cv.put(COL_PRICE, price);
        cv.put(COL_LAT, lat);
        cv.put(COL_LNG, lng);
        cv.put(COL_RATING, rating);
        cv.put(COL_SEATS, seats);
        db.insert(TABLE_RIDES, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDES);
        onCreate(db);
    }

    public Cursor getAllRides() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RIDES + " WHERE " + COL_SEATS + " > 0", null);
    }

    public boolean insertReservation(int rideId, String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_SEATS + " FROM " + TABLE_RIDES + " WHERE " + COL_RIDE_ID + "=?", new String[]{String.valueOf(rideId)});
        if (cursor.moveToFirst()) {
            int currentSeats = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEATS));
            if (currentSeats > 0) {
                db.execSQL("UPDATE " + TABLE_RIDES + " SET " + COL_SEATS + " = " + COL_SEATS + " - 1 WHERE " + COL_RIDE_ID + "=?", new String[]{String.valueOf(rideId)});
                ContentValues cv = new ContentValues();
                cv.put(COL_RES_RIDE_ID, rideId);
                cv.put(COL_USER_NAME, userName);
                long result = db.insert(TABLE_RESERVATIONS, null, cv);
                cursor.close();
                return result != -1;
            }
        }
        cursor.close();
        return false;
    }

    public boolean hasUserReserved(int rideId, String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RESERVATIONS + " WHERE " + COL_RES_RIDE_ID + "=? AND " + COL_USER_NAME + "=?", 
                new String[]{String.valueOf(rideId), userName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public int getUserTotalReservations(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RESERVATIONS + " WHERE " + COL_USER_NAME + "=?", 
                new String[]{userName});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public void resetData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDES);
        onCreate(db);
    }
}
