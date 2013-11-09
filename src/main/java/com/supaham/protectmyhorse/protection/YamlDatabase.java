package com.supaham.protectmyhorse.protection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.supaham.protectmyhorse.ProtectMyHorse;
import com.supaham.protectmyhorse.configuration.SupaYaml;
import com.supaham.protectmyhorse.protection.ProtectedHorse.Type;

public class YamlDatabase implements Database {

    private ProtectMyHorse plugin;
    private String fileName;
    private SupaYaml supaYaml;

    /**
     * Constructs a new {@link YamlDatabase} for retrieving and storing
     * {@link ProtectedHorse} data.
     * 
     * @param instance instance of {@link ProtectMyHorse}
     * @param name name of the file, typically would be the name of the world
     */
    public YamlDatabase(ProtectMyHorse instance, String name) {

        this.plugin = instance;
        this.fileName = name;
    }

    @Override
    public void initialize() {

        this.supaYaml = new SupaYaml(plugin, this.fileName);
        this.supaYaml.saveConfig();
    }

    @Override
    public Map<String, ProtectedHorse> load() {

        FileConfiguration config = this.supaYaml.getConfig();
        ConfigurationSection cs = config.getConfigurationSection("horses");
        Map<String, ProtectedHorse> horses = new HashMap<>();

        if (cs == null) {
            return horses;
        }

        for (String uuid : cs.getKeys(false)) {
            ConfigurationSection temp = cs.getConfigurationSection(uuid);
            String owner = temp.getString("owner");
            Type type = Type.getType(temp.getString("type"));
            List<String> players = temp.getStringList("players");

            ProtectedHorse horse = new ProtectedHorse(owner);
            horse.setType(type);
            horse.setListedPlayers(players);
            horses.put(uuid, horse);
        }
        return horses;
    }

    @Override
    public void save(Map<String, ProtectedHorse> map) {

        FileConfiguration config = this.supaYaml.getConfig();
        ConfigurationSection cs = config.getConfigurationSection("horses");

        if (cs == null) {
            cs = config.createSection("horses");
        }

        for (Map.Entry<String, ProtectedHorse> entry : map.entrySet()) {
            String uuid = entry.getKey();
            ProtectedHorse horse = entry.getValue();
            String owner = horse.getOwner();

            ConfigurationSection temp = cs.getConfigurationSection(uuid);
            if (temp == null) {
                temp = cs.createSection(uuid);
            }

            temp.set("owner", owner);
            temp.set("type", horse.getType().toString());
            temp.set("players", horse.getListedPlayers());
        }
        this.supaYaml.saveConfig();
    }
}
