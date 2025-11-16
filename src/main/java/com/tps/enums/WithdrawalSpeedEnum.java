package com.tps.enums;

public enum WithdrawalSpeedEnum {
	
	DAILY("Daily"), 
	WEEKLY("Weekly"), 
	BIWEEKLY("Bi-Weekly"), 
	MONTHLY("Monthly"), 
	ON_REQUEST("On_Request");
    
    private final String value;
    
    WithdrawalSpeedEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}