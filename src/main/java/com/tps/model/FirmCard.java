package com.tps.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.tps.util.FirmStatus;
import com.tps.util.WithdrawalSpeedEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name="firms") 
@Data
@EqualsAndHashCode(exclude = {"platforms", "assets"}) 
@EntityListeners(AuditingEntityListener.class) 
public class FirmCard {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
	private Long id;
	
	@Column(nullable=false, length = 150)
	private String name; 

    @Column(nullable=false, unique = true, length = 150)
    private String slug; 
    
    @Column(length = 255)
    private String website;

	@Column(name="logo_url", length = 255)
	private String logo; 
    
    @Column(name="hq_country", length = 100)
    private String countryCode;
    
    @Column(name="founded_year")
    private Short foundedYear;
    
    @Column(name="is_trusted", nullable=false)
    private Boolean isTrusted = false;

    @Column(name="rating", precision = 3, scale = 2)
	private String rating;
	
	@Column(name="all_ratings")
	private Integer allRatings;
    
    @Column(name="max_account_size_usd")
	private Integer maxAccountSizeUsd; 
	
	@Column(name="profit_split_pct") 
	private Short profitSplit;
    
    @Column(name="discount_code", length = 64)
    private String discountCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name="withdrawal_speed", columnDefinition = "ENUM('WEEKLY','BIWEEKLY','MONTHLY','ON_REQUEST')")
    private WithdrawalSpeedEnum withdrawalSpeed;
    @Column(name="key_features", columnDefinition = "JSON")
    private String keyFeatures; 

    @Column(name="legal_name", length = 200)
    private String legalName;

    @Column(name="registration_no", length = 120)
    private String registrationNo;

    @Column(name="established_date")
    private LocalDate establishedDate;

    @Column(length = 255)
    private String founders;

    @Column(length = 200)
    private String headquarters;

    @Column(length = 200)
    private String jurisdiction;
    
    @Enumerated(EnumType.STRING) 
    @Column(name="firm_status", length = 10, nullable = false)
    private FirmStatus firmStatus = FirmStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name="firm_type", length = 200)
    private String firmType;
    
	@Column(name="buy_url", length = 255)
	private String buyUrl; 
    

	@Column(nullable=false)
	private boolean updated; 
    
    // --- AUDITING FIELDS ---
	@CreatedDate
	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	private Instant updatedAt;

	@Column(name = "created_by")
	private Long createdBy;
	@Column(name = "updated_by")
	private Long updatedBy;
	@Column(name="about_description", columnDefinition = "TEXT")
	private String aboutDescription;

	
     
	
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "firm_instruments", joinColumns = @JoinColumn(name = "firm_id")) 
	@Column(name = "instrument") 
	private Set<String> assets = new HashSet<>(); 

	@OneToMany(mappedBy = "firmCard", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<FirmPlatform> platforms = new HashSet<>();	
    
    // NOTE: The new OneToMany relationship for AccountPlan will be added here later.
}