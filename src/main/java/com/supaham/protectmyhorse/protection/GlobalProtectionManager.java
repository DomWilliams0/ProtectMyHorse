package com.supaham.protectmyhorse.protection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.supaham.protectmyhorse.ProtectMyHorse;
import com.supaham.protectmyhorse.configuration.ConfigHandler;

/**
 * Represents a global protection manager that controls all {@link ProtectionManager}s.
 * 
 * @author SupaHam
 * 
 */
public class GlobalProtectionManager {

    private ProtectMyHorse plugin;

    private Map<String, ProtectionManager> protectionManagers;

    public GlobalProtectionManager(ProtectMyHorse instance) {

        this.plugin = instance;
        this.protectionManagers = new HashMap<>();
        checkWorlds();
    }

    /**
     * Checks what worlds should be listened for depending on the configuration file.
     */
    public void checkWorlds() {

        List<String> list =
                plugin.getConfigHandler().getStringList(ConfigHandler.ENABLED_WORLDS);

        for (String world : list)
            addProtectionManager(world, new ProtectionManager(new YamlDatabase(plugin,
                    world)));
    }
    
    public void saveAll(){
        for(ProtectionManager mgr : getProtectionManagers().values()){
            mgr.getDatabase().save(mgr.getProtectedHorses());
        }
    }

    /**
     * @return the map of {@link ProtectionManager}s
     */
    public Map<String, ProtectionManager> getProtectionManagers() {

        return protectionManagers;
    }

    /**
     * @param protectionManager the map of {@link ProtectionManager}s
     */
    public void setProtectionManager(Map<String, ProtectionManager> protectionManagers) {

        this.protectionManagers = protectionManagers;
    }

    public ProtectionManager getProtectionManager(String world) {

        return this.protectionManagers.get(world.toLowerCase());
    }

    /**
     * Adds {@code world} as a protected manager.
     * 
     * @param world world to add
     * @param protectionManager protection manager belonging to world
     */
    public void addProtectionManager(String world, ProtectionManager protectionManager) {

        this.protectionManagers.put(world.toLowerCase(), protectionManager);
    }

    /**
     * Adds {@code world} as protected.
     * 
     * @param world world to add
     * @param protectionManager protection manager belonging to world
     */
    public ProtectionManager removeProtectionManager(String world) {

        return this.protectionManagers.remove(world.toLowerCase());
    }
}
