package com.liskcell.easypunish.command;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.model.Punishment;
import com.liskcell.easypunish.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Set;
import java.util.UUID;

public class AltsCommand implements CommandExecutor {
    private final EasyPunish plugin;

    public AltsCommand(EasyPunish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String permNode = plugin.getConfigManager().getPermission("alts");
        if (!sender.hasPermission(permNode) && !sender.hasPermission(plugin.getConfigManager().getPermission("admin"))) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage").replace("%usage%", "/alts <player>"));
            return true;
        }

        String targetName = args[0];
        String ip = plugin.getAltsManager().getIpForPlayer(targetName);
        if (ip == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found").replace("%player%", targetName));
            return true;
        }

        Set<String> alts = plugin.getAltsManager().getAlts(targetName);
        sender.sendMessage(plugin.getConfigManager().getMessage("alts-header").replace("%player%", targetName).replace("%ip%", ip));

        for (String entry : alts) {
            String[] parts = entry.split(":");
            String name = parts[0];
            UUID uuid = parts.length > 1 ? UUID.fromString(parts[1]) : null;

            String status = "&#45E539נקי";
            if (uuid != null) {
                Punishment ban = plugin.getPunishmentManager().getActiveBan(uuid);
                Punishment mute = plugin.getPunishmentManager().getActiveChatMute(uuid);
                if (ban != null) {
                    status = "&#F53F3FBanned (ID: " + ban.getId() + ")";
                } else if (mute != null) {
                    status = "&#FF867AMuted (ID: " + mute.getId() + ")";
                }
            }

            String itemMsg = plugin.getConfigManager().getMessage("alts-item")
                    .replace("%player%", name)
                    .replace("%status%", ColorUtil.color(status));
            sender.sendMessage(itemMsg);
        }

        return true;
    }
}
