package com.hashmal.tourapplication.enums;

public enum Code {

    SUCCESS("0000","Successfully"),
    FAILED("9999","Fail to do this action"),
    ER0001("ER0001","There is no user registered with the phone number"),
    ER0002("ER0002", "Incorrect password!"),
    T00001("T0001", "This tour is not verified"),
    ER0003("ER0003", "This user already exists"),
    ER0004("ER0004", "Please active your account with code that we have sent to your email."),
    ER0005("ER0005", "Your account have been cancelled."),
    ER0006("ER0006", "Your new password is same as your old one.");


    Code(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    private String defaultMessage;
}
