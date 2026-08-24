package com.liskcell.easypunish.command;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.model.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnpunishCommand implements CommandExecutor {
    private final EasyPunish plugin;

    public UnpunishCommand(EasyPunish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage").replace("%usage%", "/unpunish <player|id> [type]"));
            return true;
        }

        String targetStr = args[0];

        // If 6-digit ID
        if (targetStr.matches("\\d{6}")) {
            Punishment p = plugin.getPunishmentManager().getPunishmentById(targetStr);
            if (p != null) {
                String permKey = "unpunish-" + p.getType().toLowerCase();
                String permNode = plugin.getConfigManager().getPermission(permKey);
                if (!sender.hasPermission(permNode) && !sender.hasPermission(plugin.getConfigManager().getPermission("admin"))) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }

                boolean success = plugin.getPunishmentManager().unpunishById(targetStr, sender.getName());
                if (!success) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("invalid-id").replace("%id%", targetStr));
                }
                return true;
            }
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage").replace("%usage%", "/unpunish <player> <type>"));
            return true;
        }

        String type = args[1];
        String permKey = "unpunish-" + type.toLowerCase();
        String permNode = plugin.getConfigManager().getPermission(permKey);
        if (!sender.hasPermission(permNode) && !sender.hasPermission(plugin.getConfigManager().getPermission("admin"))) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetStr);
        boolean found = false;

        for (Punishment p : plugin.getPunishmentManager().getActivePunishments().values()) {
            if (p.getPlayerUuid().equals(target.getUniqueId()) && p.getType().equalsIgnoreCase(type) && p.isActive()) {
                plugin.getPunishmentManager().unpunishById(p.getId(), sender.getName());
                found = true;
                break;
            }
        }

        if (!found) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found").replace("%player%", targetStr));
        }

        return true;
    }
}
