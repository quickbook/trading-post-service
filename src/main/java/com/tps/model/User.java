package com.tps.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="user_details")
public class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long Id;
	
	@Column(nullable=false,unique=true)
	private String username;
	
	@Column(name="first_name",nullable=false)
	private String firstname;
	
	@Column(name="middle_name",nullable=false)
	private String middlename;
	
	@Column(name="last_name",nullable=false)
	private String lastname;
	
	@Column(name="contact_id",nullable=false)
	private String contactId;
	
	@Column(name="gmail",nullable=false,unique=true)
	private String gmail;
	
	@Column(name="address",nullable=false)
	private String address;
	
	@Column(nullable=false)
	private String password;
	
	
	@ManyToOne
	@JoinColumn(name="role_id")
	private Role role;


	
}
