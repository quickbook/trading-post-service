package com.tps.dto;

import java.math.BigDecimal;
import java.util.List;

import com.tps.enums.WithdrawalSpeedEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradingConditionsDto {

    /* Account sizing */
    @Min(value = 1, message = "Maximum account size must be greater than 0")
    private Integer maximumAccountSizeUsd;

    /* Profit split - single percentage (0-100). If you need multiple options, see below. */
    @Min(value = 0, message = "Profit split cannot be negative")
    @Max(value = 100, message = "Profit split cannot exceed 100%")
    private Integer profitSplitPct;

    /* Optional discount code */
    @Pattern(regexp = "^[A-Za-z0-9]{2,20}$", message = "Discount code must be 2–20 alphanumeric characters")
    private String discountCode;

    /* Use enum type for withdrawal speed (not a String). Validate not null when required. */
    @NotNull(message = "Withdrawal speed is required")
    private WithdrawalSpeedEnum withdrawalSpeed;

    /* Key features list (non-empty strings) */
    @Size(max = 20, message = "Maximum of 20 key features allowed")
    private List<
        @NotBlank(message = "Feature cannot be empty")
        @Size(max = 100, message = "Feature must be max 100 characters")
        String
    > keyFeatures;

    /* Trading platforms */
    @NotEmpty(message = "At least one trading platform is required")
    private List<@NotBlank String> tradingPlatforms;

    /* Available assets */
    @NotEmpty(message = "At least one asset is required")
    private List<@NotBlank String> availableAssets;

    /* Spreads & Commission */
    private boolean rawSpreads; // true -> raw spread model
    @DecimalMin(value = "0.0", inclusive = true, message = "Commission per lot must be >= 0")
    @Digits(integer = 10, fraction = 2, message = "Commission must be a valid monetary value")
    private BigDecimal commissionPerLot;    

    /* News trading rule summary (free text or structured) */
    @Size(max = 500, message = "News trading rule description must be <= 500 characters")
    private String newsTradingRule; // e.g., "Allowed only if purchased..."

    /* Profit Split options (structured) */
    @NotNull(message = "ProfitSplitOption speed is required")
    private ProfitSplitOptionDto profitSplitOption;


    /* Payouts */
    @NotEmpty(message = "At least one payout method is required")
    private List<@NotBlank String> payoutMethods; // e.g., ["Rise"]

    @NotEmpty(message = "At least one payout frequency is required")
    private List<@Pattern(regexp = "Bi-Weekly|Monthly|Weekly|Daily",
                         message = "Invalid payout frequency") String> payoutFrequencies;

    /* Daily drawdown calculation method (e.g., "equityOrBalance") and optional dynamic rule */
    @NotBlank(message = "Daily drawdown calculation method is required")
    private String dailyDrawdownCalculation; // e.g., "equityOrBalance"

    /* Prohibited trading strategies */
    @NotEmpty(message = "Prohibited strategies list cannot be empty")
    private List<@NotBlank String> prohibitedStrategies;

    /* Hedging allowed? IP restrictions & device rules */
    private boolean hedgingAllowed;
    private boolean allowMultipleDevices;
    private boolean requireIpConsistency; // true => IP must remain consistent across devices

    /* Consistency rule applied? (your data: not applied) */
    private boolean consistencyRuleApplied;

    /* Scaling plan */
    @Min(value = 1, message = "Scaling criteria days must be at least 1")
    private Integer scalingCriteriaDays; // e.g., 60

    @DecimalMin(value = "0.0", inclusive = true, message = "Max allocation after scaling must be >= 0")
    private BigDecimal maxAllocationAfterScaling; // e.g., 4000000

    @Min(value = 1, message = "Scaling cycle days must be at least 1")
    private Integer scalingCycleDays; // e.g., 60

    @Min(value = 0, message = "Scaling reward percent must be >= 0")
    @Max(value = 100, message = "Scaling reward percent must be <= 100")
    private Integer scalingRewardPct; // e.g., 50

    /* Accessibility / Restricted countries */
    private List<@Pattern(regexp = "^[A-Za-z \\-()]{2,100}$", message = "Invalid country name") String> restrictedCountries;

    /* Customer support */
    @Email(message = "Support email must be valid")
    private String supportEmail;

    private boolean liveChatAvailable;

    @Pattern(regexp = "^(https?://).+", message = "Discord URL must be a valid URL")
    private String discordUrl;

    @Pattern(regexp = "^[+\\d\\-() ]{6,30}$", message = "Phone number seems invalid")
    private String supportPhone;
    
    @NotEmpty(message = "At least one leverage profile must be provided")
    @Valid
    private List<LeverageDto> leverages;
}
