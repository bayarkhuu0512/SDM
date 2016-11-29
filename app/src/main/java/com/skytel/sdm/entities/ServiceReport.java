package com.skytel.sdm.entities;

/**
 * Created by Altanchimeg on 7/8/2016.
 */

public class ServiceReport {
    private int id;
    private String date;
    private String orderStatus;
    private String phone;
    private String isActivation;
    private String serviceType;
    private String simcardSerial;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIsActivation() {
        return isActivation;
    }

    public void setIsActivation(String isActivation) {
        this.isActivation = isActivation;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getSimcardSerial() {
        return simcardSerial;
    }

    public void setSimcardSerial(String simcardSerial) {
        this.simcardSerial = simcardSerial;
    }
}
