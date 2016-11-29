package com.skytel.sdm.entities;


import com.j256.ormlite.field.DatabaseField;
import com.skytel.sdm.enums.PackageTypeEnum;

public class CardType {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = true)
    private String name;

    @DatabaseField(canBeNull = true)
    private String desciption;

    @DatabaseField(canBeNull = true)
    private PackageTypeEnum packageTypeEnum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public PackageTypeEnum getPackageTypeEnum() {
        return packageTypeEnum;
    }

    public void setPackageTypeEnum(PackageTypeEnum packageTypeEnum) {
        this.packageTypeEnum = packageTypeEnum;
    }
}
