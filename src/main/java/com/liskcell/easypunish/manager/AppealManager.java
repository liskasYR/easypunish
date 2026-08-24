package com.liskcell.easypunish.manager;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.model.Appeal;
import com.liskcell.easypunish.model.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AppealManager {
    private final EasyPunish plugin;
    private final File dataFolder;
    private final File appealsFile;
    private FileConfiguration appealsConfig;
    private final Map<String, Appeal> appeals = new HashMap<>();

    public AppealManager(EasyPunish plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.appealsFile = new File(dataFolder, "appeals.yml");
        loadAppeals();
    }

    public void loadAppeals() {
        try {
            if (!appealsFile.exists()) {
                appealsFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        appealsConfig = YamlConfiguration.loadConfiguration(appealsFile);
        appeals.clear();

        if (appealsConfig.contains("appeals")) {
            for (String id : appealsConfig.getConfigurationSection("appeals").getKeys(false)) {
                String path = "appeals." + id + ".";
                UUID uuid = UUID.fromString(appealsConfig.getString(path + "uuid"));
                String name = appealsConfig.getString(path + "name");
                String type = appealsConfig.getString(path + "type");
                String origReason = appealsConfig.getString(path + "originalReason");
                String appealReason = appealsConfig.getString(path + "appealReason");
                String status = appealsConfig.getString(path + "status", "PENDING");
                long timestamp = appealsConfig.getLong(path + "timestamp");

                Appeal appeal = new Appeal(id, uuid, name, type, origReason, appealReason, status, timestamp);
                appeals.put(id, appeal);
            }
        }
    }

    public void saveAppeals() {
        appealsConfig.set("appeals", null);
        for (Appeal a : appeals.values()) {
            String path = "appeals." + a.getPunishmentId() + ".";
            appealsConfig.set(path + "uuid", a.getPlayerUuid().toString());
            appealsConfig.set(path + "name", a.getPlayerName());
            appealsConfig.set(path + "type", a.getPunishmentType());
            appealsConfig.set(path + "originalReason", a.getOriginalReason());
            appealsConfig.set(path + "appealReason", a.getAppealReason());
            appealsConfig.set(path + "status", a.getStatus());
            appealsConfig.set(path + "timestamp", a.getTimestamp());
        }
        try {
            appealsConfig.save(appealsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String submitAppeal(Player player, String punishmentId, String appealReason) {
        Punishment p = plugin.getPunishmentManager().getPunishmentById(punishmentId);
        if (p == null) {
            return plugin.getConfigManager().getMessage("invalid-id").replace("%id%", punishmentId);
        }

        if (!p.getPlayerUuid().equals(player.getUniqueId())) {
            return plugin.getConfigManager().getMessage("not-your-punishment");
        }

        if (appeals.containsKey(punishmentId) && "PENDING".equalsIgnoreCase(appeals.get(punishmentId).getStatus())) {
            return plugin.getConfigManager().getMessage("already-appealed").replace("%id%", punishmentId);
        }

        Appeal appeal = new Appeal(punishmentId, player.getUniqueId(), player.getName(), p.getType(), p.getReasonTranslate(), appealReason, "PENDING", System.currentTimeMillis());
        appeals.put(punishmentId, appeal);
        saveAppeals();

        // Notify Staff
        String staffPerm = plugin.getConfigManager().getPermission("appeallist");
        String notifyMsg = plugin.getConfigManager().getMessage("appeal-notify-staff")
                .replace("%player%", player.getName())
                .replace("%id%", punishmentId);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission(staffPerm) || online.isOp()) {
                online.sendMessage(plugin.getConfigManager().getPrefix() + notifyMsg);
            }
        }

        return plugin.getConfigManager().getMessage("appeal-submitted").replace("%id%", punishmentId);
    }

    public boolean approveAppeal(String punishmentId, String staffName) {
        Appeal appeal = appeals.get(punishmentId);
        if (appeal == null) return false;

        appeal.setStatus("APPROVED");
        saveAppeals();

        plugin.getPunishmentManager().unpunishById(punishmentId, staffName);

        Player target = Bukkit.getPlayer(appeal.getPlayerUuid());
        if (target != null && target.isOnline()) {
            String msg = plugin.getConfigManager().getMessage("appeal-approved").replace("%id%", punishmentId);
            target.sendMessage(plugin.getConfigManager().getPrefix() + msg);
        }

        return true;
    }

    public boolean denyAppeal(String punishmentId, String staffName) {
        Appeal appeal = appeals.get(punishmentId);
        if (appeal == null) return false;

        appeal.setStatus("DENIED");
        saveAppeals();

        Player target = Bukkit.getPlayer(appeal.getPlayerUuid());
        if (target != null && target.isOnline()) {
            String msg = plugin.getConfigManager().getMessage("appeal-denied").replace("%id%", punishmentId);
            target.sendMessage(plugin.getConfigManager().getPrefix() + msg);
        }

        return true;
    }

    public List<Appeal> getPendingAppeals() {
        List<Appeal> list = new ArrayList<>();
        for (Appeal a : appeals.values()) {
            if ("PENDING".equalsIgnoreCase(a.getStatus())) {
                list.add(a);
            }
        }
        return list;
    }

    public Appeal getAppeal(String id) { return appeals.get(id); }
    public Map<String, Appeal> getAppeals() { return appeals; }
}
