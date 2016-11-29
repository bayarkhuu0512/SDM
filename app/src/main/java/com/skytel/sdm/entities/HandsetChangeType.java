package com.skytel.sdp.entities;

/**
 * Created by Altanchimeg on 7/6/2016.
 */

public class HandsetChangeType {
    private int id;
    private String typeName;
    private int price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSimChangeTypeId() {
        return id;
    }

    public void setSimChangeTypeId(int id) {
        this.id = id;
    }
}
