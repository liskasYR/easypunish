package com.liskcell.easypunish;

import com.liskcell.easypunish.command.*;
import com.liskcell.easypunish.listener.GUIListener;
import com.liskcell.easypunish.listener.PlayerListener;
import com.liskcell.easypunish.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyPunish extends JavaPlugin {
    private static EasyPunish instance;

    private ConfigManager configManager;
    private PunishmentManager punishmentManager;
    private WarnManager warnManager;
    private AppealManager appealManager;
    private AltsManager altsManager;

    @Override
    public void onEnable() {
        instance = this;

        try {
            getLogger().info("Enabling EasyPunish v" + getDescription().getVersion() + " on " + Bukkit.getName() + " " + Bukkit.getMinecraftVersion() + "...");

            // Managers
            this.configManager = new ConfigManager(this);
            this.punishmentManager = new PunishmentManager(this);
            this.warnManager = new WarnManager(this);
            this.appealManager = new AppealManager(this);
            this.altsManager = new AltsManager(this);

            // Listeners
            getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
            getServer().getPluginManager().registerEvents(new GUIListener(this), this);

            // Commands
            registerCommand("punish", new PunishCommand(this));
            registerCommand("unpunish", new UnpunishCommand(this));
            registerCommand("warn", new WarnCommand(this));
            registerCommand("unwarn", new UnwarnCommand(this));
            registerCommand("kick", new KickCommand(this));

            ListCommands listCmd = new ListCommands(this);
            registerCommand("banlist", listCmd);
            registerCommand("mutelist", listCmd);
            registerCommand("warnlist", listCmd);
            registerCommand("voicelist", listCmd);

            registerCommand("history", new HistoryCommand(this));
            registerCommand("check", new CheckCommand(this));
            registerCommand("alts", new AltsCommand(this));
            registerCommand("appeal", new AppealCommand(this));
            registerCommand("appeals", new AppealsCommand(this));

            getLogger().info("EasyPunish successfully enabled!");
        } catch (Throwable t) {
            getLogger().severe("Failed to enable EasyPunish: " + t.getMessage());
            t.printStackTrace();
        }
    }

    private void registerCommand(String name, CommandExecutor executor) {
        if (getCommand(name) != null) {
            getCommand(name).setExecutor(executor);
        } else {
            getLogger().warning("Command /" + name + " was not found in plugin.yml!");
        }
    }

    @Override
    public void onDisable() {
        try {
            if (punishmentManager != null) punishmentManager.saveData();
            if (appealManager != null) appealManager.saveAppeals();
            if (altsManager != null) altsManager.saveAlts();
        } catch (Throwable t) {
            getLogger().warning("Error while saving EasyPunish data on disable: " + t.getMessage());
        }
        getLogger().info("EasyPunish disabled!");
    }

    public static EasyPunish getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public PunishmentManager getPunishmentManager() { return punishmentManager; }
    public WarnManager getWarnManager() { return warnManager; }
    public AppealManager getAppealManager() { return appealManager; }
    public AltsManager getAltsManager() { return altsManager; }
}
