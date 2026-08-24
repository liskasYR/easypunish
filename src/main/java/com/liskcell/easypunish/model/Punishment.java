package com.liskcell.easypunish.model;

import java.util.UUID;

public class Punishment {
    private final String id;
    private final UUID playerUuid;
    private final String playerName;
    private final String type; // Ban, Chat, VoiceChat
    private final String reasonKey;
    private final String reasonTranslate;
    private final String staffName;
    private long startTime;
    private long durationMs;
    private long remainingMs;
    private boolean active;

    public Punishment(String id, UUID playerUuid, String playerName, String type, String reasonKey, String reasonTranslate, String staffName, long startTime, long durationMs, long remainingMs, boolean active) {
        this.id = id;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.type = type;
        this.reasonKey = reasonKey;
        this.reasonTranslate = reasonTranslate;
        this.staffName = staffName;
        this.startTime = startTime;
        this.durationMs = durationMs;
        this.remainingMs = remainingMs;
        this.active = active;
    }

    public String getId() { return id; }
    public UUID getPlayerUuid() { return playerUuid; }
    public String getPlayerName() { return playerName; }
    public String getType() { return type; }
    public String getReasonKey() { return reasonKey; }
    public String getReasonTranslate() { return reasonTranslate; }
    public String getStaffName() { return staffName; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }
    public long getRemainingMs() { return remainingMs; }
    public void setRemainingMs(long remainingMs) { this.remainingMs = remainingMs; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public long getRemainingTimeMs() {
        if (!active) {
            return remainingMs;
        }
        long elapsed = System.currentTimeMillis() - startTime;
        long left = durationMs - elapsed;
        return Math.max(0L, left);
    }

    public boolean isExpired() {
        if (!active) {
            return false;
        }
        return getRemainingTimeMs() <= 0;
    }
}
