package com.hashmal.tourapplication.enums;

public enum IntentResult {
    DELETE_PACKAGE(3333),
    ADD_TOUR_REQUEST (1001),
    DETAIL_TOUR_REQUEST (1002),

    REQUEST_PACKAGE_EDIT( 1001),
   REQUEST_TOUR_EDIT (1011);
    private int value;

    IntentResult(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
