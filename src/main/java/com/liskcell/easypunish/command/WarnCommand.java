package com.liskcell.easypunish.command;

import com.liskcell.easypunish.EasyPunish;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WarnCommand implements CommandExecutor {
    private final EasyPunish plugin;

    public WarnCommand(EasyPunish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage").replace("%usage%", "/warn <player> <type> <punishment>"));
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

        int res = plugin.getWarnManager().addWarn(target.getUniqueId(), targetName, type, punishmentKey, sender.getName());
        if (res == -1) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-punishment").replace("%punishment%", punishmentKey));
        }

        return true;
    }
}
