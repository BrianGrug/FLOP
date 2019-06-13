package cc.funkemunky.fiona.utils;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.data.PlayerData;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerUtils {

    public static boolean hasEnchantment(ItemStack item, Enchantment enchantment) {
        return item.getEnchantments().keySet().contains(enchantment);
    }

    public static int getDepthStriderLevel(Player player) {
        if (player.getInventory().getBoots() != null
                && hasEnchantment(player.getInventory().getBoots(), Enchantment.getByName("DEPTH_STRIDER"))) {
            return player.getInventory().getBoots().getEnchantments().get(Enchantment.getByName("DEPTH_STRIDER"));
        }

        return 0;
    }

    public static boolean hasBlocksAround(Location loc) {
        Location one = loc.clone().subtract(1, 0, 1), two = loc.clone().add(1, 1, 1);

        int minX = Math.min(one.getBlockX(), two.getBlockX()), minY = Math.min(one.getBlockY(), two.getBlockY()), minZ = Math.min(one.getBlockZ(), two.getBlockZ());
        int maxX = Math.max(one.getBlockX(), two.getBlockX()), maxY = Math.max(one.getBlockY(), two.getBlockY()), maxZ = Math.max(one.getBlockZ(), two.getBlockZ());

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Location blockLoc = new Location(loc.getWorld(), x, y, z);

                    if (BlockUtils.isSolid(BlockUtils.getBlock(blockLoc))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isRiskyForFlight(PlayerData data) {
        return data.webTicks > 0 || data.lastBlockPlace.hasNotPassed(20) || isGliding(data.player) || data.liquidTicks > 0 || data.climbTicks > 0 || data.pistonsNear || data.generalCancel || data.isVelocityTaken();
    }

    public static boolean facingOpposite(Entity one, Entity two) {
        return one.getLocation().getDirection().distance(two.getLocation().getDirection()) < 0.5;
    }

    public static boolean isGliding(Player p) {
        return Fiona.getInstance().getBlockBoxManager().getBlockBox().isGliding(p);
    }

    public static double getAccurateDistance(LivingEntity attacked, LivingEntity entity) {
        Location origin = attacked.getEyeLocation(), point;
        if (entity.getLocation().getY() > attacked.getLocation().getBlockY()) {
            point = entity.getLocation();
        } else {
            point = entity.getEyeLocation();
        }


        return origin.distance(point);
    }

    public static double getAccurateDistance(Location origin, Location point) {
        return origin.distance(point) * Math.cos(origin.getPitch());
    }

    public static double getAccurateDistance(FionaLocation origin, FionaLocation point) {
        return origin.toVector().distance(point.toVector()) + 0.3f;
    }

    public static int getPotionEffectLevel(Player player, PotionEffectType pet) {
        for (PotionEffect pe : player.getActivePotionEffects()) {
            if (!pe.getType().getName().equals(pet.getName())) continue;
            return pe.getAmplifier() + 1;
        }
        return 0;
    }
}
