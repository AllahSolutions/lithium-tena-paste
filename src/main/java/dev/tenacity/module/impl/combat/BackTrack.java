package dev.tenacity.module.impl.combat;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.player.AttackEvent;
import dev.tenacity.event.impl.player.UpdateEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.time.TimerUtil;
import lombok.Getter;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.*;
import java.util.function.Predicate;

public class BackTrack extends Module {
    private TimerUtil timeHelper;
    private ArrayList<Packet> packets;
    public AxisAlignedBB boundingBox;

    public boolean b;
    public boolean bb;
    public boolean aBoolean;

    private EntityLivingBase entity;
    private boolean blockPackets;
    private WorldClient lastWorld;
    private INetHandler packetListener;

    // The Position Limit.

    private final NumberSetting hitRange = new NumberSetting("hitRange", 6,6,3,1);
    private final NumberSetting timerDelay = new NumberSetting("hitRange", 0,5000,0,111);
    private final BooleanSetting esp = new BooleanSetting("Esp", false);
    private final BooleanSetting onlyWhenNeed = new BooleanSetting("onlyWhenNeed", false);
    private final BooleanSetting player = new BooleanSetting("player", false);
    private final BooleanSetting onlyKillAura = new BooleanSetting("onlyKillAura", false);
    private final BooleanSetting range = new BooleanSetting("Preaim", false);
    private final BooleanSetting packetVelocity = new BooleanSetting("Speed", false);
    private final BooleanSetting packetVelocityExplosion = new BooleanSetting("packetVelocityExplosion", false);
    private final BooleanSetting packetTimeUpdate = new BooleanSetting("Speed", false);
    private final BooleanSetting packetKeepAlive = new BooleanSetting("Speed", false);





    public BackTrack() {
        super("BackTrack", Category.COMBAT, "Allows you to hit entities from their previous positions.");
        this.timeHelper = new TimerUtil();
        this.packets = new ArrayList<Packet>();
        this.entity = null;
        this.packetListener = null;
        this.addSettings(packetKeepAlive,packetVelocity,packetTimeUpdate,packetVelocityExplosion,range,hitRange,onlyKillAura,player,onlyWhenNeed,esp,timerDelay,hitRange);
    }



    @Override
    public void onEnable() {
        this.blockPackets = false;
        this.b = true;
        if (BackTrack.mc.theWorld != null && BackTrack.mc.thePlayer != null) {
            for (final Entity entity : BackTrack.mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityLivingBase) {
                    final EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                    entityLivingBase.posX = entityLivingBase.serverPosX;
                    entityLivingBase.posZ = entityLivingBase.serverPosZ;
                    entityLivingBase.posY = entityLivingBase.serverPosY;
                }
            }
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (this.packets.size() > 0 && this.packetListener != null) {
            //this.resetPackets(this.packetListener);
        }
        this.packets.clear();
        super.onDisable();
    }

    @Override
    public void onAttackEvent(AttackEvent event) {

        super.onAttackEvent(event);
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {

        super.onPacketReceiveEvent(event);
    }

    @Override
    public void onTickEvent(TickEvent event) {



        super.onTickEvent(event);
    }
}