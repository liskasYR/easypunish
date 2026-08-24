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

import java.util.ArrayList;
import java.util.List;

public class ListCommands implements CommandExecutor {
    private final EasyPunish plugin;

    public ListCommands(EasyPunish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("פקודה זו היא לשחקנים בלבד.");
            return true;
        }
        Player player = (Player) sender;
        String cmdName = label.toLowerCase();

        String type;
        if (cmdName.contains("ban")) type = "Ban";
        else if (cmdName.contains("mute")) type = "Chat";
        else if (cmdName.contains("voice")) type = "VoiceChat";
        else type = "Chat";

        String permNode = plugin.getConfigManager().getPermission(cmdName);
        if (!player.hasPermission(permNode) && !player.hasPermission(plugin.getConfigManager().getPermission("admin"))) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        String filterPlayerName = args.length > 0 ? args[0] : null;
        List<Punishment> punishments = plugin.getPunishmentManager().getActivePunishmentsByType(type);

        if (filterPlayerName != null && !filterPlayerName.isEmpty()) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(filterPlayerName);
            List<Punishment> filtered = new ArrayList<>();
            for (Punishment p : punishments) {
                if (p.getPlayerUuid().equals(target.getUniqueId()) || p.getPlayerName().equalsIgnoreCase(filterPlayerName)) {
                    filtered.add(p);
                }
            }
            punishments = filtered;
        }

        new PunishmentListMenu(plugin, cmdName, filterPlayerName, punishments, 0).open(player);
        return true;
    }
}
