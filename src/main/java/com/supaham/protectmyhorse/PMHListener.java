package com.supaham.protectmyhorse;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.supaham.protectmyhorse.actions.Actions;
import com.supaham.protectmyhorse.actions.Actions.Action;
import com.supaham.protectmyhorse.actions.Actions.Add;
import com.supaham.protectmyhorse.actions.Actions.Info;
import com.supaham.protectmyhorse.actions.Actions.Lock;
import com.supaham.protectmyhorse.actions.Actions.PlayerAction;
import com.supaham.protectmyhorse.actions.Actions.Remove;
import com.supaham.protectmyhorse.actions.Actions.Unlock;
import com.supaham.protectmyhorse.configuration.ConfigHandler;
import com.supaham.protectmyhorse.protection.ProtectedHorse;
import com.supaham.protectmyhorse.protection.ProtectedHorse.Type;
import com.supaham.protectmyhorse.protection.ProtectionManager;
import com.supaham.protectmyhorse.util.FireworkUtil;

public class PMHListener implements Listener {

   private static final Set<PotionEffectType> HARMFUL_POTIONS;

    static {
        HARMFUL_POTIONS = new HashSet<>();
        HARMFUL_POTIONS.add(PotionEffectType.HARM);
        HARMFUL_POTIONS.add(PotionEffectType.POISON);
        HARMFUL_POTIONS.add(PotionEffectType.SLOW);
        HARMFUL_POTIONS.add(PotionEffectType.WEAKNESS);
        HARMFUL_POTIONS.add(PotionEffectType.SLOW_DIGGING);
        HARMFUL_POTIONS.add(PotionEffectType.BLINDNESS);
        HARMFUL_POTIONS.add(PotionEffectType.CONFUSION);
        HARMFUL_POTIONS.add(PotionEffectType.HUNGER);
        HARMFUL_POTIONS.add(PotionEffectType.WITHER);
    }


    private ProtectMyHorse plugin;
    private Map<String, Action> actingPlayers;

    public PMHListener(ProtectMyHorse instance) {

        this.plugin = instance;
        this.actingPlayers = new HashMap<>();
    }

    @EventHandler
    public void onHorseDamage(EntityDamageEvent evt) {
        if (!(evt.getEntity() instanceof Horse))
            return;

        ProtectionManager mgr = plugin.getGlobalManager().getProtectionManager(evt.getEntity().getWorld().getName());
        if (mgr == null)
            return;

        ProtectedHorse horse = mgr.getProtectedHorse(evt.getEntity().getUniqueId().toString());

        if (horse == null)
            return;


        // block possible player inflicted damage
        if (evt instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) evt;

            Player damager;

            // melee
            if (event.getDamager() instanceof Player)
                damager = (Player) event.getDamager();
            else if (event.getDamager() instanceof Projectile) {
                // projectile
                Projectile p = (Projectile) event.getDamager();
                if (!(p.getShooter() instanceof Player))
                    return;
                else
                    damager = (Player) p.getShooter();
            } else
                return; // wasn't melee or projectile

            // check player
            if (!horse.canRide(damager)) {
                evt.setCancelled(true);
                damager.sendMessage(ChatColor.RED + "You can't attack that horse!");
                damager.damage(0D); // slap
            }

        }

