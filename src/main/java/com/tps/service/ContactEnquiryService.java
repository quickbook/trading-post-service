package com.tps.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tps.dto.request.ContactEnquiryRequest;
import com.tps.model.ContactEnquiry;
import com.tps.repository.ContactEnquiryRepository;

@Service
public class ContactEnquiryService {

    private final ContactEnquiryRepository repository;

    public ContactEnquiryService(ContactEnquiryRepository repository) {
        this.repository = repository;
    }

    public ContactEnquiry save(ContactEnquiryRequest request) {

        ContactEnquiry entity = new ContactEnquiry();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setFirm(request.getFirm());
        entity.setWhatsapp(request.getWhatsapp());
        entity.setServices(request.getServices());
        entity.setAboutFirm(request.getAboutFirm());
        entity.setConsent(request.getConsent());
        entity.setCreatedAt(Instant.now());


        return repository.save(entity);
    }
    public List<ContactEnquiry> fetchAll() {  

        return repository.findAllByOrderByCreatedAtDesc();
    }
}

