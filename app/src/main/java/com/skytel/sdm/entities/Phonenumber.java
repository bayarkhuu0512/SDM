package com.skytel.sdp.entities;

/**
 * Created by Altanchimeg on 6/16/2016.
 */

public class Phonenumber {
    private int id;
    private String phoneNumber;
    private String priceType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }
}
