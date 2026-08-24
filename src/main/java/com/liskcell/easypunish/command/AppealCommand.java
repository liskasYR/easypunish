package com.liskcell.easypunish.command;

import com.liskcell.easypunish.EasyPunish;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AppealCommand implements CommandExecutor {
    private final EasyPunish plugin;

    public AppealCommand(EasyPunish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("פקודה זו מיועדת לשחקנים בלבד.");
            return true;
        }
        Player player = (Player) sender;

        String permNode = plugin.getConfigManager().getPermission("appeal");
        if (!player.hasPermission(permNode) && !player.hasPermission(plugin.getConfigManager().getPermission("admin"))) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().getMessage("invalid-usage").replace("%usage%", "/appeal <id> <reason>"));
            return true;
        }

        String id = args[0];
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String appealReason = sb.toString().trim();

        String response = plugin.getAppealManager().submitAppeal(player, id, appealReason);
        player.sendMessage(plugin.getConfigManager().getPrefix() + response);
        return true;
    }
}
