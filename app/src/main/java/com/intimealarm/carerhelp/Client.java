package com.intimealarm.carerhelp;

import com.google.android.gms.maps.model.LatLng;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 27/11/2016.
 */

public class Client {
    // Variables
    LatLng geo;

    private String name, address, phone, time, client_id;

    // Constructors
    public Client() {
    }

    public Client(String client_id, String name, String address, String phone, String time, LatLng geo) {
        this.client_id = client_id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.time = time;
        this.geo = geo;


    }

    // Getters and Setters
    public LatLng getGeo() {
        return geo;
    }

    public void setGeo(LatLng geo) {
        this.geo = geo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
