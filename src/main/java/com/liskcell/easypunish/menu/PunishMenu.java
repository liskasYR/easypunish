package com.liskcell.easypunish.menu;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class PunishMenu implements InventoryHolder {
    private final EasyPunish plugin;
    private final String targetPlayerName;
    private final String selectedCategory; // null for category selection, or "Chat", "Ban", "VoiceChat"
    private Inventory inventory;

    public PunishMenu(EasyPunish plugin, String targetPlayerName, String selectedCategory) {
        this.plugin = plugin;
        this.targetPlayerName = targetPlayerName;
        this.selectedCategory = selectedCategory;
    }

    public void open(Player player) {
        if (selectedCategory == null) {
            // Category Select Menu
            String title = ColorUtil.color("&#9EB99ESelect Type for &#1B83E5" + targetPlayerName);
            this.inventory = Bukkit.createInventory(this, 27, title);

            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta gMeta = glass.getItemMeta();
            if (gMeta != null) { gMeta.setDisplayName(" "); glass.setItemMeta(gMeta); }
            for (int i = 0; i < 27; i++) inventory.setItem(i, glass);

            // Target Skull at Slot 4
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta sMeta = (SkullMeta) skull.getItemMeta();
            if (sMeta != null) {
                sMeta.setOwningPlayer(Bukkit.getOfflinePlayer(targetPlayerName));
                sMeta.setDisplayName(ColorUtil.color("&#1B83E5Target: " + targetPlayerName));
                skull.setItemMeta(sMeta);
            }
            inventory.setItem(4, skull);

            // Category 1: Chat (Slot 11)
            ItemStack chat = new ItemStack(Material.WRITABLE_BOOK);
            ItemMeta cMeta = chat.getItemMeta();
            if (cMeta != null) {
                cMeta.setDisplayName(ColorUtil.color("&#1B83E5Chat Mutes"));
                List<String> lore = new ArrayList<>();
                lore.add(ColorUtil.color("&#B9B8B3Click to select chat mute reasons"));
                cMeta.setLore(lore);
                chat.setItemMeta(cMeta);
            }
            inventory.setItem(11, chat);

            // Category 2: Ban (Slot 13)
            ItemStack ban = new ItemStack(Material.REDSTONE_BLOCK);
            ItemMeta bMeta = ban.getItemMeta();
            if (bMeta != null) {
                bMeta.setDisplayName(ColorUtil.color("&#F53F3FServer Bans"));
                List<String> lore = new ArrayList<>();
                lore.add(ColorUtil.color("&#B9B8B3Click to select server ban reasons"));
                bMeta.setLore(lore);
                ban.setItemMeta(bMeta);
            }
            inventory.setItem(13, ban);

            // Category 3: VoiceChat (Slot 15)
            ItemStack voice = new ItemStack(Material.JUKEBOX);
            ItemMeta vMeta = voice.getItemMeta();
            if (vMeta != null) {
                vMeta.setDisplayName(ColorUtil.color("&#E0DB52VoiceChat Mutes"));
                List<String> lore = new ArrayList<>();
                lore.add(ColorUtil.color("&#B9B8B3Click to select voice chat mute reasons"));
                vMeta.setLore(lore);
                voice.setItemMeta(vMeta);
            }
            inventory.setItem(15, voice);

            // Close at Slot 22
            ItemStack close = new ItemStack(Material.BARRIER);
            ItemMeta clMeta = close.getItemMeta();
            if (clMeta != null) { clMeta.setDisplayName(ColorUtil.color("&#F53F3FClose Menu")); close.setItemMeta(clMeta); }
            inventory.setItem(22, close);

        } else {
            // Reason Select Menu
            String title = ColorUtil.color("&#9EB99ESelect Reason (&#1B83E5" + selectedCategory + "&#9EB99E) - &#1B83E5" + targetPlayerName);
            this.inventory = Bukkit.createInventory(this, 27, title);

            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta gMeta = glass.getItemMeta();
            if (gMeta != null) { gMeta.setDisplayName(" "); glass.setItemMeta(gMeta); }
            for (int i = 0; i < 27; i++) inventory.setItem(i, glass);

            FileConfiguration pConfig = plugin.getConfigManager().getPunishmentsConfig();
            if (pConfig.contains(selectedCategory)) {
                int[] slots = {10, 11, 12, 13, 14, 15, 16};
                int idx = 0;
                for (String reasonKey : pConfig.getConfigurationSection(selectedCategory).getKeys(false)) {
                    if (idx >= slots.length) break;

                    String timeStr = pConfig.getString(selectedCategory + "." + reasonKey + ".time", "1h");
                    String translate = pConfig.getString(selectedCategory + "." + reasonKey + ".punishment-translate", reasonKey);

                    ItemStack item = new ItemStack(Material.PAPER);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(ColorUtil.color("&#FF867A" + reasonKey));
                        List<String> lore = new ArrayList<>();
                        lore.add(ColorUtil.color("&#B9B8B3Description: &#FF867A" + translate));
                        lore.add(ColorUtil.color("&#B9B8B3Duration: &#E0DB52" + timeStr));
                        lore.add(" ");
                        lore.add(ColorUtil.color("&#45E539Click to issue punishment immediately!"));
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }
                    inventory.setItem(slots[idx++], item);
                }
            }

            // Back button at Slot 18
            ItemStack back = new ItemStack(Material.ARROW);
            ItemMeta bkMeta = back.getItemMeta();
            if (bkMeta != null) { bkMeta.setDisplayName(ColorUtil.color("&#45E539Back to Category Selection")); back.setItemMeta(bkMeta); }
            inventory.setItem(18, back);

            // Close button at Slot 22
            ItemStack close = new ItemStack(Material.BARRIER);
            ItemMeta clMeta = close.getItemMeta();
            if (clMeta != null) { clMeta.setDisplayName(ColorUtil.color("&#F53F3FClose Menu")); close.setItemMeta(clMeta); }
            inventory.setItem(22, close);
        }

        player.openInventory(inventory);
    }

    public String getTargetPlayerName() { return targetPlayerName; }
    public String getSelectedCategory() { return selectedCategory; }

    @Override
    public Inventory getInventory() { return inventory; }
}
