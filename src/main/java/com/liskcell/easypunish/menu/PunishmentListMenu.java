package com.liskcell.easypunish.menu;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.model.Punishment;
import com.liskcell.easypunish.util.ColorUtil;
import com.liskcell.easypunish.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.util.*;

public class PunishmentListMenu implements InventoryHolder {
    private final EasyPunish plugin;
    private final String menuType; // banlist, mutelist, warnlist, voicelist, history
    private final String targetPlayerName;
    private final List<Punishment> punishments;
    private int page;
    private Inventory inventory;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public PunishmentListMenu(EasyPunish plugin, String menuType, String targetPlayerName, List<Punishment> punishments, int page) {
        this.plugin = plugin;
        this.menuType = menuType;
        this.targetPlayerName = targetPlayerName;
        this.punishments = punishments;
        this.page = page;
    }

    public void open(Player player) {
        FileConfiguration guiCfg = plugin.getConfigManager().getGuiConfig();
        String titleKey = menuType.toLowerCase();
        String rawTitle = guiCfg.getString("titles." + titleKey, "&8" + menuType + " (Page %page%)");
        String title = ColorUtil.color(rawTitle.replace("%page%", String.valueOf(page + 1)).replace("%player%", targetPlayerName != null ? targetPlayerName : ""));

        this.inventory = Bukkit.createInventory(this, 27, title);

        // Border Glass
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
        }
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i >= 18 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, glass);
            }
        }

        // Pagination
        int maxPerPage = 7;
        int startIndex = page * maxPerPage;
        int endIndex = Math.min(startIndex + maxPerPage, punishments.size());

        int[] itemSlots = {10, 11, 12, 13, 14, 15, 16};
        int slotIndex = 0;

        for (int i = startIndex; i < endIndex; i++) {
            Punishment p = punishments.get(i);
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(p.getPlayerUuid()));
                meta.setDisplayName(ColorUtil.color("&#1B83E5" + p.getPlayerName()));

                List<String> lore = new ArrayList<>();
                lore.add(ColorUtil.color("&#B9B8B3Punishment ID: &#1B83E5" + p.getId()));
                lore.add(ColorUtil.color("&#B9B8B3Type: &#1B83E5" + p.getType()));
                lore.add(ColorUtil.color("&#B9B8B3Reason: &#FF867A" + p.getReasonTranslate()));
                lore.add(ColorUtil.color("&#B9B8B3Staff: &#1B83E5" + p.getStaffName()));
                lore.add(ColorUtil.color("&#B9B8B3Date: &#B9B8B3" + DATE_FORMAT.format(new Date(p.getStartTime()))));
                lore.add(ColorUtil.color("&#B9B8B3Time Left: &#E0DB52" + TimeUtil.formatTime(p.getRemainingTimeMs())));
                lore.add(ColorUtil.color("&#B9B8B3Status: " + (p.isActive() ? "&#45E539Active" : "&#F53F3FInactive")));
                lore.add(" ");
                lore.add(ColorUtil.color("&#45E539Click to view full details"));

                meta.setLore(lore);
                skull.setItemMeta(meta);
            }
            if (slotIndex < itemSlots.length) {
                inventory.setItem(itemSlots[slotIndex++], skull);
            }
        }

        // Previous Page Button
        if (page > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta pMeta = prev.getItemMeta();
            if (pMeta != null) {
                pMeta.setDisplayName(ColorUtil.color("&#45E539Previous Page (" + page + ")"));
                prev.setItemMeta(pMeta);
            }
            inventory.setItem(18, prev);
        }

        // Next Page Button
        if (endIndex < punishments.size()) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nMeta = next.getItemMeta();
            if (nMeta != null) {
                nMeta.setDisplayName(ColorUtil.color("&#45E539Next Page (" + (page + 2) + ")"));
                next.setItemMeta(nMeta);
            }
            inventory.setItem(26, next);
        }

        // Close Button
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta cMeta = close.getItemMeta();
        if (cMeta != null) {
            cMeta.setDisplayName(ColorUtil.color("&#F53F3FClose Menu"));
            close.setItemMeta(cMeta);
        }
        inventory.setItem(22, close);

        player.openInventory(inventory);
    }

    public String getMenuType() { return menuType; }
    public String getTargetPlayerName() { return targetPlayerName; }
    public List<Punishment> getPunishments() { return punishments; }
    public int getPage() { return page; }

    @Override
    public Inventory getInventory() { return inventory; }
}
