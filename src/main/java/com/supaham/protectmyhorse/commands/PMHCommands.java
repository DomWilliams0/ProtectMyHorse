package com.supaham.protectmyhorse.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.supaham.protectmyhorse.ProtectMyHorse;
import com.supaham.protectmyhorse.actions.Actions;
import com.supaham.protectmyhorse.protection.ProtectedHorse.Type;

public class PMHCommands {

    private static ProtectMyHorse plugin;

    public static void init(ProtectMyHorse instance) {

        plugin = instance;
    }

    public static class PMHParentCommand {

        @Command(aliases = { "pmh", "protectmyhorse" },
                desc = "ProtectMyHorse plugin commands.")
        @NestedCommand(value = { PMHCommands.class })
        public static void pmh(CommandContext args, CommandSender sender) {

        }
    }

    // TODO add commands:
    // lock, unlock, add, remove (what is the usage for these)
    @Command(aliases = { "lock", "l", "protect" },
            desc = "Sets the player in a protecting horse state.",
            min = 0,
            max = 0)
    public static void lock(CommandContext args, CommandSender sender)
            throws CommandException {

        if (!(sender instanceof Player)) {
            throw new CommandException(
                    "Silly you! You'll never be able to protect a horse!");
        }

        Player player = (Player) sender;
        plugin.getListener().addActingPlayer(player, new Actions.Lock() {
        });
        player.sendMessage(ChatColor.YELLOW
                + "Right click the horse you wish to protect.");
    }

    @Command(aliases = { "unlock", "u", "unprotect" },
            desc = "Sets the player in an uprotecting horse state.",
            min = 0,
            max = 0)
    public static void unlock(CommandContext args, CommandSender sender)
            throws CommandException {

        if (!(sender instanceof Player)) {
            throw new CommandException(
                    "Silly you! You'll never be able to unprotect a horse!");
        }

        Player player = (Player) sender;
        plugin.getListener().addActingPlayer(player, new Actions.Unlock() {
        });
        player.sendMessage(ChatColor.YELLOW
                + "Right click the horse you wish to set free.");
    }

    @Command(aliases = { "add", "a" },
            desc = "Adds a player to the player list of a horse.",
            usage = "<player>",
            min = 1,
            max = 1)
    public static void add(CommandContext args, CommandSender sender)
            throws CommandException {

        if (!(sender instanceof Player)) {
            throw new CommandException(
                    "Silly you! You'll never be able to add a player to a horse!");
        }

        Player player = (Player) sender;
        final String target = args.getString(0);
        plugin.getListener().addActingPlayer(player, new Actions.Add() {

            @Override
            public String getPlayerName() {

                return target;
            }
        });
        player.sendMessage(ChatColor.YELLOW + "Right click the horse you wish to add "
                + ChatColor.AQUA + target + ChatColor.YELLOW + " to.");
    }

    @Command(aliases = { "remove", "rm", "r" },
            desc = "Removes a player from the player list of a horse.",
            usage = "<player>",
            min = 1,
            max = 1)
    public static void remove(CommandContext args, CommandSender sender)
            throws CommandException {

        if (!(sender instanceof Player)) {
            throw new CommandException(
                    "Silly you! You'll never be able to remove a player from a horse!");
        }

        Player player = (Player) sender;
        final String target = args.getString(0);
        plugin.getListener().addActingPlayer(player, new Actions.Remove() {

            @Override
            public String getPlayerName() {

                return target;
            }
        });
        player.sendMessage(ChatColor.YELLOW
                + "Right click the horse you wish to remove " + ChatColor.AQUA + target
                + ChatColor.YELLOW + " from.");
    }

    @Command(aliases = { "type", "t" },
            desc = "Changes the type of a protected horse.",
            usage = "<whitelist/blacklist>",
            min = 1,
            max = 1)
    public static void type(CommandContext args, CommandSender sender)
            throws CommandException {

        if (!(sender instanceof Player)) {
            throw new CommandException(
                    "Silly you! You'll never be able to unprotect a horse!");
        }

        Player player = (Player) sender;
        final Type type = Type.getType(args.getString(0));
        if (type == null) {
            StringBuilder sb = new StringBuilder();
            for (Type temp : Type.values()) {
                sb.append(temp + ", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            throw new CommandException("That's not a valid type, valid types: "
                    + sb.toString());
        }
        plugin.getListener().addActingPlayer(player, new Actions.Type() {

            @Override
            public Type getType() {

                return type;
            }
        });
        player.sendMessage(ChatColor.YELLOW
                + "Right click the horse you wish to change the type of.");
    }

    @Command(aliases = { "info", "i" },
            desc = "Displays info about a horse",
            min = 0,
            max = 0)
    public static void info(CommandContext args, CommandSender sender)
            throws CommandException {

        if (!(sender instanceof Player)) {
            throw new CommandException(
                    "Silly you! You'll never be able to unprotect a horse!");
        }

        Player player = (Player) sender;
        plugin.getListener().addActingPlayer(player, new Actions.Info() {
        });
        player.sendMessage(ChatColor.YELLOW
                + "Right click the horse you wish to display information about.");
    }
}
