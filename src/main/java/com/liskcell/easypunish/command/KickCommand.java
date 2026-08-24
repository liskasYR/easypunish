package com.liskcell.easypunish.command;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {
    private final EasyPunish plugin;

    public KickCommand(EasyPunish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String permNode = plugin.getConfigManager().getPermission("kick");
        if (!sender.hasPermission(permNode) && !sender.hasPermission(plugin.getConfigManager().getPermission("admin"))) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage").replace("%usage%", "/kick <player> [reason]"));
            return true;
        }

        String targetStr = args[0];
        Player target = Bukkit.getPlayer(targetStr);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found").replace("%player%", targetStr));
            return true;
        }

        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.length() > 0 ? reasonBuilder.toString().trim() : "לא צוינה סיבה";

        String kickScreen = plugin.getConfigManager().getMessage("kick-screen")
                .replace("%reason%", reason)
                .replace("%staff%", sender.getName());
        target.kickPlayer(ColorUtil.color(kickScreen));

        String broad = plugin.getConfigManager().getMessage("kick-broadcast")
                .replace("%player%", target.getName())
                .replace("%staff%", sender.getName())
                .replace("%reason%", reason);
        Bukkit.broadcastMessage(plugin.getConfigManager().getPrefix() + broad);

        return true;
    }
}
