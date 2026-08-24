package com.liskcell.easypunish.manager;

import com.liskcell.easypunish.EasyPunish;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarnManager {
    private final EasyPunish plugin;
    // Map<UUID + ":" + type + ":" + reasonKey, Integer>
    private final Map<String, Integer> warnCounts = new HashMap<>();

    public WarnManager(EasyPunish plugin) {
        this.plugin = plugin;
    }

    public int addWarn(UUID playerUuid, String playerName, String type, String reasonKey, String staffName) {
        FileConfiguration pConfig = plugin.getConfigManager().getPunishmentsConfig();
        String typeSection = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
        if (typeSection.equalsIgnoreCase("chat")) typeSection = "Chat";
        if (typeSection.equalsIgnoreCase("ban")) typeSection = "Ban";
        if (typeSection.equalsIgnoreCase("voicechat")) typeSection = "VoiceChat";

        if (!pConfig.contains(typeSection + "." + reasonKey)) {
            return -1;
        }

        int maxWarns = pConfig.getInt(typeSection + "." + reasonKey + ".warns", 3);
        String reasonTranslate = pConfig.getString(typeSection + "." + reasonKey + ".punishment-translate", reasonKey);
        String key = playerUuid.toString() + ":" + typeSection.toLowerCase() + ":" + reasonKey.toLowerCase();

        int current = warnCounts.getOrDefault(key, 0) + 1;
        warnCounts.put(key, current);

        // Broadcast/Notify Warn
        String broad = plugin.getConfigManager().getMessage("warned-broadcast")
                .replace("%player%", playerName)
                .replace("%reason%", reasonTranslate)
                .replace("%warns%", String.valueOf(current))
                .replace("%max_warns%", String.valueOf(maxWarns));
        Bukkit.broadcastMessage(plugin.getConfigManager().getPrefix() + broad);

        Player target = Bukkit.getPlayer(playerUuid);
        if (target != null && target.isOnline()) {
            String targetMsg = plugin.getConfigManager().getMessage("warned-target")
                    .replace("%reason%", reasonTranslate)
                    .replace("%warns%", String.valueOf(current))
                    .replace("%max_warns%", String.valueOf(maxWarns));
            target.sendMessage(plugin.getConfigManager().getPrefix() + targetMsg);
        }

        // Auto-Punish if reached threshold (3 warnings -> Automatic Ban)
        if (current >= maxWarns) {
            warnCounts.put(key, 0);

            String autoBanMsg = plugin.getConfigManager().getMessage("warned-autoban")
                    .replace("%player%", playerName)
                    .replace("%warns%", String.valueOf(current))
                    .replace("%max_warns%", String.valueOf(maxWarns));
            Bukkit.broadcastMessage(plugin.getConfigManager().getPrefix() + autoBanMsg);

            // Automatically issue a Ban
            plugin.getPunishmentManager().addPunishment(playerUuid, playerName, "Ban", reasonKey.equalsIgnoreCase("Cheating") ? "Cheating" : "SevereToxic", staffName);
        }

        return current;
    }

    public boolean removeWarn(UUID playerUuid, String playerName, String type, String reasonKey) {
        String typeSection = type.toLowerCase();
        String key = playerUuid.toString() + ":" + typeSection + ":" + reasonKey.toLowerCase();
        int current = warnCounts.getOrDefault(key, 0);
        if (current <= 0) {
            return false;
        }
        warnCounts.put(key, current - 1);
        return true;
    }

    public int getWarnCount(UUID playerUuid, String type, String reasonKey) {
        String key = playerUuid.toString() + ":" + type.toLowerCase() + ":" + reasonKey.toLowerCase();
        return warnCounts.getOrDefault(key, 0);
    }
}
