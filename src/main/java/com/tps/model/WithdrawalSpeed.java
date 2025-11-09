package com.tps.model;

public enum WithdrawalSpeed {
    WEEKLY("Weekly"),
    BIWEEKLY("Biweekly"),
    MONTHLY("Monthly"),
    ON_REQUEST("On_Request");
    
    private final String value;
    
    WithdrawalSpeed(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}