package com.skytel.sdp.entities;

/**
 * Created by Altanchimeg on 6/30/2016.
 */

public class NewNumberReport {
    private int id;
    private String serviceType;
    private String numberType;
    private String number;
    private String unitAndDay;
    private String price;
    private String orderState;
    private String date;
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getNumberType() {
        return numberType;
    }

    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUnitAndDay() {
        return unitAndDay;
    }

    public void setUnitAndDay(String unitAndDay) {
        this.unitAndDay = unitAndDay;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
