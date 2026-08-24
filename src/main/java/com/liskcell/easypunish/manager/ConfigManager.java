package com.liskcell.easypunish.manager;

import com.liskcell.easypunish.EasyPunish;
import com.liskcell.easypunish.util.ColorUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final EasyPunish plugin;
    private FileConfiguration config;
    private FileConfiguration permissionsConfig;
    private FileConfiguration punishmentsConfig;
    private FileConfiguration guiConfig;

    private File permissionsFile;
    private File punishmentsFile;
    private File guiFile;

    private final Map<String, String> permissionMap = new HashMap<>();

    public ConfigManager(EasyPunish plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    public void loadConfigs() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        permissionsFile = new File(plugin.getDataFolder(), "permissions.yml");
        if (!permissionsFile.exists()) {
            plugin.saveResource("permissions.yml", false);
        }
        permissionsConfig = YamlConfiguration.loadConfiguration(permissionsFile);

        punishmentsFile = new File(plugin.getDataFolder(), "punishments.yml");
        if (!punishmentsFile.exists()) {
            plugin.saveResource("punishments.yml", false);
        }
        punishmentsConfig = YamlConfiguration.loadConfiguration(punishmentsFile);

        guiFile = new File(plugin.getDataFolder(), "gui.yml");
        if (!guiFile.exists()) {
            plugin.saveResource("gui.yml", false);
        }
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);

        loadPermissions();
    }

    private void loadPermissions() {
        permissionMap.clear();
        if (permissionsConfig.contains("permissions")) {
            for (String key : permissionsConfig.getConfigurationSection("permissions").getKeys(false)) {
                permissionMap.put(key, permissionsConfig.getString("permissions." + key));
            }
        }
    }

    public String getPermission(String key) {
        return permissionMap.getOrDefault(key, "easypunish." + key);
    }

    public String getVoiceChatNode() {
        return permissionsConfig.getString("voicechat-permission-node", "voicechat.muted");
    }

    public String getMessage(String path) {
        String msg = config.getString("messages." + path, "");
        return ColorUtil.color(msg);
    }

    public String getPrefix() {
        return ColorUtil.color(config.getString("prefix", "&8[&bEasyPunish&8] "));
    }

    public FileConfiguration getConfig() { return config; }
    public FileConfiguration getPermissionsConfig() { return permissionsConfig; }
    public FileConfiguration getPunishmentsConfig() { return punishmentsConfig; }
    public FileConfiguration getGuiConfig() { return guiConfig; }
}
