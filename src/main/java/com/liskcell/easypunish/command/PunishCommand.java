package com.liskcell.easypunish.command;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.menu.PunishMenu;
import com.liskcell.easypunish.model.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PunishCommand implements CommandExecutor {
    private final EasyPunish plugin;

    public PunishCommand(EasyPunish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage").replace("%usage%", "/punish <player|id> [type] [punishment]"));
            return true;
        }

        String targetStr = args[0];

        // Check if targetStr is a 6-digit ID
        if (targetStr.matches("\\d{6}")) {
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetStr);
            if (!targetPlayer.hasPlayedBefore() && !targetPlayer.isOnline()) {
                // Treat as ID
                Punishment p = plugin.getPunishmentManager().getPunishmentById(targetStr);
                if (p == null) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("invalid-id").replace("%id%", targetStr));
                    return true;
                }
                String permKey = "punish-" + p.getType().toLowerCase();
                String permNode = plugin.getConfigManager().getPermission(permKey);
                if (!sender.hasPermission(permNode) && !sender.hasPermission(plugin.getConfigManager().getPermission("admin"))) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }

                boolean resumed = plugin.getPunishmentManager().resumePunishmentById(targetStr, sender.getName());
                if (!resumed) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("invalid-id").replace("%id%", targetStr));
                }
                return true;
            }
        }

        // If only player name is given and sender is Player -> Open Punish GUI
        if (args.length < 3) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                new PunishMenu(plugin, targetStr, null).open(player);
                return true;
            } else {
                sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage").replace("%usage%", "/punish <player> <type> <punishment>"));
                return true;
            }
        }

        String type = args[1];
        String punishmentKey = args[2];

        String permKey = "punish-" + type.toLowerCase();
        String permNode = plugin.getConfigManager().getPermission(permKey);
        if (!sender.hasPermission(permNode) && !sender.hasPermission(plugin.getConfigManager().getPermission("admin"))) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetStr);
        UUID targetUuid = target.getUniqueId();
        String targetName = target.getName() != null ? target.getName() : targetStr;

        Punishment p = plugin.getPunishmentManager().addPunishment(targetUuid, targetName, type, punishmentKey, sender.getName());
        if (p == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-punishment").replace("%punishment%", punishmentKey));
        }

        return true;
    }
}
