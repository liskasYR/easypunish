package com.liskcell.easypunish.listener;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.menu.AppealsMenu;
import com.liskcell.easypunish.menu.PunishMenu;
import com.liskcell.easypunish.menu.PunishmentDetailMenu;
import com.liskcell.easypunish.menu.PunishmentListMenu;
import com.liskcell.easypunish.model.Appeal;
import com.liskcell.easypunish.model.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIListener implements Listener {
    private final EasyPunish plugin;

    public GUIListener(EasyPunish plugin) {
        this.plugin = plugin;
    }

    private void playGuiSound(Player player, String soundKey, String defaultSound) {
        FileConfiguration cfg = plugin.getConfigManager().getGuiConfig();
        String soundName = cfg.getString("sounds." + soundKey, defaultSound);
        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (Exception ignored) {}
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (holder instanceof PunishMenu) {
            event.setCancelled(true);
            PunishMenu menu = (PunishMenu) holder;
            int slot = event.getRawSlot();

            if (menu.getSelectedCategory() == null) {
                // Category Selection
                if (slot == 11) { // Chat
                    playGuiSound(player, "click", "ENTITY_EXPERIENCE_ORB_PICKUP");
                    new PunishMenu(plugin, menu.getTargetPlayerName(), "Chat").open(player);
                } else if (slot == 13) { // Ban
                    playGuiSound(player, "click", "ENTITY_EXPERIENCE_ORB_PICKUP");
                    new PunishMenu(plugin, menu.getTargetPlayerName(), "Ban").open(player);
                } else if (slot == 15) { // VoiceChat
                    playGuiSound(player, "click", "ENTITY_EXPERIENCE_ORB_PICKUP");
                    new PunishMenu(plugin, menu.getTargetPlayerName(), "VoiceChat").open(player);
                } else if (slot == 22) { // Close
                    playGuiSound(player, "click", "ENTITY_EXPERIENCE_ORB_PICKUP");
                    player.closeInventory();
                }
            } else {
                // Reason Selection
                if (slot == 18) { // Back
                    playGuiSound(player, "click", "ENTITY_EXPERIENCE_ORB_PICKUP");
                    new PunishMenu(plugin, menu.getTargetPlayerName(), null).open(player);
                } else if (slot == 22) { // Close
                    playGuiSound(player, "click", "ENTITY_EXPERIENCE_ORB_PICKUP");
                    player.closeInventory();
                } else if (slot >= 10 && slot <= 16) {
                    ItemStack clicked = event.getCurrentItem();
                    if (clicked != null && clicked.hasItemMeta()) {
                        ItemMeta meta = clicked.getItemMeta();
                        if (meta != null && meta.hasDisplayName()) {
                            String rawName = meta.getDisplayName();
                            // Strip formatting to get reason key
                            String reasonKey = org.bukkit.ChatColor.stripColor(rawName).trim();
                            OfflinePlayer target = Bukkit.getOfflinePlayer(menu.getTargetPlayerName());

                            playGuiSound(player, "approve", "ENTITY_PLAYER_LEVELUP");
                            plugin.getPunishmentManager().addPunishment(target.getUniqueId(), menu.getTargetPlayerName(), menu.getSelectedCategory(), reasonKey, player.getName());
                            player.closeInventory();
                        }
                    }
                }
            }

        } else if (holder instanceof PunishmentListMenu) {
            event.setCancelled(true);
            PunishmentListMenu menu = (PunishmentListMenu) holder;
            int slot = event.getRawSlot();

            if (slot == 18 && menu.getPage() > 0) {
                playGuiSound(player, "page-change", "ITEM_BOOK_PAGE_TURN");
                new PunishmentListMenu(plugin, menu.getMenuType(), menu.getTargetPlayerName(), menu.getPunishments(), menu.getPage() - 1).open(player);
            } else if (slot == 26) {
                int maxPerPage = 7;
                int maxPages = (int) Math.ceil((double) menu.getPunishments().size() / maxPerPage);
                if (menu.getPage() + 1 < maxPages) {
                    playGuiSound(player, "page-change", "ITEM_BOOK_PAGE_TURN");
                    new PunishmentListMenu(plugin, menu.getMenuType(), menu.getTargetPlayerName(), menu.getPunishments(), menu.getPage() + 1).open(player);
                }
            } else if (slot == 22) {
                playGuiSound(player, "click", "ENTITY_EXPERIENCE_ORB_PICKUP");
                player.closeInventory();
            } else if (slot >= 10 && slot <= 16) {
                int index = menu.getPage() * 7 + (slot - 10);
                if (index >= 0 && index < menu.getPunishments().size()) {
                    Punishment p = menu.getPunishments().get(index);
                    playGuiSound(player, "click", "ENTITY_EXPERIENCE_ORB_PICKUP");
                    new PunishmentDetailMenu(plugin, p).open(player);
                }
            }

        } else if (holder instanceof PunishmentDetailMenu) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot == 22) {
                playGuiSound(player, "click", "ENTITY_EXPERIENCE_ORB_PICKUP");
                player.closeInventory();
            }

        } else if (holder instanceof AppealsMenu) {
            event.setCancelled(true);
            AppealsMenu menu = (AppealsMenu) holder;
            int slot = event.getRawSlot();

            if (menu.getSingleAppeal() != null) {
                Appeal appeal = menu.getSingleAppeal();
                if (slot == 11) {
                    playGuiSound(player, "approve", "ENTITY_PLAYER_LEVELUP");
                    plugin.getAppealManager().approveAppeal(appeal.getPunishmentId(), player.getName());
                    player.closeInventory();
                } else if (slot == 15) {
                    playGuiSound(player, "deny", "ENTITY_VILLAGER_NO");
                    plugin.getAppealManager().denyAppeal(appeal.getPunishmentId(), player.getName());
                    player.closeInventory();
                } else if (slot == 22) {
                    playGuiSound(player, "click", "ENTITY_EXPERIENCE_ORB_PICKUP");
                    new AppealsMenu(plugin, plugin.getAppealManager().getPendingAppeals(), 0).open(player);
                }
            } else {
                if (slot == 18 && menu.getPage() > 0) {
                    playGuiSound(player, "page-change", "ITEM_BOOK_PAGE_TURN");
                    new AppealsMenu(plugin, menu.getAppeals(), menu.getPage() - 1).open(player);
                } else if (slot == 26) {
                    int maxPerPage = 7;
                    int maxPages = (int) Math.ceil((double) menu.getAppeals().size() / maxPerPage);
                    if (menu.getPage() + 1 < maxPages) {
                        playGuiSound(player, "page-change", "ITEM_BOOK_PAGE_TURN");
                        new AppealsMenu(plugin, menu.getAppeals(), menu.getPage() + 1).open(player);
                    }
                } else if (slot == 22) {
                    playGuiSound(player, "click", "ENTITY_EXPERIENCE_ORB_PICKUP");
                    player.closeInventory();
                } else if (slot >= 10 && slot <= 16) {
                    int index = menu.getPage() * 7 + (slot - 10);
                    if (index >= 0 && index < menu.getAppeals().size()) {
                        Appeal a = menu.getAppeals().get(index);
                        playGuiSound(player, "click", "ENTITY_EXPERIENCE_ORB_PICKUP");
                        new AppealsMenu(plugin, a).open(player);
                    }
                }
            }
        }
    }
}
