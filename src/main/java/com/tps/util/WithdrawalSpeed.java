package com.tps.util;

public enum WithdrawalSpeed {
	
	DAILY("Daily"), 
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