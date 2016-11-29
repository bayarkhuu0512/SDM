package com.skytel.sdm.entities;

/**
 * Created by Altanchimeg on 7/8/2016.
 */

public class VasType {

    private int id;
    private String typeName;
    private String pricePost;
    private String pricePostAlt;
    private String pricePre;
    private int vasTypeId;

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

    public String getPricePost() {
        return pricePost;
    }

    public void setPricePost(String pricePost) {
        this.pricePost = pricePost;
    }

    public String getPricePostAlt() {
        return pricePostAlt;
    }

    public void setPricePostAlt(String pricePostAlt) {
        this.pricePostAlt = pricePostAlt;
    }

    public String getPricePre() {
        return pricePre;
    }

    public void setPricePre(String pricePre) {
        this.pricePre = pricePre;
    }

    public int getVasTypeId() {
        return vasTypeId;
    }

    public void setVasTypeId(int vasTypeId) {
        this.vasTypeId = vasTypeId;
    }
}
