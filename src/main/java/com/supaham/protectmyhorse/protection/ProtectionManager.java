package com.supaham.protectmyhorse.protection;

import java.util.HashMap;
import java.util.Map;

public class ProtectionManager {

    private Database database;

    private Map<String, ProtectedHorse> protectedHorses = new HashMap<>();

    public ProtectionManager(Database database) {

        this.setDatabase(database);
        this.database.initialize();
        this.protectedHorses = this.database.load();
    }

    /**
     * Gets the database that stores data about protected horses.
     * 
     * @return the database
     */
    public Database getDatabase() {

        return database;
    }

    /**
     * Sets the database that stores data about protected horses.
     * 
     * @param database the database
     */
    public void setDatabase(Database database) {

        this.database = database;
    }

    /**
     * Gets a Map of protected horses the Key is a horse's uuid and the value is the
     * {@link ProtectedHorse} object.
     * 
     * @return the map of protected horses
     */
    public Map<String, ProtectedHorse> getProtectedHorses() {

        return protectedHorses;
    }

    /**
     * Sets a Map of protected horses the Key is a horse's uuid and the value is the
     * {@link ProtectedHorse} object.
     * 
     * @param protectedHorses the map of protected horses
     */
    public void setProtectedHorses(Map<String, ProtectedHorse> protectedHorses) {

        this.protectedHorses = protectedHorses;
    }

    /**
     * Checks whether this manager contains a horse with the {@code uuid}.
     * 
     * @param uuid uuid to check
     * @return true if there is a horse with the {@code uuid}, otherwise false
     */
    public boolean containsHorse(String uuid) {

        return this.protectedHorses.containsKey(uuid);
    }

    /**
     * Gets a {@link ProtectedHorse} registered with the provided {@code uuid}.
     * 
     * @return the {@link ProtectedHorse} if it exists, otherwise null
     */
    public ProtectedHorse getProtectedHorse(String uuid) {

        return protectedHorses.get(uuid);
    }

    /**
     * Adds a horse UUID to the list of protected horses.
     * 
     * @param uuid uuid of the horse
     * @param owner owner of the horse
     */
    public void addHorse(String uuid, ProtectedHorse horse) {

        this.protectedHorses.put(uuid, horse);
    }

    /**
     * Removes a horse with the provided {@code uuid}.
     * 
     * @param uuid uuid of the horse
     * @return the {@link ProtectedHorse} object, otherwise null if no uuid could be
     *         found
     */
    public ProtectedHorse removeHorse(String uuid) {

        return this.protectedHorses.remove(uuid);
    }
}
