package com.example.saketh.marked;

/**
 * Created by Saketh on 2017-03-30.
 */

public class Car {
    private String make, model, colour, licence_plate, token;
    private int id;
    private double longitude;
    private double latitude;
    private int marked;


    //empty constructor
    public Car() {
    }

    //constructor
    public Car(String make, String model, String colour, String licence_plate) {
        this.make = make;
        this.model = model;
        this.colour = colour;
        this.licence_plate = licence_plate;
    }

    //Getters
    public int getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getColour() {
        return colour;
    }

    public String getLicence_plate() {
        return licence_plate;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getMarked() {
        return marked;
    }

    public String getToken() {return token; }

    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public void setLicence_plate(String licence_plate) {
        this.licence_plate = licence_plate;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setMarked(int marked) {
        this.marked = marked;
    }

    public void setToken(String token) {this.token = token; }
}