        // block all other damage regardless
        else
            evt.setCancelled(true);
    }

    @EventHandler
    public void onPotion(PotionSplashEvent event) {
        // is potion harmful
        if (!isHarmfulPotion(event.getPotion()))
            return;

        ProtectionManager mgr = plugin.getGlobalManager().getProtectionManager(event.getPotion().getWorld().getName());
        if (mgr == null)
            return;

        for (Iterator<LivingEntity> iterator = event.getAffectedEntities().iterator(); iterator.hasNext(); ) {
            LivingEntity e = iterator.next();
            if (e.getType() == EntityType.HORSE) {
                ProtectedHorse horse = mgr.getProtectedHorse(e.getUniqueId().toString());
                if (horse == null)
                    return;

                if (event.getPotion().getShooter() instanceof Player) {
                    Player p = (Player) event.getPotion().getShooter();
                    if (!horse.canRide(p)) // cancel if potion thrower doesn't have access to horse
                        iterator.remove();
                }

            }

        }

    }

    private boolean isHarmfulPotion(ThrownPotion thrownPotion) {
        for (PotionEffect e : thrownPotion.getEffects())
            if (HARMFUL_POTIONS.contains(e.getType()))
                return true;
        return false;
    }

    @EventHandler
    public void onInteractWithHorse(PlayerInteractEntityEvent evt) {

        if (evt.getRightClicked() instanceof Horse) {
            World world = evt.getRightClicked().getWorld();
            ProtectionManager mgr =
                    plugin.getGlobalManager().getProtectionManager(world.getName());
            Player player = evt.getPlayer();
            if (mgr != null) {
                if (contains(player)) {
                    handleInteraction(evt, mgr);
                    evt.setCancelled(true);
                } else {
                    handleRiding(evt, mgr);
                }
            }
        }
    }

    private void handleInteraction(PlayerInteractEntityEvent evt, ProtectionManager mgr) {

        Player player = evt.getPlayer();
        Horse horse = (Horse) evt.getRightClicked();
        AnimalTamer owner = horse.getOwner();
        Action action = removeActingPlayer(player);

        if (action.requiresTaming()
                && plugin.getConfigHandler().getBoolean(ConfigHandler.MUST_TAME_HORSE)) {
            if (owner == null) {
                player.sendMessage(ChatColor.RED
                        + "You must tame that horse to interact with ProtectMyHorse!");
                return;
            } else if (!player.getName().equalsIgnoreCase(owner.getName())) {
                player.sendMessage(ChatColor.RED
                        + "HEY! That horse is tamed by someone else!");
                return;
            }
        }

        String uuid = horse.getUniqueId().toString();
        if (action instanceof Lock) {
            if (handleActionLock(uuid, mgr, player)) {
                spawnFirework(evt.getRightClicked().getLocation());
            }
        } else if (action instanceof Unlock) {
            handleActionUnlock(uuid, mgr, player);
        } else if (action instanceof Add) {
            handleActionAdd(uuid, mgr, player, ((PlayerAction) action).getPlayerName());
        } else if (action instanceof Remove) {
            handleActionRemove(uuid, mgr, player,
                    ((PlayerAction) action).getPlayerName());
        } else if (action instanceof Actions.Type) {
            handleActionType(uuid, mgr, player, ((Actions.Type) action).getType());
        } else if (action instanceof Info) {
            handleActionInfo(uuid, mgr, player);
        }
    }

    /**
     * Handles {@code player} locking a horse.
     * 
     * @param uuid uuid of the horse
     * @param mgr {@link ProtectionManager}
     * @param player player locking the horse
     * @return true if the horse has successfully been locked, otherwise false
     */
    private boolean handleActionLock(String uuid, ProtectionManager mgr, Player player) {

        if (!plugin.checkPermissions(player, "lock")) {
            return false;
        }

        if (mgr.containsHorse(uuid)) {
            ProtectedHorse protectedHorse = mgr.getProtectedHorse(uuid);

            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.RED + "That horse is ");

            if (protectedHorse.isOwner(player.getName()))
                sb.append("already owned by you.");
            else
                sb.append("owned by someone else.");
            player.sendMessage(sb.toString());
            return false;
        }

        int limit = plugin.getLockLimit(player);
        if (limit > -1) {
            if (plugin.getPlayerProtectedHorses(player) >= limit) {
                player.sendMessage(ChatColor.RED
                        + "You have reached your limit of horse protection.");
                return false;
            }
        }
        double price = plugin.getConfigHandler().getDouble(ConfigHandler.PRICE_TO_LOCK);
        if (price > 0) {
            if (!plugin.hasBalance(player.getName(), price)) {
                double missing =
                        price - plugin.getEconomy().getBalance(player.getName());
                StringBuilder sb = new StringBuilder();
                sb.append(ChatColor.RED);
                sb.append("You can not afford to protect this horse, you need ");
                sb.append(missing + " more ");
                sb.append(getCurrencyName(missing, false));
                sb.append(".");
                player.sendMessage(sb.toString());
                return false;
            }
            if (plugin.withdraw(player.getName(), price)) {
                player.sendMessage(ChatColor.GREEN.toString() + price + " "
                        + ChatColor.YELLOW + getCurrencyName(price, true)
                        + " deducted from your balance.");
            }
        }
        mgr.addHorse(uuid, new ProtectedHorse(player.getName()));
        player.sendMessage(ChatColor.YELLOW + "You successfully protected that horse!");
        return true;
    }

    private void handleActionUnlock(String uuid, ProtectionManager mgr, Player player) {

        if (!plugin.checkPermissions(player, "unlock")) {
            return;
        }

        if (!mgr.containsHorse(uuid)) {
            player.sendMessage(ChatColor.RED + "That horse isn't protected.");
            return;
        } else {
            ProtectedHorse horse = mgr.getProtectedHorse(uuid);
            if (!horse.isOwner(player.getName())) {
                player.sendMessage(ChatColor.RED
                        + "Nice try... that horse belongs to someone else!");
                return;
            } else {
                double refund =
                        plugin.getConfigHandler().getDouble(
                                ConfigHandler.PRICE_TO_UNLOCK);
                if (plugin.deposit(player.getName(), refund)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ChatColor.GREEN + "" + refund + " ");
                    sb.append(ChatColor.YELLOW);
                    sb.append(getCurrencyName(refund, true));
                    sb.append(" added to your account.");
                    player.sendMessage(sb.toString());
                }
                mgr.removeHorse(uuid);
                player.sendMessage(ChatColor.YELLOW
                        + "You successfully unprotected that horse!");
            }
        }
    }

    private void handleActionAdd(
            String uuid,
            ProtectionManager mgr,
            Player player,
            String target) {

        if (!plugin.checkPermissions(player, "add")) {
            return;
        }

        if (!mgr.containsHorse(uuid)) {
            player.sendMessage(ChatColor.RED + "That horse isn't protected.");
            return;
        } else {
            ProtectedHorse horse = mgr.getProtectedHorse(uuid);
            if (!horse.isOwner(player.getName())) {
                player.sendMessage(ChatColor.RED
                        + "Nice try... that horse belongs to someone else!");
                return;
            } else {
                int limit = plugin.getAddLimit(player);
                if (limit > -1) {
                    if (horse.getListedPlayers().size() >= limit) {
                        player.sendMessage("You have reached your limit to adding players to this horse");
                        return;
                    }
                }

                if (horse.addPlayer(target)) {
                    player.sendMessage(ChatColor.YELLOW + "You successfully added "
                            + target + " to the players list.");
                } else {
                    player.sendMessage(ChatColor.RED + target
                            + " is already on the player list.");
                }
            }
        }
    }

    private void handleActionRemove(
            String uuid,
            ProtectionManager mgr,
            Player player,
            String target) {

        if (!plugin.checkPermissions(player, "remove")) {
            return;
        }

        if (!mgr.containsHorse(uuid)) {
            player.sendMessage(ChatColor.RED + "That horse isn't protected.");
            return;
        } else {
            ProtectedHorse horse = mgr.getProtectedHorse(uuid);
            if (!horse.isOwner(player.getName())) {
                player.sendMessage(ChatColor.RED
                        + "Nice try... that horse belongs to someone else!");
                return;
            } else {
                if (horse.removePlayer(target)) {
                    player.sendMessage(ChatColor.YELLOW + "You successfully removed "
                            + target + " from the players list.");
                } else {
                    player.sendMessage(ChatColor.RED + target
                            + " is not on the player list.");
                }
            }
        }
    }

    private void handleActionType(
            String uuid,
            ProtectionManager mgr,
            Player player,
            Type type) {

        if (!plugin.checkPermissions(player, "type." + type.toString())) {
            return;
        }

        if (!mgr.containsHorse(uuid)) {
            player.sendMessage(ChatColor.RED + "That horse isn't protected.");
            return;
        } else {
            ProtectedHorse horse = mgr.getProtectedHorse(uuid);
            if (!horse.isOwner(player.getName())) {
                player.sendMessage(ChatColor.RED
                        + "Nice try... that horse belongs to someone else!");
                return;
            } else {
                if (horse.getType() == type) {
                    player.sendMessage(ChatColor.RED + "That horse's type is already "
                            + type.toString());
                } else {
                    horse.setType(type);
                    player.sendMessage(ChatColor.YELLOW
                            + "You successfully set the horse's new type to "
                            + type.toString());
                }
            }
        }
    }

    private void handleActionInfo(String uuid, ProtectionManager mgr, Player player) {

        if (!plugin.checkPermissions(player, "info")) {
            return;
        }

        if (!mgr.containsHorse(uuid)) {
            player.sendMessage(ChatColor.RED + "That horse isn't protected.");
            return;
        } else {
            ProtectedHorse horse = mgr.getProtectedHorse(uuid);
            player.sendMessage(ChatColor.YELLOW + "Owner: " + ChatColor.AQUA
                    + horse.getOwner());
            player.sendMessage(ChatColor.YELLOW + "Type: " + ChatColor.AQUA
                    + horse.getType().toString());

            StringBuilder sb = new StringBuilder();

            for (String horsePlayer : horse.getListedPlayers()) {
                sb.append(horsePlayer + ", ");
            }
            if (sb.length() > 0) {
                sb.insert(0, ChatColor.AQUA);
                sb.delete(sb.length() - 2, sb.length());
            } else {
                sb.append(ChatColor.RED + "None");
            }

            player.sendMessage(ChatColor.YELLOW + "Players: " + sb.toString());
        }
    }

    /**
     * Handles a player involved in the {@code evt} trying to ride a protected horse.
     * 
     * @param evt {@link PlayerInteractEntityEvent}
     * @param mgr {@link ProtectionManager} of the world the event is occurring in
     */
    private void handleRiding(PlayerInteractEntityEvent evt, ProtectionManager mgr) {

        String uuid = evt.getRightClicked().getUniqueId().toString();
        ProtectedHorse horse = mgr.getProtectedHorse(uuid);
        if (horse != null && !horse.canRide(evt.getPlayer())) {
            evt.getPlayer().sendMessage(ChatColor.RED + "You can not ride that horse!");
            evt.setCancelled(true);
        }
    }

    private void spawnFirework(Location location) {

        if (plugin.getConfigHandler().getBoolean(ConfigHandler.SPAWN_FIREWORK)) {
            FireworkUtil.getRandomFirework(location);
        }
    }

    public String getCurrencyName(double amount, boolean past) {

        String message = "";
        if (amount == 1) {
            message += plugin.getEconomy().currencyNameSingular();
            if (past)
                message.concat(" was");
        } else {
            message += plugin.getEconomy().currencyNamePlural();
            if (past)
                message.concat(" were");
        }
        return message;
    }

    /**
     * Gets a Map of players that should be listened for when interacting with a horse.
     * 
     * @return the map of acting players
     */
    public Map<String, Action> getActingPlayers() {

        return actingPlayers;
    }

    /**
     * Sets a Map of players that should be listened for when interacting with a horse.
     * 
     * @param actingPlayers the map of acting players
     */
    public void setActingPlayers(Map<String, Action> actingPlayers) {

        this.actingPlayers = actingPlayers;
    }

    /**
     * Checks if the {@code player} is acting.
     * 
     * @param players player to check
     * @return true if the player is acting, otherwise false
     */
    public boolean contains(Player player) {

        return this.actingPlayers.containsKey(player.getName().toLowerCase());
    }

    /**
     * Gets the {@link Action} the {@code player} is performing.
     * 
     * @param player player to get Action from
     * @return the Action if the player is acting, otherwise null
     */
    public Action getActingPlayer(Player player) {

        return this.actingPlayers.get(player.getName().toLowerCase());
    }

    /**
     * Adds the {@code player} to the map of acting players with the {@code action}
     * 
     * @param player player to add
     * @param action action the player is performing
     */
    public void addActingPlayer(Player player, Action action) {

        this.actingPlayers.put(player.getName().toLowerCase(), action);
    }

    /**
     * Removes the {@code player} from the map of acting players.
     * 
     * @param player player to remove
     * @return the action that belonged to the player
     */
    public Action removeActingPlayer(Player player) {

        return this.actingPlayers.remove(player.getName().toLowerCase());
    }
}
