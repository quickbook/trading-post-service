package com.tps.cache.events;

public class FirmChangedEvent {
    private final Long firmId;
    public FirmChangedEvent(Long firmId) { this.firmId = firmId; }
    public Long getFirmId() { return firmId; }
}
