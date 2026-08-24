package com.liskcell.easypunish.listener;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.model.Punishment;
import com.liskcell.easypunish.util.ColorUtil;
import com.liskcell.easypunish.util.TimeUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final EasyPunish plugin;

    public PlayerListener(EasyPunish plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        Punishment ban = plugin.getPunishmentManager().getActiveBan(event.getUniqueId());
        if (ban != null && ban.isActive()) {
            if (ban.isExpired()) {
                ban.setActive(false);
                ban.setRemainingMs(0L);
                plugin.getPunishmentManager().saveData();
                return;
            }
            String kickMsg = plugin.getConfigManager().getMessage("ban-screen")
                    .replace("%reason%", ban.getReasonTranslate())
                    .replace("%time%", TimeUtil.formatTime(ban.getRemainingTimeMs()))
                    .replace("%id%", ban.getId());
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ColorUtil.color(kickMsg));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getAddress() != null) {
            String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
            plugin.getAltsManager().registerPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName(), ip);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Punishment mute = plugin.getPunishmentManager().getActiveChatMute(event.getPlayer().getUniqueId());
        if (mute != null && mute.isActive()) {
            if (mute.isExpired()) {
                mute.setActive(false);
                mute.setRemainingMs(0L);
                plugin.getPunishmentManager().saveData();
                return;
            }
            event.setCancelled(true);
            String muteMsg = plugin.getConfigManager().getMessage("chat-muted")
                    .replace("%reason%", mute.getReasonTranslate())
                    .replace("%time%", TimeUtil.formatTime(mute.getRemainingTimeMs()))
                    .replace("%id%", mute.getId());
            event.getPlayer().sendMessage(plugin.getConfigManager().getPrefix() + muteMsg);
        }
    }
}
