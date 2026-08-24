package com.liskcell.easypunish.command;

import com.liskcell.easypunish.EasyPunish;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnwarnCommand implements CommandExecutor {
    private final EasyPunish plugin;

    public UnwarnCommand(EasyPunish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage").replace("%usage%", "/unwarn <player> <type> <punishment>"));
            return true;
        }

        String targetStr = args[0];
        String type = args[1];
        String punishmentKey = args[2];

        String permKey = "warn-" + type.toLowerCase();
        String permNode = plugin.getConfigManager().getPermission(permKey);
        if (!sender.hasPermission(permNode) && !sender.hasPermission(plugin.getConfigManager().getPermission("admin"))) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetStr);
        String targetName = target.getName() != null ? target.getName() : targetStr;

        boolean success = plugin.getWarnManager().removeWarn(target.getUniqueId(), targetName, type, punishmentKey);
        if (success) {
            String msg = plugin.getConfigManager().getMessage("unwarned-staff")
                    .replace("%player%", targetName)
                    .replace("%reason%", punishmentKey);
            sender.sendMessage(plugin.getConfigManager().getPrefix() + msg);
        } else {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found").replace("%player%", targetName));
        }

        return true;
    }
}
