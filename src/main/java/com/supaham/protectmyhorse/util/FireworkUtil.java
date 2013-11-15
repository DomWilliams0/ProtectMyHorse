package com.supaham.protectmyhorse.util;

import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import com.google.common.collect.Lists;

public class FireworkUtil {

    static Random random = new Random();
    static List<Color> colors = Lists.newArrayList(Color.AQUA, Color.BLUE,
            Color.FUCHSIA, Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY,
            Color.NAVY, Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.RED, Color.TEAL,
            Color.YELLOW);

    public static Firework getRandomFirework(Location loc) {

        FireworkMeta fireworkMeta =
                (FireworkMeta) (new ItemStack(Material.FIREWORK)).getItemMeta();
        Firework firework =
                (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);

        fireworkMeta.setPower(random.nextInt(1));
        fireworkMeta.addEffect(FireworkEffect.builder().with(randomFireworkType())
                .withColor(randomColor()).trail(random.nextBoolean()).build());

        firework.setFireworkMeta(fireworkMeta);
        return firework;
    }

    public static FireworkEffect.Type randomFireworkType() {

        return FireworkEffect.Type.values()[random
                .nextInt(FireworkEffect.Type.values().length)];
    }

    public static Color randomColor() {

        return colors.get(random.nextInt(colors.size()));
    }
}
