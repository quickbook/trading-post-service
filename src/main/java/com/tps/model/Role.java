package com.tps.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="role_details")
public class Role {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="role_id")
	private Long id;
	
	@Column(name="role_name",nullable=false)
	private String name;
	
	@Column(name = "role_description", length = 255)
	private String description;
	
	@Column(name = "access_level")
	private Integer accessLevel;
	
	@Column(name = "is_active")
	private Boolean active = true;
	
	

}
