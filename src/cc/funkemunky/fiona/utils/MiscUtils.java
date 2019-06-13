package cc.funkemunky.fiona.utils;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Detection;
import com.ngxdev.tinyprotocol.packet.out.WrappedPacketPlayOutWorldParticle;
import com.ngxdev.tinyprotocol.packet.types.WrappedEnumParticle;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MiscUtils {

    public static Map<EntityType, Vector> entityDimensions;

    public MiscUtils() {
        entityDimensions = new HashMap<>();

        entityDimensions.put(EntityType.WOLF, new Vector(0.31, 0.8, 0.31));
        entityDimensions.put(EntityType.SHEEP, new Vector(0.45, 1.3, 0.45));
        entityDimensions.put(EntityType.COW, new Vector(0.45, 1.3, 0.45));
        entityDimensions.put(EntityType.PIG, new Vector(0.45, 0.9, 0.45));
        entityDimensions.put(EntityType.MUSHROOM_COW, new Vector(0.45, 1.3, 0.45));
        entityDimensions.put(EntityType.WITCH, new Vector(0.31, 1.95, 0.31));
        entityDimensions.put(EntityType.BLAZE, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.PLAYER, new Vector(0.3, 1.8, 0.3));
        entityDimensions.put(EntityType.VILLAGER, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.CREEPER, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.GIANT, new Vector(1.8, 10.8, 1.8));
        entityDimensions.put(EntityType.SKELETON, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.ZOMBIE, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.SNOWMAN, new Vector(0.35, 1.9, 0.35));
        entityDimensions.put(EntityType.HORSE, new Vector(0.7, 1.6, 0.7));
        entityDimensions.put(EntityType.ENDER_DRAGON, new Vector(1.5, 1.5, 1.5));
        entityDimensions.put(EntityType.ENDERMAN, new Vector(0.31, 2.9, 0.31));
        entityDimensions.put(EntityType.CHICKEN, new Vector(0.2, 0.7, 0.2));
        entityDimensions.put(EntityType.OCELOT, new Vector(0.31, 0.7, 0.31));
        entityDimensions.put(EntityType.SPIDER, new Vector(0.7, 0.9, 0.7));
        entityDimensions.put(EntityType.WITHER, new Vector(0.45, 3.5, 0.45));
        entityDimensions.put(EntityType.IRON_GOLEM, new Vector(0.7, 2.9, 0.7));
        entityDimensions.put(EntityType.GHAST, new Vector(2, 4, 2));
    }

    public static boolean hasPermissionForAlerts(Player player) {
        return player.hasPermission("fiona.staff") || player.hasPermission("fiona.alerts");
    }

    public static boolean containsIgnoreCase(String toCheck, String contains) {
        return toCheck.toLowerCase().contains(contains.toLowerCase());
    }

    public static String line(String color) {
        return color + Color.Strikethrough + "-----------------------------------------------------";
    }

    public static String formattedMessage(String message) {
        return Color.translate(Fiona.getInstance().getMessageFields().prefix + message);
    }

    public static void createParticlesForBoundingBox(Player player, BoundingBox box) {
        for (float x = box.minX; x < box.maxX + 0.2; x += 0.2f) {
            for (float y = box.minY; y < box.maxY + 0.2; y += 0.2f) {
                for (float z = box.minZ; z < box.maxZ + 0.2; z += 0.2f) {
                    WrappedPacketPlayOutWorldParticle packet = new WrappedPacketPlayOutWorldParticle(WrappedEnumParticle.FLAME, true, x, y, z, 0f, 0f, 0f, 0f, 1, null);
                    packet.sendPacket(player);
                }
            }
        }
    }

    public static void createParticlesForBoundingBox(Player player, BoundingBox box, WrappedEnumParticle type) {
        for (float x = box.minX; x < box.maxX + 0.2; x += 0.2f) {
            for (float y = box.minY; y < box.maxY + 0.2; y += 0.2f) {
                for (float z = box.minZ; z < box.maxZ + 0.2; z += 0.2f) {
                    WrappedPacketPlayOutWorldParticle packet = new WrappedPacketPlayOutWorldParticle(WrappedEnumParticle.FLAME, true, x, y, z, 0f, 0f, 0f, 0f, 1, null);
                    packet.sendPacket(player);
                }
            }
        }
    }

    public static void createParticlesForBoundingBox(Player player, BoundingBox box, WrappedEnumParticle type, float accuracy) {
        for (float x = box.minX; x < box.maxX + accuracy; x += accuracy) {
            for (float y = box.minY; y < box.maxY + accuracy; y += accuracy) {
                for (float z = box.minZ; z < box.maxZ + accuracy; z += accuracy) {
                    WrappedPacketPlayOutWorldParticle packet = new WrappedPacketPlayOutWorldParticle(type, true, x, y, z, 0f, 0f, 0f, 0f, 1, null);
                    packet.sendPacket(player);
                }
            }
        }
    }

    public static boolean canCancel(Detection detection, PlayerData data) {
        return detection.isCancellable() && detection.getParentCheck().isCancellable() && data.getViolations(detection.getParentCheck()) > detection.getParentCheck().getCancelThreshold();
    }

    public static BoundingBox getEntityBoundingBox(LivingEntity entity) {
        if (entityDimensions.containsKey(entity.getType())) {
            Vector entityVector = entityDimensions.get(entity.getType());

            float minX = (float) Math.min(-entityVector.getX() + entity.getLocation().getX(), entityVector.getX() + entity.getLocation().getX());
            float minY = (float) Math.min(entity.getLocation().getY(), entityVector.getY() + entity.getLocation().getY());
            float minZ = (float) Math.min(-entityVector.getZ() + entity.getLocation().getZ(), entityVector.getZ() + entity.getLocation().getZ());
            float maxX = (float) Math.max(-entityVector.getX() + entity.getLocation().getX(), entityVector.getX() + entity.getLocation().getX());
            float maxY = (float) Math.max(entity.getLocation().getY(), entityVector.getY() + entity.getLocation().getY());
            float maxZ = (float) Math.max(-entityVector.getZ() + entity.getLocation().getZ(), entityVector.getZ() + entity.getLocation().getZ());
            return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return ReflectionsUtil.toBoundingBox(ReflectionsUtil.getBoundingBox(entity));
    }

    public static ItemStack createItem(Material material, int amount, String name, String... lore) {
        ItemStack thing = new ItemStack(material, amount);
        ItemMeta thingm = thing.getItemMeta();
        thingm.setDisplayName(Color.translate(name));
        ArrayList<String> loreList = new ArrayList<>();
        for (String string : lore) {
            loreList.add(Color.translate(string));
        }
        thingm.setLore(loreList);
        thing.setItemMeta(thingm);
        return thing;
    }

    public static void printToConsole(String string) {
        Fiona.getInstance().consoleSender.sendMessage(Color.translate(string));
    }
}

