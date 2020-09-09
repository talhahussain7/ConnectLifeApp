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
    String bloodGroup;
    String docRef;

    public String getImgRef() {
        return imgRef;
    }

    public void setImgRef(String imgRef) {
        this.imgRef = imgRef;
    }

    String imgRef;

    public String getDocRef() {
        return docRef;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }

    //Address address;
    //List<BloodRequest> list;
    //String status;


    public User(String id, String name, String city, String country, LatLng coordinates, String dob, String phoneNumber, String bloodGroup, String docRef, String imgRef) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
        this.coordinates = coordinates;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
        this.bloodGroup = bloodGroup;
        this.docRef = docRef;
        this.imgRef = imgRef;
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

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }
}