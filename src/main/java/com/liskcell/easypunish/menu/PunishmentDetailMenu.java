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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PunishmentDetailMenu implements InventoryHolder {
    private final EasyPunish plugin;
    private final Punishment punishment;
    private Inventory inventory;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public PunishmentDetailMenu(EasyPunish plugin, Punishment punishment) {
        this.plugin = plugin;
        this.punishment = punishment;
    }

    public void open(Player player) {
        FileConfiguration guiCfg = plugin.getConfigManager().getGuiConfig();
        String rawTitle = guiCfg.getString("titles.detail", "&8Punishment ID %id%");
        String title = ColorUtil.color(rawTitle.replace("%id%", punishment.getId()).replace("%player%", punishment.getPlayerName()));

        this.inventory = Bukkit.createInventory(this, 27, title);

        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
        }
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, glass);
        }

        // Center Skull
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(punishment.getPlayerUuid()));
            meta.setDisplayName(ColorUtil.color("&#1B83E5" + punishment.getPlayerName()));

            List<String> lore = new ArrayList<>();
            lore.add(ColorUtil.color("&#B9B8B3Punishment ID: &#1B83E5" + punishment.getId()));
            lore.add(ColorUtil.color("&#B9B8B3Type: &#1B83E5" + punishment.getType()));
            lore.add(ColorUtil.color("&#B9B8B3Reason Key: &#FF867A" + punishment.getReasonKey()));
            lore.add(ColorUtil.color("&#B9B8B3Reason Description: &#FF867A" + punishment.getReasonTranslate()));
            lore.add(ColorUtil.color("&#B9B8B3Staff Member: &#1B83E5" + punishment.getStaffName()));
            lore.add(ColorUtil.color("&#B9B8B3Issued Date: &#B9B8B3" + DATE_FORMAT.format(new Date(punishment.getStartTime()))));
            lore.add(ColorUtil.color("&#B9B8B3Time Remaining: &#E0DB52" + TimeUtil.formatTime(punishment.getRemainingTimeMs())));
            lore.add(ColorUtil.color("&#B9B8B3Status: " + (punishment.isActive() ? "&#45E539Active" : "&#F53F3FInactive / Expired")));

            meta.setLore(lore);
            skull.setItemMeta(meta);
        }
        inventory.setItem(13, skull);

        // Close Button
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta cMeta = close.getItemMeta();
        if (cMeta != null) {
            cMeta.setDisplayName(ColorUtil.color("&#F53F3FBack to List"));
            close.setItemMeta(cMeta);
        }
        inventory.setItem(22, close);

        player.openInventory(inventory);
    }

    public Punishment getPunishment() { return punishment; }

    @Override
    public Inventory getInventory() { return inventory; }
}
