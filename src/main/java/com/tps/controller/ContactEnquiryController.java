package com.tps.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tps.dto.request.ContactEnquiryRequest;
import com.tps.model.ContactEnquiry;
import com.tps.service.ContactEnquiryService;

@RestController
@RequestMapping("/tradingpost/api/v1/enquiries")
@CrossOrigin // allow React/Angular calls
public class ContactEnquiryController {

    private final ContactEnquiryService service;

    public ContactEnquiryController(ContactEnquiryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ContactEnquiry> create(
            @RequestBody ContactEnquiryRequest request) {

        ContactEnquiry saved = service.save(request);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<ContactEnquiry>> getAll() {

        List<ContactEnquiry> list = service.fetchAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
}
