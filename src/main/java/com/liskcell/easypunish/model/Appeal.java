package com.liskcell.easypunish.model;

import java.util.UUID;

public class Appeal {
    private final String punishmentId;
    private final UUID playerUuid;
    private final String playerName;
    private final String punishmentType;
    private final String originalReason;
    private final String appealReason;
    private String status; // PENDING, APPROVED, DENIED
    private final long timestamp;

    public Appeal(String punishmentId, UUID playerUuid, String playerName, String punishmentType, String originalReason, String appealReason, String status, long timestamp) {
        this.punishmentId = punishmentId;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.punishmentType = punishmentType;
        this.originalReason = originalReason;
        this.appealReason = appealReason;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getPunishmentId() { return punishmentId; }
    public UUID getPlayerUuid() { return playerUuid; }
    public String getPlayerName() { return playerName; }
    public String getPunishmentType() { return punishmentType; }
    public String getOriginalReason() { return originalReason; }
    public String getAppealReason() { return appealReason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getTimestamp() { return timestamp; }
}
