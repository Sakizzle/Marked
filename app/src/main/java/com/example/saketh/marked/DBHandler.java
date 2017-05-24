package com.example.saketh.marked;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saketh on 2017-03-30.
 */

public class DBHandler extends SQLiteOpenHelper {

    public final static String AUTH_KEY_FCM = "AIzaSyASBd_ghA8ZPpg_wyV_9H6yfFMQoxNVb8k";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

    //Database Version
    private static final int DATABASE_VERSION = 3;

    //Database Name
    private static final String DATABASE_NAME = "Marked.db";

    //Users table name
    private static final String TABLE_USERS = "Users";

    //Cars table name
    private static final String TABLE_CARS = "Cars";

    //Owned_by table name
    private static final String TABLE_OWNED_BY = "Owned_by";

    //Users Table Column names
    private static final String KEY_UID = "Uid";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_NAME = "name";
    private static final String KEY_PASS = "password";
    private static final String KEY_EMAIL = "email";

    //Car Table Column names
    private static final String KEY_CID = "Cid";
    private static final String KEY_MAKE = "make";
    private static final String KEY_MODEL = "model";
    private static final String KEY_COLOUR = "colour";
    private static final String KEY_LICENCE_PLATE = "licence_plate";
    private static final String KEY_CAR_LAT = "latitude";
    private static final String KEY_CAR_LNG = "longitude";
    private static final String KEY_MARKED = "marked";
    private static final String KEY_TOKEN = "token";


    //Owned_by Table Column names
    private static final String KEY_OID = "Oid";

    //Create tables
    private static final String USER_TABLE_CREATE = "create table " + TABLE_USERS +
            "(username text primary key not null , name text not null , password text not null , email text not null)";
    private static final String CARS_TABLE_CREATE = "create table Cars " +
            "(Cid integer primary key autoincrement not null , make text , model text , colour text , licence_plate text , latitude double , longitude double , marked integer , token text);";
    private static final String OWNED_BY_TABLE_CREATE = "create table Owned_by " +
            "(Oid integer primary key autoincrement not null , username text references Users , Cid integer references Cars);"; //do i declare foreign keys here?

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(USER_TABLE_CREATE);
        db.execSQL(CARS_TABLE_CREATE);
        db.execSQL(OWNED_BY_TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OWNED_BY);

