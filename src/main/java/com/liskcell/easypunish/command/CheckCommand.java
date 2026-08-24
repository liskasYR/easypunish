package com.liskcell.easypunish.command;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.model.Punishment;
import com.liskcell.easypunish.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CheckCommand implements CommandExecutor {
    private final EasyPunish plugin;

    public CheckCommand(EasyPunish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage").replace("%usage%", "/check <player>"));
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        List<Punishment> activeList = plugin.getPunishmentManager().getAllPunishmentsForPlayer(target.getUniqueId());
        boolean hasActive = false;

        sender.sendMessage(plugin.getConfigManager().getMessage("check-header").replace("%player%", targetName));

        for (Punishment p : activeList) {
            if (p.isActive() && !p.isExpired()) {
                hasActive = true;
                String itemMsg = plugin.getConfigManager().getMessage("check-item")
                        .replace("%id%", p.getId())
                        .replace("%type%", p.getType())
                        .replace("%reason%", p.getReasonTranslate())
                        .replace("%time%", TimeUtil.formatTime(p.getRemainingTimeMs()));
                sender.sendMessage(itemMsg);
            }
        }

        if (!hasActive) {
            sender.sendMessage(plugin.getConfigManager().getMessage("check-none").replace("%player%", targetName));
        }

        return true;
    }
}
