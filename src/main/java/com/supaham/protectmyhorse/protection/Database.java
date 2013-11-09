package com.supaham.protectmyhorse.protection;

import java.util.Map;

public interface Database {

    /**
     * Initializes the database storing data. Only needed to be used when the plugin is
     * enabling.
     */
    void initialize();

    /**
     * Loads the plugin data from the database.
     */
    Map<String, ProtectedHorse> load();

    /**
     * Saves the plugin data to the database.
     */
    void save(Map<String, ProtectedHorse> map);
}
