package com.supaham.protectmyhorse.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.supaham.protectmyhorse.ProtectMyHorse;

public class ConfigHandler {

    private ProtectMyHorse plugin;
    private SupaYaml file;

    //@formatter:off
    public static Property<Double> PRICE_TO_LOCK = new Property<Double>("price-to-lock", 10D);
    public static Property<Double> PRICE_TO_UNLOCK = new Property<Double>("price-to-unlock", 10D);
    public static Property<Boolean> SPAWN_FIREWORK = new Property<Boolean>("spawn-firework", true);
    public static Property<List<String>> ENABLED_WORLDS = new Property<List<String>>("enabled-worlds", 
            new ArrayList<String>(Arrays.asList("world", "world_nether", "world_the_end")));
    public static Property<Boolean> MUST_TAME_HORSE = new Property<Boolean>("must-tame-horse", true);
    //@formatter:on

    public ConfigHandler(ProtectMyHorse instance) {

        this.plugin = instance;
        load();
    }

    /**
     * Loads the configuration file.
     */
    public void load() {

        this.file = new SupaYaml(plugin, "config");
        this.file.saveDefaultConfig();
    }

    /**
     * Reloads the config.yml file.
     */
    public void reload() {

        this.file.saveDefaultConfig();
        this.file.reloadConfig();
    }

    /**
     * Gets the FileConfiguration.
     * 
     * @return FileConfiguration
     */
    public FileConfiguration getConfig() {

        return this.file.getConfig();
    }

    /**
     * Gets a long value from a property.
     * 
     * @param property property to get the value from
     * @return the value
     */
    public long getLong(Property<Long> property) {

        return getConfig().getLong(property.getPath(), property.getDefaultValue());
    }

    /**
     * Gets an int value from a property.
     * 
     * @param property property to get the value from
     * @return the value
     */
    public int getInt(Property<Integer> property) {

        return getConfig().getInt(property.getPath(), property.getDefaultValue());
    }

    /**
     * Gets a double value from a property.
     * 
     * @param property property to get the value from
     * @return the value
     */
    public double getDouble(Property<Double> property) {

        return getConfig().getDouble(property.getPath(), property.getDefaultValue());
    }

    /**
     * Gets a boolean value from a property.
     * 
     * @param property property to get the value from
     * @return the value
     */
    public boolean getBoolean(Property<Boolean> property) {

        return getConfig().getBoolean(property.getPath(), property.getDefaultValue());
    }

    /**
     * Gets a string value from a property.
     * 
     * @param property property to get the value from
     * @return the value
     */
    public String getString(Property<String> property) {

        return getConfig().getString(property.getPath(), property.getDefaultValue());
    }

    /**
     * Gets a string list value from a property.
     * 
     * @param property property to get the value from
     * @return the value
     */
    public List<String> getStringList(Property<List<String>> property) {

        return getConfig().getStringList(property.getPath());
    }
}
