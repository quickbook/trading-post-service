package com.tps.cache.events;

import com.tps.enums.EventChangeType;

public class DataChangedEvent {
	private final EventChangeType type;
    private final Long firmId;         // always present for firm/review/challenge
    private final Long reviewId;       // only for reviews
    private final Long challengeId;    // only for challenges

    public DataChangedEvent(
    		EventChangeType type,
            Long firmId,
            Long reviewId,
            Long challengeId) {
        this.type = type;
        this.firmId = firmId;
        this.reviewId = reviewId;
        this.challengeId = challengeId;
    }

    public EventChangeType getType() { return type; }
    public Long getFirmId() { return firmId; }
    public Long getReviewId() { return reviewId; }
    public Long getChallengeId() { return challengeId; }
}
