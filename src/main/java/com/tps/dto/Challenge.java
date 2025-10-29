package com.tps.dto;

import java.util.List;

import lombok.Data;

@Data
public class Challenge {
	
	 private String name;
	    private List<Phase> phases;
	    private Integer maxDailyLossPct;
	    private Integer maxOverallLossPct;
}
