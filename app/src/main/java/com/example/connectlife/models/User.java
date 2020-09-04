package com.example.connectlife.models;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

public class User {
    String id;
    String name;
    String city;
    String country;
    LatLng coordinates;
    String dob;
    String phoneNumber;
    String email;
    //Address address;
    //String bloodGroup;
    //List<BloodRequest> list;
    //String status;


    public User(String id, String name, String city, String country, LatLng coordinates, String dob, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
        this.coordinates = coordinates;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}