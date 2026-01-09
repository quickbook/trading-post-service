package com.tps.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tps.model.ContactEnquiry;

public interface ContactEnquiryRepository
        extends JpaRepository<ContactEnquiry, Long> {
	
	List<ContactEnquiry> findAllByOrderByCreatedAtDesc();
}
