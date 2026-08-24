package com.liskcell.easypunish.manager;

import com.liskcell.easypunish.EasyPunish;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AltsManager {
    private final EasyPunish plugin;
    private final File dataFolder;
    private final File altsFile;
    private FileConfiguration altsConfig;

    // Map<IP, Set<Username>>
    private final Map<String, Set<String>> ipToPlayers = new HashMap<>();
    // Map<Username, String IP>
    private final Map<String, String> playerToIp = new HashMap<>();
    // Map<Username, UUID>
    private final Map<String, UUID> playerToUuid = new HashMap<>();

    public AltsManager(EasyPunish plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.altsFile = new File(dataFolder, "alts.yml");
        loadAlts();
    }

    public void loadAlts() {
        try {
            if (!altsFile.exists()) {
                altsFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        altsConfig = YamlConfiguration.loadConfiguration(altsFile);

        ipToPlayers.clear();
        playerToIp.clear();
        playerToUuid.clear();

        if (altsConfig.contains("ips")) {
            for (String ipKey : altsConfig.getConfigurationSection("ips").getKeys(false)) {
                String cleanIp = ipKey.replace("_", ".");
                List<String> list = altsConfig.getStringList("ips." + ipKey + ".players");
                Set<String> players = new HashSet<>(list);
                ipToPlayers.put(cleanIp, players);

                for (String playerEntry : players) {
                    String[] parts = playerEntry.split(":");
                    String name = parts[0];
                    playerToIp.put(name.toLowerCase(), cleanIp);
                    if (parts.length > 1) {
                        try {
                            playerToUuid.put(name.toLowerCase(), UUID.fromString(parts[1]));
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
    }

    public void saveAlts() {
        altsConfig.set("ips", null);
        for (Map.Entry<String, Set<String>> entry : ipToPlayers.entrySet()) {
            String ipKey = entry.getKey().replace(".", "_");
            altsConfig.set("ips." + ipKey + ".players", new ArrayList<>(entry.getValue()));
        }
        try {
            altsConfig.save(altsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerPlayer(UUID uuid, String playerName, String ip) {
        String cleanIp = ip.replace("/", "").split(":")[0];
        playerToIp.put(playerName.toLowerCase(), cleanIp);
        playerToUuid.put(playerName.toLowerCase(), uuid);

        Set<String> set = ipToPlayers.computeIfAbsent(cleanIp, k -> new HashSet<>());
        set.add(playerName + ":" + uuid.toString());
        saveAlts();
    }

    public String getIpForPlayer(String playerName) {
        return playerToIp.get(playerName.toLowerCase());
    }

    public Set<String> getAlts(String playerName) {
        String ip = getIpForPlayer(playerName);
        if (ip == null) return Collections.emptySet();
        return ipToPlayers.getOrDefault(ip, Collections.emptySet());
    }

    public Map<String, UUID> getPlayerToUuidMap() {
        return playerToUuid;
    }
}
