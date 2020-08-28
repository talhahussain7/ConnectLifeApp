package com.example.connectlife.models;

public class Address {
    String city;
    String Street;
    String Country;
    int lati;
    int longi;


    public Address(String city, String street, String country, int lati, int longi) {
        this.city = city;
        Street = street;
        Country = country;
        this.lati = lati;
        this.longi = longi;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public int getLati() {
        return lati;
    }

    public void setLati(int lati) {
        this.lati = lati;
    }

    public int getLongi() {
        return longi;
    }

    public void setLongi(int longi) {
        this.longi = longi;
    }
}
