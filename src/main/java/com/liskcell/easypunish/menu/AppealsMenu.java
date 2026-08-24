package com.liskcell.easypunish.menu;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.model.Appeal;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppealsMenu implements InventoryHolder {
    private final EasyPunish plugin;
    private final List<Appeal> appeals;
    private final Appeal singleAppeal;
    private final int page;
    private Inventory inventory;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public AppealsMenu(EasyPunish plugin, List<Appeal> appeals, int page) {
        this.plugin = plugin;
        this.appeals = appeals;
        this.singleAppeal = null;
        this.page = page;
    }

    public AppealsMenu(EasyPunish plugin, Appeal singleAppeal) {
        this.plugin = plugin;
        this.appeals = null;
        this.singleAppeal = singleAppeal;
        this.page = 0;
    }

    public void open(Player player) {
        FileConfiguration guiCfg = plugin.getConfigManager().getGuiConfig();

        if (singleAppeal != null) {
            String rawTitle = guiCfg.getString("titles.appeal-detail", "&8Appeal ID %id%");
            String title = ColorUtil.color(rawTitle.replace("%id%", singleAppeal.getPunishmentId()).replace("%player%", singleAppeal.getPlayerName()));

            this.inventory = Bukkit.createInventory(this, 27, title);

            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta glassMeta = glass.getItemMeta();
            if (glassMeta != null) {
                glassMeta.setDisplayName(" ");
                glass.setItemMeta(glassMeta);
            }
            for (int i = 0; i < 27; i++) inventory.setItem(i, glass);

            // Center Skull
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(singleAppeal.getPlayerUuid()));
                meta.setDisplayName(ColorUtil.color("&#1B83E5" + singleAppeal.getPlayerName()));

                List<String> lore = new ArrayList<>();
                lore.add(ColorUtil.color("&#B9B8B3Punishment ID: &#1B83E5" + singleAppeal.getPunishmentId()));
                lore.add(ColorUtil.color("&#B9B8B3Type: &#1B83E5" + singleAppeal.getPunishmentType()));
                lore.add(ColorUtil.color("&#B9B8B3Original Reason: &#FF867A" + singleAppeal.getOriginalReason()));
                lore.add(ColorUtil.color("&#B9B8B3Appeal Reason: &#E0DB52" + singleAppeal.getAppealReason()));
                lore.add(ColorUtil.color("&#B9B8B3Submitted Date: &#B9B8B3" + DATE_FORMAT.format(new Date(singleAppeal.getTimestamp()))));
                lore.add(ColorUtil.color("&#B9B8B3Status: &#1B83E5" + singleAppeal.getStatus()));

                meta.setLore(lore);
                skull.setItemMeta(meta);
            }
            inventory.setItem(13, skull);

            // Approve Button
            ItemStack approve = new ItemStack(Material.LIME_WOOL);
            ItemMeta aMeta = approve.getItemMeta();
            if (aMeta != null) {
                aMeta.setDisplayName(ColorUtil.color("&#45E539Approve Appeal (Unpunish)"));
                List<String> aLore = new ArrayList<>();
                aLore.add(ColorUtil.color("&#B9B8B3Left-click to approve appeal"));
                aLore.add(ColorUtil.color("&#B9B8B3and remove punishment immediately!"));
                aMeta.setLore(aLore);
                approve.setItemMeta(aMeta);
            }
            inventory.setItem(11, approve);

            // Deny Button
            ItemStack deny = new ItemStack(Material.RED_WOOL);
            ItemMeta dMeta = deny.getItemMeta();
            if (dMeta != null) {
                dMeta.setDisplayName(ColorUtil.color("&#F53F3FDeny Appeal"));
                List<String> dLore = new ArrayList<>();
                dLore.add(ColorUtil.color("&#B9B8B3Right-click to deny appeal"));
                dLore.add(ColorUtil.color("&#B9B8B3and remove from list!"));
                dMeta.setLore(dLore);
                deny.setItemMeta(dMeta);
            }
            inventory.setItem(15, deny);

            // Close
            ItemStack close = new ItemStack(Material.BARRIER);
            ItemMeta cMeta = close.getItemMeta();
            if (cMeta != null) {
                cMeta.setDisplayName(ColorUtil.color("&#F53F3FBack to Appeals List"));
                close.setItemMeta(cMeta);
            }
            inventory.setItem(22, close);

        } else {
            String rawTitle = guiCfg.getString("titles.appeals", "&8Appeals List (Page %page%)");
            String title = ColorUtil.color(rawTitle.replace("%page%", String.valueOf(page + 1)));

            this.inventory = Bukkit.createInventory(this, 27, title);

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

            int maxPerPage = 7;
            int startIndex = page * maxPerPage;
            int endIndex = Math.min(startIndex + maxPerPage, appeals.size());

            int[] itemSlots = {10, 11, 12, 13, 14, 15, 16};
            int slotIndex = 0;

            for (int i = startIndex; i < endIndex; i++) {
                Appeal a = appeals.get(i);
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                if (meta != null) {
                    meta.setOwningPlayer(Bukkit.getOfflinePlayer(a.getPlayerUuid()));
                    meta.setDisplayName(ColorUtil.color("&#1B83E5" + a.getPlayerName() + " &#B9B8B3(ID: " + a.getPunishmentId() + ")"));

                    List<String> lore = new ArrayList<>();
                    lore.add(ColorUtil.color("&#B9B8B3Type: &#1B83E5" + a.getPunishmentType()));
                    lore.add(ColorUtil.color("&#B9B8B3Original Reason: &#FF867A" + a.getOriginalReason()));
                    lore.add(ColorUtil.color("&#B9B8B3Appeal Reason: &#E0DB52" + a.getAppealReason()));
                    lore.add(ColorUtil.color("&#B9B8B3Date: &#B9B8B3" + DATE_FORMAT.format(new Date(a.getTimestamp()))));
                    lore.add(" ");
                    lore.add(ColorUtil.color("&#45E539Click to review appeal"));

                    meta.setLore(lore);
                    skull.setItemMeta(meta);
                }
                if (slotIndex < itemSlots.length) {
                    inventory.setItem(itemSlots[slotIndex++], skull);
                }
            }

            // Pagination & Close
            if (page > 0) {
                ItemStack prev = new ItemStack(Material.ARROW);
                ItemMeta pMeta = prev.getItemMeta();
                if (pMeta != null) {
                    pMeta.setDisplayName(ColorUtil.color("&#45E539Previous Page (" + page + ")"));
                    prev.setItemMeta(pMeta);
                }
                inventory.setItem(18, prev);
            }

            if (endIndex < appeals.size()) {
                ItemStack next = new ItemStack(Material.ARROW);
                ItemMeta nMeta = next.getItemMeta();
                if (nMeta != null) {
                    nMeta.setDisplayName(ColorUtil.color("&#45E539Next Page (" + (page + 2) + ")"));
                    next.setItemMeta(nMeta);
                }
                inventory.setItem(26, next);
            }

            ItemStack close = new ItemStack(Material.BARRIER);
            ItemMeta cMeta = close.getItemMeta();
            if (cMeta != null) {
                cMeta.setDisplayName(ColorUtil.color("&#F53F3FClose Menu"));
                close.setItemMeta(cMeta);
            }
            inventory.setItem(22, close);
        }

        player.openInventory(inventory);
    }

    public List<Appeal> getAppeals() { return appeals; }
    public Appeal getSingleAppeal() { return singleAppeal; }
    public int getPage() { return page; }

    @Override
    public Inventory getInventory() { return inventory; }
}
