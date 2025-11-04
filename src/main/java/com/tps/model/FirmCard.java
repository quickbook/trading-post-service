package com.tps.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="firm_card")
@Data
@EqualsAndHashCode(exclude = {"platforms", "challenges", "assets"})
@EntityListeners(AuditingEntityListener.class) 
public class FirmCard {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id") 
	private Long id;
	
	@Column(nullable=false)
	private BigDecimal account;
	
	@Column(name="all_ratings")
	private Integer allRatings;
	
	@Column(nullable=false)
	private String code;
	
	private String country;
	
	private String flag;
	
	private String logo;
	
	@Column(name="max_allocation", nullable=false)
	private BigDecimal maxAllocation;

	@Column(name="profit_split") 
	private Integer profitSplit;
	
	private String rating;

	@Column(nullable=false)
	private String title;
	
	@Column(nullable=false)
	private boolean updated;
	
	@CreatedDate
	@Column(name = "created_date", updatable = false)
	private Instant createdDate;

	@LastModifiedDate
	@Column(name = "updated_date")
	private Instant updatedDate;

	@Column(name = "created_by")
	private Long createdBy;
	@Column(name = "updated_by")
	private Long updatedBy;
	
	@OneToMany(mappedBy = "firmCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Challenge> challenges = new HashSet<>();
	
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "firm_assets", joinColumns = @JoinColumn(name = "firm_card_id"))
	@Column(name = "asset")
	private Set<String> assets = new HashSet<>();

	
	@OneToMany(mappedBy = "firmCard", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Platform> platforms = new HashSet<>();	
}