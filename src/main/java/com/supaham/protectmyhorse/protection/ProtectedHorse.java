package com.supaham.protectmyhorse.protection;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

/**
 * Represents a protected horse with a UUID and an owner.
 * 
 * @author SupaHam
 * 
 */
public class ProtectedHorse {

    private String owner;
    private List<String> listedPlayers;
    private Type type;

    /**
     * Represents a type of protection for a {@link ProtectedHorse}.
     * 
     * @author SupaHam
     * 
     */
    public static enum Type {
        WHITELIST(),
        BLACKLIST();

        /**
         * Gets a type by {@code name}.
         * 
         * @param typeName name being {@link #toString()}
         * @return the type, otherwise null if it doesn't exist
         */
        public static Type getType(String typeName) {

            for (Type type : values()) {
                if (type.toString().equals(typeName)) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String toString() {

            return this.name().toLowerCase();
        };
    }

    public ProtectedHorse(String owner) {

        this.owner = owner.toLowerCase();
        this.type = Type.WHITELIST;
        this.listedPlayers = new ArrayList<>();
    }

    /**
     * Checks whether the {@code player} can ride this horse.
     * 
     * @param player player to check
     * @return true if the {@code player} can ride the horse
     */
    public boolean canRide(Player player) {

        String name = player.getName().toLowerCase();
        if (name.equals(owner)) {
            return true;
        }

        if (type == Type.WHITELIST) {
            if (listedPlayers.contains(name)) {
                return true;
            }
        } else if (type == Type.BLACKLIST) {
            if (!listedPlayers.contains(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets this horse's owner.
     * 
     * @return the owner
     */
    public String getOwner() {

        return owner;
    }

    /**
     * Sets this horse's owner.
     * 
     * @param owner the owner to set
     */
    public void setOwner(String owner) {

        this.owner = owner;
    }

    /**
     * Checks if the {@code player} is the owner of this horse.
     * 
     * @param player player to check
     * @return true if the {@code player} is the owner of this horse
     */
    public boolean isOwner(String player) {

        return this.owner.equalsIgnoreCase(player);
    }

    /**
     * Gets this horse's listed players, depending on the type of the horse protection
     * they will be whitelisted/blacklisted.
     * 
     * @return the list of players
     */
    public List<String> getListedPlayers() {

        return listedPlayers;
    }

    /**
     * Sets this horse's listed players, depending on the type of the horse protection
     * they will be whitelisted/blacklisted.
     * 
     * @param listedPlayers a list of players
     */
    public void setListedPlayers(List<String> listedPlayers) {

        this.listedPlayers = listedPlayers;
    }

    /**
     * Checks whether the {@code player} exists in the listed players.
     * 
     * @param player player to check
     * @return true if the player is on the list, otherwise false
     */
    public boolean containsPlayer(String player) {

        return this.listedPlayers.contains(player.toLowerCase());
    }

    /**
     * Adds the {@code player} to the list of players.
     * 
     * @param player player to add
     * @return true if the {@code player} was added, otherwise false if the
     *         {@code player} already exists
     */
    public boolean addPlayer(String player) {

        if (containsPlayer(player)) {
            return false;
        }
        this.listedPlayers.add(player.toLowerCase());
        return true;
    }

    /**
     * Removes the {@code player} from the list of players.
     * 
     * @param player player to remove
     * @return true if the {@code player} was removed, otherwise false if the
     *         {@code player} doesn't exists
     */
    public boolean removePlayer(String player) {

        if (!containsPlayer(player)) {
            return false;
        }
        this.listedPlayers.remove(player.toLowerCase());
        return true;
    }

    /**
     * Gets the type of horse protection belonging to this horse.
     * 
     * @return the type
     */
    public Type getType() {

        return type;
    }

    /**
     * Sets the type of horse protection belonging to this horse.
     * 
     * @param type the type to set
     */
    public void setType(Type type) {

        this.type = type;
    }

}
