package com.liskcell.easypunish.command;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.menu.AppealsMenu;
import com.liskcell.easypunish.model.Appeal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AppealsCommand implements CommandExecutor {
    private final EasyPunish plugin;

    public AppealsCommand(EasyPunish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("פקודה זו מיועדת לשחקנים בלבד.");
            return true;
        }
        Player player = (Player) sender;

        String permNode = plugin.getConfigManager().getPermission("appeallist");
        if (!player.hasPermission(permNode) && !player.hasPermission(plugin.getConfigManager().getPermission("admin"))) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length > 0) {
            String id = args[0];
            String typeFilter = args.length > 1 ? args[1] : null;

            Appeal appeal = plugin.getAppealManager().getAppeal(id);
            if (appeal != null) {
                if (typeFilter != null && !appeal.getPunishmentType().equalsIgnoreCase(typeFilter)) {
                    player.sendMessage(plugin.getConfigManager().getMessage("invalid-id").replace("%id%", id));
                    return true;
                }
                new AppealsMenu(plugin, appeal).open(player);
                return true;
            }
        }

        List<Appeal> pending = plugin.getAppealManager().getPendingAppeals();
        new AppealsMenu(plugin, pending, 0).open(player);
        return true;
    }
}
