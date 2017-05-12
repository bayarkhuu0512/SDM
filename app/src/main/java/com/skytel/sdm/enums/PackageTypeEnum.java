package com.skytel.sdm.enums;

import com.skytel.sdm.utils.Constants;

public enum PackageTypeEnum implements Constants{
    SKYTEL_CARD_PACKAGE(CONST_SKYTEL_CARD_PACKAGE),
    SKYTEL_DATA_PACKAGE(CONST_SKYTEL_DATA_PACKAGE),
    SKYMEDIA_IP76_PACKAGE(CONST_SKYMEDIA_IP76_PACKAGE),
    SMART_PACKAGE(CONST_SMART_PACKAGE);

    private final int value;

    PackageTypeEnum(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