        // create new tables
        onCreate(db);

    }

    //***********************
    //*** Insert Data***
    //**********************
    public boolean insertUser (User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("name", user.getName());
        values.put("password", user.getPassword());
        values.put("email", user.getEmail());
        db.insert("Users", null, values);
        return true;
    }

    public User getUser (String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select username, name, password, email from "+ TABLE_USERS + " where " + KEY_USERNAME + "=?";
        User user = new User();
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(username)});

        if (cursor.moveToFirst()) {
            do {
                user.setUsername(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
                user.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASS)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return user;
    }


    public boolean updateUser (String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        db.update("Users", values, KEY_USERNAME + "=?", new String[] {String.valueOf(username)});
        return true;
    }

    public long insertCar (String make, String model, String colour, String licence_plate, double latitude, double longitude, int marked, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("make", make);
        values.put("model", model);
        values.put("colour", colour);
        values.put("licence_plate", licence_plate);
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        values.put("marked", marked);
        values.put("token", token);
        return db.insert("Cars", null, values);
        //return true;
    }

    public int getCarId (String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        //String query = "select Cid from "+ TABLE_OWNED_BY + " where " + KEY_USERNAME + " =?";
        //Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(username)});
        Cursor cursor = db.rawQuery("select Cid from Owned_by where " + KEY_USERNAME + "=?", new String[] {String.valueOf(username)});
        int carId = -1;

        if (cursor.moveToFirst()) {
            do {
                carId = cursor.getInt(cursor.getColumnIndex(KEY_CID));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return carId;
    }

    public Car getCar (int carId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select make, model, colour, licence_plate, latitude, longitude, marked, token from "+ TABLE_CARS + " where " + KEY_CID + "=?";
        Car car = new Car();
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(carId)});

        if (cursor.moveToFirst()) {
            do {
                car.setMake(cursor.getString(cursor.getColumnIndex(KEY_MAKE)));
                car.setModel(cursor.getString(cursor.getColumnIndex(KEY_MODEL)));
                car.setColour(cursor.getString(cursor.getColumnIndex(KEY_COLOUR)));
                car.setLicence_plate(cursor.getString(cursor.getColumnIndex(KEY_LICENCE_PLATE)));
                car.setLatitude(cursor.getDouble(cursor.getColumnIndex(KEY_CAR_LAT)));
                car.setLongitude(cursor.getDouble(cursor.getColumnIndex(KEY_CAR_LNG)));
                car.setMarked(cursor.getInt(cursor.getColumnIndex(KEY_MARKED)));
                car.setToken(cursor.getString(cursor.getColumnIndex(KEY_TOKEN)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return car;
    }

    public List<Car> getAllCars() {
        //array of columns to fetch
        String[] columns = {
                KEY_CID,
                KEY_MAKE,
                KEY_MODEL,
                KEY_COLOUR,
                KEY_LICENCE_PLATE,
                KEY_CAR_LAT,
                KEY_CAR_LNG,
                KEY_MARKED,
                KEY_TOKEN
        };
        List<Car> carList = new ArrayList<Car>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARS, //Table yo query
                columns,    //columns to return
                null,       //columns for the WHERE clause
                null,       //the values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                null);      //the sort order

        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Car car = new Car();
                car.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_CID))));
                car.setMake(cursor.getString(cursor.getColumnIndex(KEY_MAKE)));
                car.setModel(cursor.getString(cursor.getColumnIndex(KEY_MODEL)));
                car.setColour(cursor.getString(cursor.getColumnIndex(KEY_COLOUR)));
                car.setLicence_plate(cursor.getString(cursor.getColumnIndex(KEY_LICENCE_PLATE)));
                car.setLatitude(cursor.getDouble(cursor.getColumnIndex(KEY_CAR_LAT)));
                car.setLongitude(cursor.getDouble(cursor.getColumnIndex(KEY_CAR_LNG)));
                car.setMarked(cursor.getInt(cursor.getColumnIndex(KEY_MARKED)));
                car.setToken(cursor.getString(cursor.getColumnIndex(KEY_TOKEN)));

                // Adding user record to list
                carList.add(car);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return carList;
    }

    public boolean updateCar (int carId, int marked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("marked", marked);
        //this part might give me some shit
        db.update("Cars", values, KEY_CID + "=?", new String[] {String.valueOf(carId)});
        return true;
    }

    public boolean updateCar (int carId, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        //this part might give me some shit
        db.update("Cars", values, KEY_CID + "=?", new String[] {String.valueOf(carId)});
        return true;
    }

    public boolean updateCar (int carId, String make, String model, String colour, String licence) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("make", make);
        values.put("model", model);
        values.put("colour", colour);
        values.put("licence_plate", licence);
        db.update("Cars", values, KEY_CID + "=?", new String[] {String.valueOf(carId)});
        return true;
    }

    public boolean registerToken (int carId, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("token", token);
        db.update("Cars", values, KEY_CID + "=?", new String[] {String.valueOf(carId)});
        return true;
    }

    public boolean insertOwnedBY (String username, int cid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("Cid", cid);
        db.insert("Owned_by", null, values);
        return true;
    }

    //Given a username, returns a password(string) associated with it
    public String searchPass(String uname){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select username, password from "+ TABLE_USERS;
        Cursor cursor = db.rawQuery(query, null);
        String a, b; //where a is username and b is password
        b = "not found";
        if(cursor.moveToFirst()) {
            do{
                a = cursor.getString(0);

                if(a.equals(uname)) {
                    b = cursor.getString(1);
                    break;
                }
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return b;
    }

    //Given a username, returns true if username already exists, false if not exists
    public boolean searchUser(String uname){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select username from "+ TABLE_USERS;
        Cursor cursor = db.rawQuery(query, null);
        String a; //where a is username
        boolean exists = false;
        if(cursor.moveToFirst()) {
            do{
                a = cursor.getString(0);

                if(a.equals(uname)) {
                    exists = true; //username already exists
                    break;
                }
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return exists;
    }

    /*
 * get single todo
 */
/*    public User getUser(int user_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE "
                + KEY_UID + " = " + user_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        User usr = new User();
        usr.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        usr.setNote((c.getString(c.getColumnIndex(KEY_TODO))));
        usr.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

        return usr;
    }*/

    // Method to send Notifications from server to client end.
    // userDeviceIdKey is the device id you will query from your database
    public static void pushFCMNotification(String userDeviceIdKey, String message) throws Exception{

        String authKey = AUTH_KEY_FCM;   // You FCM AUTH key
        String FMCurl = API_URL_FCM;

        URL url = new URL(FMCurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization","key="+authKey);
        conn.setRequestProperty("Content-Type","application/json");

        JSONObject json = new JSONObject();
        json.put("to",userDeviceIdKey.trim());
        JSONObject info = new JSONObject();
        info.put("title", "Marked");   // Notification title
        info.put("body", message); // Notification body
        json.put("notification", info);

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(json.toString());
        wr.flush();
        conn.getInputStream();
    }

}
