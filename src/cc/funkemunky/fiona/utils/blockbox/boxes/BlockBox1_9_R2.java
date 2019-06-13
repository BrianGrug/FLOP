package cc.funkemunky.fiona.utils.blockbox.boxes;

import cc.funkemunky.fiona.utils.BlockUtils;
import cc.funkemunky.fiona.utils.BoundingBox;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.ReflectionsUtil;
import cc.funkemunky.fiona.utils.blockbox.BlockBox;
import com.google.common.collect.Lists;
import lombok.val;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class BlockBox1_9_R2 implements BlockBox {
    @Override
    public List<BoundingBox> getCollidingBoxes(World world, BoundingBox box) {
        BoundingBox collisionBox = box;
        List<AxisAlignedBB> aabbs = Lists.newArrayList();
        List<BoundingBox> boxes = Lists.newArrayList();

        int minX = MathUtils.floor(box.minX);
        int maxX = MathUtils.floor(box.maxX + 1);
        int minY = MathUtils.floor(box.minY);
        int maxY = MathUtils.floor(box.maxY + 1);
        int minZ = MathUtils.floor(box.minZ);
        int maxZ = MathUtils.floor(box.maxZ + 1);


        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                for (int y = minY - 1; y < maxY; y++) {
                    org.bukkit.block.Block block = BlockUtils.getBlock(new Location(world, x, y, z));
                    if (!block.getType().equals(Material.AIR)) {
                        if (BlockUtils.collisionBoundingBoxes.containsKey(block.getType())) {
                            aabbs.add((AxisAlignedBB) BlockUtils.collisionBoundingBoxes.get(block.getType()).add(block.getLocation().toVector()).toAxisAlignedBB());
                        } else {
                            net.minecraft.server.v1_9_R2.World nmsWorld = ((CraftWorld) world).getHandle();
                            net.minecraft.server.v1_9_R2.IBlockData nmsiBlockData = ((CraftWorld) world).getHandle().getType(new BlockPosition(x, y, z));
                            net.minecraft.server.v1_9_R2.Block nmsBlock = nmsiBlockData.getBlock();


                            nmsBlock.a(nmsiBlockData, nmsWorld, new BlockPosition(x, y, z), (AxisAlignedBB) box.toAxisAlignedBB(), aabbs, null);
                        }
                        /*
                        else {
                            BoundingBox blockBox = new BoundingBox((float) nmsBlock.B(), (float) nmsBlock.D(), (float) nmsBlock.F(), (float) nmsBlock.C(), (float) nmsBlock.E(), (float) nmsBlock.G());
                        }*/

                    }
                }
            }
        }

        aabbs.forEach(aabb -> boxes.add(ReflectionsUtil.toBoundingBox(aabb)));
        return boxes;
    }

    @Override
    public List<BoundingBox> getSpecificBox(Location loc) {
        World world = loc.getWorld();

        AxisAlignedBB collisionBox = (AxisAlignedBB) new BoundingBox(loc.toVector(), loc.toVector()).grow(1f, 1f, 1f).toAxisAlignedBB();
        List<AxisAlignedBB> boxList = ((CraftWorld) world).getHandle().a(collisionBox);

        List<AxisAlignedBB> aabbs = Lists.newArrayList();
        List<BoundingBox> boxes = Lists.newArrayList();

        BoundingBox box = new BoundingBox(loc.toVector(), loc.toVector()).grow(2, 2, 2);
        int minX = MathUtils.floor(box.minX);
        int maxX = MathUtils.floor(box.maxX + 1);
        int minY = MathUtils.floor(box.minY);
        int maxY = MathUtils.floor(box.maxY + 1);
        int minZ = MathUtils.floor(box.minZ);
        int maxZ = MathUtils.floor(box.maxZ + 1);


        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                for (int y = minY - 1; y < maxY; y++) {
                    org.bukkit.block.Block block = BlockUtils.getBlock(new Location(world, x, y, z));
                    if (!block.getType().equals(Material.AIR) && block.getLocation().equals(loc)) {
                        if (BlockUtils.collisionBoundingBoxes.containsKey(block.getType())) {
                            aabbs.add((AxisAlignedBB) BlockUtils.collisionBoundingBoxes.get(block.getType()).add(block.getLocation().toVector()).toAxisAlignedBB());
                        } else {
                            net.minecraft.server.v1_9_R2.World nmsWorld = ((CraftWorld) world).getHandle();
                            net.minecraft.server.v1_9_R2.IBlockData nmsiBlockData = ((CraftWorld) world).getHandle().getType(new BlockPosition(x, y, z));
                            net.minecraft.server.v1_9_R2.Block nmsBlock = nmsiBlockData.getBlock();


                            nmsBlock.a(nmsiBlockData, nmsWorld, new BlockPosition(x, y, z), (AxisAlignedBB) box.toAxisAlignedBB(), aabbs, null);
                        }
                        /*
                        else {
                            BoundingBox blockBox = new BoundingBox((float) nmsBlock.B(), (float) nmsBlock.D(), (float) nmsBlock.F(), (float) nmsBlock.C(), (float) nmsBlock.E(), (float) nmsBlock.G());
                        }*/

                    }
                }
            }
        }
        aabbs.forEach(aabb -> boxes.add(ReflectionsUtil.toBoundingBox(aabb)));
        return boxes;
    }

    @Override
    public boolean isChunkLoaded(Location loc) {
        net.minecraft.server.v1_9_R2.World world = ((org.bukkit.craftbukkit.v1_9_R2.CraftWorld) loc.getWorld()).getHandle();

        return !world.isClientSide && world.isLoaded(new net.minecraft.server.v1_9_R2.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())) && world.getChunkAtWorldCoords(new net.minecraft.server.v1_9_R2.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())).p();
    }

    @Override
    public boolean isUsingItem(Player player) {
        net.minecraft.server.v1_9_R2.EntityLiving entity = ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftLivingEntity) player).getHandle();
        return entity.cw() != null && entity.cw().getItem().f(entity.cw()) != net.minecraft.server.v1_9_R2.EnumAnimation.NONE;
    }

    @Override
    public boolean isGliding(LivingEntity entity) {
        return ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftLivingEntity) entity).isGliding();
    }
    
    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public int getTrackerId(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        val trackedEntities = ((WorldServer) entityPlayer.getWorld()).tracker.trackedEntities;

        if(trackedEntities.b(player.getEntityId())) {
            EntityTrackerEntry entry = trackedEntities.get(entityPlayer.getId());
            return entry.b().getId();
        }
        return 0;
    }
}
