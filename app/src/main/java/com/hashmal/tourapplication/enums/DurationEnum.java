package com.hashmal.tourapplication.enums;

import java.util.HashMap;
import java.util.Map;

public enum DurationEnum {

    D3N2("3 Days 2 Nights"),
    D1("1 Day"),
    D2N1("2 Days 1 Night");
    private String time;

    DurationEnum(String time) {
        this.time = time    ;
    }

    public String getTime() {
        return time;
    }
}
