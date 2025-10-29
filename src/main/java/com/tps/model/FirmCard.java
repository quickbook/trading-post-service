package com.tps.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="firm_card")
@Data
@EqualsAndHashCode(exclude = {"platforms", "challenge", "assets"})
public class FirmCard {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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

	@Column(name="profit_split") // Example of explicit naming, though often optional
	private Integer profitSplit;
	
	private String rating;

	@Column(nullable=false)
	private String title;
	
	@Column(nullable=false)
	private boolean updated;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "challenge_id", referencedColumnName = "id")
	private Challenge challenge;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "firm_assets", joinColumns = @JoinColumn(name = "firm_card_id"))
	@Column(name = "asset")
	private Set<String> assets = new HashSet<>(); // Use Set and initialize

	/** Change List<Platform> to Set<Platform> */
	@OneToMany(mappedBy = "firmCard", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Platform> platforms = new HashSet<>();	
}