package com.tps.model;

import java.math.BigDecimal;
import java.util.List;

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


@Entity
@Table(name="firm_card")
@Data
public class FirmCard {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long Id;
	
	@Column(nullable=false)
	private String title;
	
	private int profitSplit;
	
	@Column(nullable=false)
	private BigDecimal account;
	
	@Column(nullable=false)
	private String code;
	
	private String logo;
	
	@Column(nullable=false)
	private boolean updated;
	
	private String rating;
	private int allRatings;
	private String country;
	private String flag;
	
	
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "firm_assets", joinColumns = @JoinColumn(name = "firm_card_id"))
	@Column(name = "asset")
	private List<String> assets;
	
	
	@OneToMany(mappedBy = "firmCard", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Platform> platforms;
	
	@Column(nullable=false)
	private BigDecimal maxAllocation;
	
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "challenge_id", referencedColumnName = "id")
	private Challenge challenge;
}