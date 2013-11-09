package com.supaham.protectmyhorse.configuration;

import java.io.File;
import java.io.FileNotFoundException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.supaham.protectmyhorse.ProtectMyHorse;

/**
 * Represents a custom YAML file.
 * 
 * @author SupaHam
 * 
 */
public class SupaYaml {

    private FileConfiguration config;
    private File file;

    private ProtectMyHorse plugin;

    /**
     * Constructs a new CustomYaml file using a file name.
     * 
     * @param file file name
     */
    public SupaYaml(ProtectMyHorse instance, String fileName) {

        this.plugin = instance;
        file = new File(plugin.getDataFolder(), fileName + ".yml");
    }

    /**
     * Reloads the YamlConfiguration.
     */
    public void reloadConfig() {

        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Saves the FileConfiguration.
     */
    public void saveConfig() {

        if (config == null)
            return;
        try {
            config.save(file);
        } catch (FileNotFoundException ex) {
        } catch (Exception e) {
            Bukkit.getLogger().severe(
                    "Error occured while saving file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets the FileConfiguration. If it null it calls {@link #reloadConfig()}
     * 
     * @return FileConfiguration
     */
    public FileConfiguration getConfig() {

        return getConfig(false);
    }

    /**
     * Gets the FileConfiguration. If it is null, it calls {@link #reloadConfig()}
     * 
     * @param reload if true, it calls {@link #reloadConfig()}
     * @return the FileConfiguration, cannot be null.
     */
    public FileConfiguration getConfig(boolean reload) {

        if (config == null || reload)
            reloadConfig();
        return config;
    }

    /**
     * Whether the file exists.
     * 
     * @return true if the file exists.
     */
    public boolean exists() {

        return file.exists();
    }

    /**
     * Exports the default file from the jar file.
     */
    public void saveDefaultConfig() {

        if (exists())
            return;
        plugin.saveResource(file.getName(), false);
    }
}
