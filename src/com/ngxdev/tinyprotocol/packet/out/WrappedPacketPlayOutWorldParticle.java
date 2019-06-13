package com.ngxdev.tinyprotocol.packet.out;

import cc.funkemunky.fiona.utils.ReflectionsUtil;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.types.WrappedEnumParticle;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@Getter
public class WrappedPacketPlayOutWorldParticle {

    private WrappedEnumParticle type;
    private boolean j;
    private float x;
    private float y;
    private float z;
    private float xOffset;
    private float yOffset;
    private float zOffset;
    private float speed;
    private int amount;
    private int[] data;

    public WrappedPacketPlayOutWorldParticle(WrappedEnumParticle type, boolean var2, float x, float y, float z, float xOffset, float yOffset, float ZOffset, float speed, int amount, int... data) {
        this.type = type;
        this.j = var2;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = ZOffset;
        this.speed = speed;
        this.amount = amount;
        this.data = data;

    }

    public void sendPacket(Player player) {
        Object packet = null;

        if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            try {
                ReflectionsUtil.CraftPlayer.getMethod("spawnParticle", Particle.class, double.class, double.class, double.class, int.class, double.class, double.class, double.class, double.class, Object.class).invoke(player, Particle.FLAME, x, y, z, amount, xOffset, yOffset, zOffset, speed, data);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else {
            try {
                packet = ReflectionsUtil.getNMSClass("PacketPlayOutWorldParticles").getConstructor(ReflectionsUtil.getNMSClass("EnumParticle"), boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class).newInstance(type.toNMS(), j, x, y, z, xOffset, yOffset, zOffset, speed, amount, data);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                e1.printStackTrace();
            }
            Object pCon = ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(ReflectionsUtil.getNMSClass("EntityPlayer"), "playerConnection"), ReflectionsUtil.getEntityPlayer(player));

            ReflectionsUtil.getMethodValue(ReflectionsUtil.getMethod(ReflectionsUtil.getNMSClass("PlayerConnection"), "sendPacket", ReflectionsUtil.getNMSClass("Packet")), pCon, packet);
        }
    }
}