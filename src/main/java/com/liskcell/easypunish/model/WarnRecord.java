package com.liskcell.easypunish.model;

import java.util.UUID;

public class WarnRecord {
    private final UUID playerUuid;
    private final String playerName;
    private final String type;
    private final String reasonKey;
    private int count;

    public WarnRecord(UUID playerUuid, String playerName, String type, String reasonKey, int count) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.type = type;
        this.reasonKey = reasonKey;
        this.count = count;
    }

    public UUID getPlayerUuid() { return playerUuid; }
    public String getPlayerName() { return playerName; }
    public String getType() { return type; }
    public String getReasonKey() { return reasonKey; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
