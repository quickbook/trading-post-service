package com.tps.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.tps.enums.FirmStatus;
import com.tps.enums.WithdrawalSpeedEnum;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "firms")
@Data
@EqualsAndHashCode(exclude = {"platforms", "assets", "leverages"})
@EntityListeners(AuditingEntityListener.class)
public class FirmCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @Column(length = 255)
    private String website;

    @Column(name = "logo_url", length = 255)
    private String logo;

    @Column(name = "hq_country", length = 100)
    private String countryCode;

    @Column(name = "founded_year")
    private Short foundedYear;

    @Column(name = "is_trusted", nullable = false)
    private Boolean isTrusted = false;

    @Column(name = "rating", length = 8)
    private String rating;

    @Column(name = "all_ratings")
    private Integer allRatings;

    /* Account size & basic profit split (legacy single-value) */
    @Column(name = "max_account_size_usd")
    private Integer maxAccountSizeUsd;

    @Column(name = "profit_split_pct")
    private Short profitSplit;

    @Column(name = "discount_code", length = 64)
    private String discountCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "withdrawal_speed", length = 20)
    private WithdrawalSpeedEnum withdrawalSpeed;

    /* Key features stored as JSON text for quick viewing (if you prefer a normalized table, change to ElementCollection) */
    @Column(name = "key_features", columnDefinition = "JSON")
    private String keyFeatures;

    /* About & legal */
    @Column(name = "legal_name", length = 200)
    private String legalName;

    @Column(name = "registration_no", length = 120)
    private String registrationNo;

    @Column(name = "established_date")
    private LocalDate establishedDate;

    @Column(length = 255)
    private String founders;

    @Column(length = 200)
    private String headquarters;

    @Column(length = 200)
    private String jurisdiction;

    @Enumerated(EnumType.STRING)
    @Column(name = "firm_status", length = 20, nullable = false)
    private FirmStatus firmStatus = FirmStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "firm_type", length = 200)
    private String firmType;

    @Column(name = "buy_url", length = 255)
    private String buyUrl;

    @Column(nullable = false)
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

    @Column(name = "about_description", columnDefinition = "TEXT")
    private String aboutDescription;

    /* ---------- Collections & relationships ---------- */

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "firm_instruments", joinColumns = @JoinColumn(name = "firm_id"))
    @Column(name = "instrument")
    private Set<String> assets = new HashSet<>();

    @OneToMany(mappedBy = "firmCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FirmPlatform> platforms = new HashSet<>();

    /**
     * Leverages are stored in a separate table `firm_leverages` (one row per
     * firm/profile/instrument). See FirmLeverage entity below.
     */
    @OneToMany(mappedBy = "firmCard", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<FirmLeverage> leverages = new HashSet<>();

    /* Spreads & commission */
    @Column(name = "raw_spreads", nullable = false)
    private boolean rawSpreads;

    @Column(name = "commission_per_lot", precision = 12, scale = 2)
    private BigDecimal commissionPerLot;

    /* News trading rule summary */
    @Column(name = "news_trading_rule", length = 1000)
    private String newsTradingRule;

    /* Payouts */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "firm_payout_methods", joinColumns = @JoinColumn(name = "firm_id"))
    @Column(name = "payout_method")
    private Set<String> payoutMethods = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "firm_payout_frequencies", joinColumns = @JoinColumn(name = "firm_id"))
    @Column(name = "payout_frequency")
    private Set<String> payoutFrequencies = new HashSet<>();

    /* Daily drawdown */
    @Column(name = "daily_drawdown_calc_method", length = 100)
    private String dailyDrawdownCalculation; // e.g., "equityOrBalance"

    /* Prohibited strategies */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "firm_prohibited_strategies", joinColumns = @JoinColumn(name = "firm_id"))
    @Column(name = "strategy")
    private Set<String> prohibitedStrategies = new HashSet<>();

    /* IP / device rules */
    @Column(name = "allow_multiple_devices")
    private boolean allowMultipleDevices;

    @Column(name = "require_ip_consistency")
    private boolean requireIpConsistency;

    /* Consistency rule flag (true if applied) */
    @Column(name = "consistency_rule_applied")
    private boolean consistencyRuleApplied;

    /* Scaling plan */
    @Column(name = "scaling_criteria_days")
    private Integer scalingCriteriaDays;

    @Column(name = "max_allocation_after_scaling", precision = 19, scale = 2)
    private BigDecimal maxAllocationAfterScaling;

    @Column(name = "scaling_cycle_days")
    private Integer scalingCycleDays;

    @Column(name = "scaling_reward_pct")
    private Integer scalingRewardPct;

    /* Accessibility / restricted countries */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "firm_restricted_countries", joinColumns = @JoinColumn(name = "firm_id"))
    @Column(name = "country")
    private Set<String> restrictedCountries = new HashSet<>();

    /* Customer support */
    @Column(name = "support_email", length = 150)
    private String supportEmail;

    @Column(name = "live_chat_available")
    private boolean liveChatAvailable;

    @Column(name = "discord_url", length = 255)
    private String discordUrl;

    @Column(name = "support_phone", length = 50)
    private String supportPhone;

    @Column(name = "profit_split_option", columnDefinition = "JSON")
    private String profitSplitOptionJson;

    
    // NOTE: The new OneToMany relationship for AccountPlan will be added here later.
}
