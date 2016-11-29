package com.skytel.sdp.enums;

public enum ErrorCodeEnum {
    ERROR_700(700),
    ERROR_703(703);

    private final int value;

    ErrorCodeEnum(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }

}
