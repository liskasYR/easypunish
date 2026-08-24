package com.liskcell.easypunish.manager;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.model.Punishment;
import com.liskcell.easypunish.util.ColorUtil;
import com.liskcell.easypunish.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PunishmentManager {
    private final EasyPunish plugin;
    private final File dataFolder;
    private final File dataFile;
    private final File historyFile;
    private FileConfiguration dataConfig;
    private FileConfiguration historyConfig;

    private final Map<String, Punishment> activePunishments = new HashMap<>();
    private final Map<String, Punishment> historyPunishments = new HashMap<>();

    public PunishmentManager(EasyPunish plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.dataFile = new File(dataFolder, "active_punishments.yml");
        this.historyFile = new File(dataFolder, "history_punishments.yml");
        loadData();
    }

    public void loadData() {
        try {
            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }
            if (!historyFile.exists()) {
                historyFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        historyConfig = YamlConfiguration.loadConfiguration(historyFile);

        activePunishments.clear();
        historyPunishments.clear();

        if (dataConfig.contains("punishments")) {
            for (String id : dataConfig.getConfigurationSection("punishments").getKeys(false)) {
                String path = "punishments." + id + ".";
                UUID uuid = UUID.fromString(dataConfig.getString(path + "uuid"));
                String name = dataConfig.getString(path + "name");
                String type = dataConfig.getString(path + "type");
                String reasonKey = dataConfig.getString(path + "reasonKey");
                String reasonTranslate = dataConfig.getString(path + "reasonTranslate");
                String staff = dataConfig.getString(path + "staff");
                long startTime = dataConfig.getLong(path + "startTime");
                long durationMs = dataConfig.getLong(path + "durationMs");
                long remainingMs = dataConfig.getLong(path + "remainingMs");
                boolean active = dataConfig.getBoolean(path + "active", true);

                Punishment p = new Punishment(id, uuid, name, type, reasonKey, reasonTranslate, staff, startTime, durationMs, remainingMs, active);
                if (p.isActive() && p.isExpired()) {
                    p.setActive(false);
                    p.setRemainingMs(0L);
                } else if (p.isActive()) {
                    activePunishments.put(id, p);
                }
            }
        }

        if (historyConfig.contains("history")) {
            for (String id : historyConfig.getConfigurationSection("history").getKeys(false)) {
                String path = "history." + id + ".";
                UUID uuid = UUID.fromString(historyConfig.getString(path + "uuid"));
                String name = historyConfig.getString(path + "name");
                String type = historyConfig.getString(path + "type");
                String reasonKey = historyConfig.getString(path + "reasonKey");
                String reasonTranslate = historyConfig.getString(path + "reasonTranslate");
                String staff = historyConfig.getString(path + "staff");
                long startTime = historyConfig.getLong(path + "startTime");
                long durationMs = historyConfig.getLong(path + "durationMs");
                long remainingMs = historyConfig.getLong(path + "remainingMs");
                boolean active = historyConfig.getBoolean(path + "active", false);

                Punishment p = new Punishment(id, uuid, name, type, reasonKey, reasonTranslate, staff, startTime, durationMs, remainingMs, active);
                historyPunishments.put(id, p);
            }
        }
    }

    public void saveData() {
        dataConfig.set("punishments", null);
        for (Punishment p : activePunishments.values()) {
            String path = "punishments." + p.getId() + ".";
            dataConfig.set(path + "uuid", p.getPlayerUuid().toString());
            dataConfig.set(path + "name", p.getPlayerName());
            dataConfig.set(path + "type", p.getType());
            dataConfig.set(path + "reasonKey", p.getReasonKey());
            dataConfig.set(path + "reasonTranslate", p.getReasonTranslate());
            dataConfig.set(path + "staff", p.getStaffName());
            dataConfig.set(path + "startTime", p.getStartTime());
            dataConfig.set(path + "durationMs", p.getDurationMs());
            dataConfig.set(path + "remainingMs", p.getRemainingTimeMs());
            dataConfig.set(path + "active", p.isActive());
        }

        historyConfig.set("history", null);
        for (Punishment p : historyPunishments.values()) {
            String path = "history." + p.getId() + ".";
            historyConfig.set(path + "uuid", p.getPlayerUuid().toString());
            historyConfig.set(path + "name", p.getPlayerName());
            historyConfig.set(path + "type", p.getType());
            historyConfig.set(path + "reasonKey", p.getReasonKey());
            historyConfig.set(path + "reasonTranslate", p.getReasonTranslate());
            historyConfig.set(path + "staff", p.getStaffName());
            historyConfig.set(path + "startTime", p.getStartTime());
            historyConfig.set(path + "durationMs", p.getDurationMs());
            historyConfig.set(path + "remainingMs", p.getRemainingMs());
            historyConfig.set(path + "active", p.isActive());
        }

        try {
            dataConfig.save(dataFile);
            historyConfig.save(historyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateUniqueId() {
        String id;
        do {
            int num = ThreadLocalRandom.current().nextInt(100000, 999999);
            id = String.valueOf(num);
        } while (activePunishments.containsKey(id) || historyPunishments.containsKey(id));
        return id;
    }

    public Punishment addPunishment(UUID playerUuid, String playerName, String type, String reasonKey, String staffName) {
        FileConfiguration pConfig = plugin.getConfigManager().getPunishmentsConfig();
        String typeSection = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
        if (typeSection.equalsIgnoreCase("chat")) typeSection = "Chat";
        if (typeSection.equalsIgnoreCase("ban")) typeSection = "Ban";
        if (typeSection.equalsIgnoreCase("voicechat")) typeSection = "VoiceChat";

        if (!pConfig.contains(typeSection + "." + reasonKey)) {
            return null;
        }

        String timeStr = pConfig.getString(typeSection + "." + reasonKey + ".time", "1h");
        String reasonTranslate = pConfig.getString(typeSection + "." + reasonKey + ".punishment-translate", reasonKey);
        long durationMs = TimeUtil.parseTime(timeStr);
        long now = System.currentTimeMillis();
        String id = generateUniqueId();

        Punishment p = new Punishment(id, playerUuid, playerName, typeSection, reasonKey, reasonTranslate, staffName, now, durationMs, durationMs, true);
        activePunishments.put(id, p);
        saveData();

        // Broadcast / Notify
        String broad = plugin.getConfigManager().getMessage("punished-broadcast")
                .replace("%player%", playerName)
                .replace("%type%", typeSection)
                .replace("%reason%", reasonTranslate)
                .replace("%time%", TimeUtil.formatTime(durationMs))
                .replace("%id%", id);
        Bukkit.broadcastMessage(plugin.getConfigManager().getPrefix() + broad);

        Player target = Bukkit.getPlayer(playerUuid);
        if (target != null && target.isOnline()) {
            if (typeSection.equalsIgnoreCase("Ban")) {
                String kickMsg = plugin.getConfigManager().getMessage("ban-screen")
                        .replace("%reason%", reasonTranslate)
                        .replace("%time%", TimeUtil.formatTime(durationMs))
                        .replace("%id%", id);
                target.kickPlayer(ColorUtil.color(kickMsg));
            } else if (typeSection.equalsIgnoreCase("Chat")) {
                String targetMsg = plugin.getConfigManager().getMessage("punished-target-mute")
                        .replace("%type%", typeSection)
                        .replace("%reason%", reasonTranslate)
                        .replace("%time%", TimeUtil.formatTime(durationMs))
                        .replace("%id%", id);
                target.sendMessage(plugin.getConfigManager().getPrefix() + targetMsg);
            }
        }

        // VoiceChat LuckPerms Integration
        if (typeSection.equalsIgnoreCase("VoiceChat")) {
            String voiceNode = plugin.getConfigManager().getVoiceChatNode();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "luckperms user " + playerName + " permission settemp " + voiceNode + " true " + timeStr);
        }

        return p;
    }

    public boolean unpunishById(String id, String staffName) {
        Punishment p = activePunishments.get(id);
        if (p == null) {
            p = historyPunishments.get(id);
        }
        if (p == null) {
            return false;
        }

        long left = p.getRemainingTimeMs();
        p.setRemainingMs(left);
        p.setActive(false);

        activePunishments.remove(id);
        historyPunishments.put(id, p);
        saveData();

        if (p.getType().equalsIgnoreCase("VoiceChat")) {
            String voiceNode = plugin.getConfigManager().getVoiceChatNode();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "luckperms user " + p.getPlayerName() + " permission unset " + voiceNode);
        }

        String broad = plugin.getConfigManager().getMessage("unpunished-broadcast")
                .replace("%player%", p.getPlayerName())
                .replace("%type%", p.getType())
                .replace("%id%", id);
        Bukkit.broadcastMessage(plugin.getConfigManager().getPrefix() + broad);

        Player target = Bukkit.getPlayer(p.getPlayerUuid());
        if (target != null && target.isOnline()) {
            String targetMsg = plugin.getConfigManager().getMessage("unpunished-target")
                    .replace("%id%", id);
            target.sendMessage(plugin.getConfigManager().getPrefix() + targetMsg);
        }
        return true;
    }

    public boolean resumePunishmentById(String id, String staffName) {
        Punishment p = historyPunishments.get(id);
        if (p == null) {
            p = activePunishments.get(id);
        }
        if (p == null || p.getRemainingMs() <= 0) {
            return false;
        }

        long remaining = p.getRemainingMs();
        p.setStartTime(System.currentTimeMillis());
        p.setDurationMs(remaining);
        p.setActive(true);

        historyPunishments.remove(id);
        activePunishments.put(id, p);
        saveData();

        String timeFormatted = TimeUtil.formatTime(remaining);
        String broad = plugin.getConfigManager().getMessage("punished-broadcast")
                .replace("%player%", p.getPlayerName())
                .replace("%type%", p.getType())
                .replace("%reason%", p.getReasonTranslate())
                .replace("%time%", timeFormatted)
                .replace("%id%", id);
        Bukkit.broadcastMessage(plugin.getConfigManager().getPrefix() + broad);

        return true;
    }

    public Punishment getActiveBan(UUID uuid) {
        for (Punishment p : activePunishments.values()) {
            if (p.getPlayerUuid().equals(uuid) && p.getType().equalsIgnoreCase("Ban") && p.isActive()) {
                if (p.isExpired()) {
                    p.setActive(false);
                    p.setRemainingMs(0L);
                    saveData();
                } else {
                    return p;
                }
            }
        }
        return null;
    }

    public Punishment getActiveChatMute(UUID uuid) {
        for (Punishment p : activePunishments.values()) {
            if (p.getPlayerUuid().equals(uuid) && p.getType().equalsIgnoreCase("Chat") && p.isActive()) {
                if (p.isExpired()) {
                    p.setActive(false);
                    p.setRemainingMs(0L);
                    saveData();
                } else {
                    return p;
                }
            }
        }
        return null;
    }

    public Punishment getActiveVoiceMute(UUID uuid) {
        for (Punishment p : activePunishments.values()) {
            if (p.getPlayerUuid().equals(uuid) && p.getType().equalsIgnoreCase("VoiceChat") && p.isActive()) {
                if (p.isExpired()) {
                    p.setActive(false);
                    p.setRemainingMs(0L);
                    saveData();
                } else {
                    return p;
                }
            }
        }
        return null;
    }

    public List<Punishment> getActivePunishmentsByType(String type) {
        List<Punishment> list = new ArrayList<>();
        for (Punishment p : activePunishments.values()) {
            if (p.getType().equalsIgnoreCase(type) && p.isActive()) {
                if (p.isExpired()) {
                    p.setActive(false);
                    p.setRemainingMs(0L);
                } else {
                    list.add(p);
                }
            }
        }
        return list;
    }

    public List<Punishment> getAllPunishmentsForPlayer(UUID uuid) {
        List<Punishment> list = new ArrayList<>();
        for (Punishment p : activePunishments.values()) {
            if (p.getPlayerUuid().equals(uuid)) list.add(p);
        }
        for (Punishment p : historyPunishments.values()) {
            if (p.getPlayerUuid().equals(uuid)) list.add(p);
        }
        return list;
    }

    public Punishment getPunishmentById(String id) {
        if (activePunishments.containsKey(id)) return activePunishments.get(id);
        return historyPunishments.get(id);
    }

    public Map<String, Punishment> getActivePunishments() { return activePunishments; }
    public Map<String, Punishment> getHistoryPunishments() { return historyPunishments; }
}
