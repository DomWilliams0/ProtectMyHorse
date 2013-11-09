package com.supaham.protectmyhorse;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.bukkit.util.BukkitCommandsManager;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import com.supaham.protectmyhorse.commands.PMHCommands;
import com.supaham.protectmyhorse.commands.PMHCommands.PMHParentCommand;
import com.supaham.protectmyhorse.configuration.ConfigHandler;
import com.supaham.protectmyhorse.protection.GlobalProtectionManager;
import com.supaham.protectmyhorse.protection.ProtectedHorse;
import com.supaham.protectmyhorse.protection.ProtectionManager;

public class ProtectMyHorse extends JavaPlugin {

    private ConfigHandler configHandler;
    private Permission permission;
    private Economy economy;
    private CommandsManager<CommandSender> commands;
    private GlobalProtectionManager globalManager;
    private PMHListener listener;

    @Override
    public void onEnable() {

        try {
            this.configHandler = new ConfigHandler(this);
            this.commands = new BukkitCommandsManager();
            loadDependencies();
            registerCommands();
            this.globalManager = new GlobalProtectionManager(this);
            getServer().getPluginManager().registerEvents(
                    setListener(new PMHListener(this)), this);

        } catch (Exception e) {
            getLogger().severe("Error occured while enabling plugin: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {

        getGlobalManager().saveAll();
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command cmd,
            String commandLabel,
            String[] args) {

        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED
                        + "Number expected, string received instead.");
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    /**
     * Loads the dependencies.
     */
    public void loadDependencies(){

        Plugin vault = getServer().getPluginManager().getPlugin("Vault");
        if (vault == null) {
            getLogger().warning("Vault could not be found, please install it to enable better permission handling and economy.");
        } else {
            RegisteredServiceProvider<Permission> permProv =
                    getServer().getServicesManager().getRegistration(Permission.class);
            if (permProv != null) {
                permission = (permProv.getProvider());
            }

            RegisteredServiceProvider<Economy> economyProvider =
                    getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
            } else {
                getLogger().warning("Economy plugin not found, please install one to be able to charge players.");
            }
        }
    }

    public void registerCommands() {

        CommandsManagerRegistration registration =
                new CommandsManagerRegistration(this, commands);
        registration.register(PMHParentCommand.class);
        PMHCommands.init(this);
    }

    /**
     * Checks if the {@code sender} has permission.
     * 
     * @param sender sender to check
     * @param permission permission to check, permission is prefixed with
     *            '<strong>pmh</strong>'.
     * @return true if the {@code sender} does have the {@code permission}, otherwise
     *         false
     */
    public boolean hasPermission(CommandSender sender, String permission) {

        permission = "pmh." + permission;

        if (this.permission != null) {
            return this.permission.has(sender, permission);
        } else {
            return sender.hasPermission(permission);
        }
    }

    /**
     * Checks if the {@code sender} has permission and notifies them.
     * 
     * @param sender sender to check
     * @param permission permission to check
     * @return false if the {@code sender} doesn't have the {@code permission}, otherwise
     *         true
     */
    public boolean checkPermissions(CommandSender sender, String permission) {

        if (!hasPermission(sender, permission)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission");
            return false;
        }
        return true;
    }

    /**
     * Checks if the {@code player} has the {@code amount} of currency in their account.
     * 
     * @param player player to check
     * @param amount amount to check
     * @return true if the there is no economy plugin or if the player has the amount,
     *         otherwise false
     */
    public boolean hasBalance(String player, double amount) {

        if (this.economy == null) {
            return true;
        }
        return this.economy.has(player, amount);
    }

    /**
     * Withdraws the {@code amount} of currency from {@code player}.
     * 
     * @param player player to withdraw from
     * @param amount amount to withdraw
     */
    public boolean withdraw(String player, double amount) {

        if (this.economy == null) {
            return false;
        }
        this.economy.withdrawPlayer(player, amount);
        return true;
    }

    /**
     * Deposits the {@code amount} of currency to the {@code player} balance.
     * 
     * @param player player account to deposit in
     * @param amount amount to deposit
     */
    public boolean deposit(String player, double amount) {

        if (this.economy == null) {
            return false;
        }
        this.economy.depositPlayer(player, amount);
        return true;
    }

    /**
     * Checks the amount of horses the {@code sender} can protect.
     * 
     * @param sender sender to check
     * @return the limit
     */
    public int getLockLimit(CommandSender sender) {

        if (hasPermission(sender, "lock.limit.-1")) {
            return -1;
        }
        for (int i = 100; i >= 0; i--) {
            if (hasPermission(sender, "lock.limit." + i)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Checks the amount of players the {@code sender} can add to each protected horse.
     * 
     * @param sender sender to check
     * @return the limit
     */
    public int getAddLimit(CommandSender sender) {

        if (hasPermission(sender, "add.limit.-1")) {
            return -1;
        }
        for (int i = 100; i >= 0; i--) {
            if (hasPermission(sender, "add.limit." + i)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Gets the total amount of horses the {@code sender} has protected.
     * 
     * @param sender sender to check
     * @return the total amount of {@link ProtectedHorse} owned by {@code sender}
     */
    public int getPlayerProtectedHorses(CommandSender sender) {

        int total = 0;
        for (ProtectionManager mgr : getGlobalManager().getProtectionManagers().values()) {
            for (ProtectedHorse horse : mgr.getProtectedHorses().values()) {
                if (horse.isOwner(sender.getName())) {
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * @return the configHandler
     */
    public ConfigHandler getConfigHandler() {

        return configHandler;
    }

    /**
     * @param configHandler the configHandler to set
     */
    public void setConfigHandler(ConfigHandler configHandler) {

        this.configHandler = configHandler;
    }

    /**
     * @return the permission
     */
    public Permission getPermission() {

        return permission;
    }

    /**
     * @param permission the permission to set
     */
    public void setPermission(Permission permission) {

        this.permission = permission;
    }

    /**
     * @return the economy
     */
    public Economy getEconomy() {

        return economy;
    }

    /**
     * @param economy the economy to set
     */
    public void setEconomy(Economy economy) {

        this.economy = economy;
    }

    /**
     * @return the commands
     */
    public CommandsManager<CommandSender> getCommands() {

        return commands;
    }

    /**
     * @param commands the commands to set
     */
    public void setCommands(CommandsManager<CommandSender> commands) {

        this.commands = commands;
    }

    /**
     * @return the globalManager
     */
    public GlobalProtectionManager getGlobalManager() {

        return globalManager;
    }

    /**
     * @param globalManager the globalManager to set
     */
    public void setGlobalManager(GlobalProtectionManager globalManager) {

        this.globalManager = globalManager;
    }

    /**
     * @return the listener
     */
    public PMHListener getListener() {

        return listener;
    }

    /**
     * @param listener the listener to set
     */
    public PMHListener setListener(PMHListener listener) {

        this.listener = listener;
        return listener;
    }
}
