package com.skytel.sdp.enums;

import com.skytel.sdp.utils.Constants;

public enum PackageTypeEnum implements Constants{
    COLOR_DATA_PACKAGE(CONST_COLOR_DATA_PACKAGE),
    COLOR_CALL_PACKAGE(CONST_COLOR_CALL_PACKAGE),
    SKYTEL_NODAY_PACKAGE(CONST_SKYTEL_NODAY_PACKAGE),
    SKYTEL_DAY_PACKAGE(CONST_SKYTEL_DAY_PACKAGE),
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
