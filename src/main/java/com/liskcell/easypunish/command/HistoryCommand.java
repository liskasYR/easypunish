package com.liskcell.easypunish.command;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.menu.PunishmentListMenu;
import com.liskcell.easypunish.model.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HistoryCommand implements CommandExecutor {
    private final EasyPunish plugin;

    public HistoryCommand(EasyPunish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String permNode = plugin.getConfigManager().getPermission("history");
        if (!sender.hasPermission(permNode) && !sender.hasPermission(plugin.getConfigManager().getPermission("admin"))) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage").replace("%usage%", "/history <player>"));
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        List<Punishment> history = plugin.getPunishmentManager().getAllPunishmentsForPlayer(target.getUniqueId());

        if (sender instanceof Player) {
            new PunishmentListMenu(plugin, "history", targetName, history, 0).open((Player) sender);
        } else {
            sender.sendMessage("=== History for " + targetName + " ===");
            for (Punishment p : history) {
                sender.sendMessage("ID: " + p.getId() + " | Type: " + p.getType() + " | Reason: " + p.getReasonTranslate() + " | Active: " + p.isActive());
            }
        }
        return true;
    }
}
