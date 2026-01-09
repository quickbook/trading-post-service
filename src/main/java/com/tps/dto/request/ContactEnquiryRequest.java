package com.tps.dto.request;

import java.math.BigDecimal;

import com.tps.dto.PriceDto;

import lombok.Data;

@Data
public class ContactEnquiryRequest {

    private String name;
    private String email;
    private String firm;
    private String whatsapp;
    private String services;
    private String aboutFirm;
    private Boolean consent;

    // getters & setters
}
