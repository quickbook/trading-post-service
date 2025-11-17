package com.tps.cache;

public final class CacheNames {
    private CacheNames() {}

    public static final String FIRMS_BY_ID = "firmsById";
    public static final String FIRMS_LIST = "firmsList";
    public static final String FIRMS_FIND_LIST = "firmsFindList";
    public static final String FIRMS_LITE = "firmsLite";
    
    public static final String REVIEWS_BY_FIRM = "reviewsByFirm";
    public static final String REVIEWS_ALL = "reviewsAll";
    public static final String REVIEWS_BY_ID = "reviewsById";
    
    public static final String CHALLENGES_BY_FIRM = "challengesByFirm";
    public static final String CHALLENGES_BY_ID = "challengesById";
    public static final String CHALLENGES_ALL = "challengesAll";
 
 // --- Domain caches (new) ---
    public static final String COUNTRIES = "countries";
    public static final String ROLES = "roles";
    public static final String TRADING_PLATFORMS = "tradingPlatforms";
    public static final String INSTRUMENTS = "instruments";
    public static final String TIERS = "tiers";
    public static final String CHALLENGE_PHASES = "challengePhases";
    public static final String DRAWDOWN_TYPES = "drawdownTypes";
    public static final String PAYOUT_FREQUENCIES = "payoutFrequencies";
    public static final String CURRENCIES = "currencies";
}
