package dev.tenacity.module.impl.combat;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.event.impl.network.PacketReadEvent;
import dev.tenacity.event.impl.network.PacketReceiveEvent;

import dev.tenacity.event.impl.render.Render3DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.Breadcrumbs;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.time.TimerUtil;
import dev.tenacity.utils.tuples.Pair;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.util.Vec3;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.util.Vec3;
import net.minecraft.util.MathHelper;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import java.awt.Color;
import net.minecraft.network.INetHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.network.Packet;
import java.util.ArrayList;


public class BackTrack2 extends Module {


    private final TimerUtil timeHelper;
    private final ArrayList<Packet> packets;
    public AxisAlignedBB boundingBox;
    public boolean b;
    public boolean bb;
    public boolean aBoolean;
    public NumberSetting hitRange;
    public NumberSetting timerDelay;
    public BooleanSetting esp;
    public BooleanSetting onlyWhenNeed;
    public BooleanSetting player;
    public BooleanSetting mob;
    public BooleanSetting animal;
    public BooleanSetting villager;
    public BooleanSetting armorStand;

    public BooleanSetting onlyKillAura;
    public NumberSetting range;
    public BooleanSetting packetVelocity;
    public BooleanSetting packetVelocityExplosion;
    public BooleanSetting packetTimeUpdate;
    public BooleanSetting packetKeepAlive;
   
    private EntityLivingBase entity;
    private boolean blockPackets;
    private WorldClient lastWorld;
    private INetHandler packetListener;


    public BackTrack2() {
        super("Backtrack2", Category.COMBAT, "Fucks players in their last possition2");
        this.timeHelper = new TimerUtil();
        this.packets = new ArrayList<Packet>();
        this.hitRange = new NumberSetting("MaxHitRange", 6.0, 6.0, 3.0, 1);
        this.timerDelay = new NumberSetting("Time",  4000.0, 30000.0,0.0 , 333);
        this.esp = new BooleanSetting("Esp", true);
        this.onlyWhenNeed = new BooleanSetting("OnlyWhenNeed",  true);
        this.player = new BooleanSetting("Player", true);
        this.mob = new BooleanSetting("Mob",  true);
        this.animal = new BooleanSetting("Animal",  true);
        this.villager = new BooleanSetting("Villager",  true);
        this.armorStand = new BooleanSetting("ArmorStand",  true);
        this.onlyKillAura = new BooleanSetting("OnlyKillAura", true);
        this.range = new NumberSetting("PreAimRange",  4.0, 15.0, 0, 1);
        this.packetVelocity = new BooleanSetting("Velocity", true);
        this.packetVelocityExplosion = new BooleanSetting("ExplosionVelocity", true);
        this.packetTimeUpdate = new BooleanSetting("TimeUpdate", true);
        this.packetKeepAlive = new BooleanSetting("KeepAlive", true);
        this.entity = null;
        this.packetListener = null;
        addSettings(hitRange,timerDelay,esp,onlyWhenNeed,player,mob,animal,villager,armorStand,onlyKillAura,range,packetVelocity,packetVelocityExplosion,packetTimeUpdate,packetKeepAlive);
    }

    @Override
    public void onTickEvent(TickEvent event) {
        if (Tenacity.INSTANCE.isEnabled(KillAura.class)) {
            this.entity = KillAura.target;
        }
        else {
            final Object[] listOfTargets = mc.theWorld.loadedEntityList.stream().filter((Predicate<? super Object>)this::canAttacked).sorted(Comparator.comparingDouble(entityy -> mc.thePlayer.getDistanceToEntity(entityy))).toArray();
         //   final EntityLivingBase[] listOfTargets = mc.theWorld.loadedEntityList.stream()
               //     .filter(entity -> entity instanceof EntityLivingBase && canAttacked((EntityLivingBase) entity))
             //       .sorted(Comparator.comparingDouble(entityy -> mc.thePlayer.getDistanceToEntity(entityy)))
            //        .toArray(EntityLivingBase[]::new);

            if (listOfTargets.length > 0) {
                this.entity = (EntityLivingBase)listOfTargets[0];
            }
            if (this.onlyKillAura.isEnabled()) {
                this.entity = null;
            }
        }
        if (this.entity != null && mc.thePlayer != null && this.packetListener != null && mc.theWorld != null) {
            final double d0 = this.entity.realPosX / 32.0;
            final double d2 = this.entity.realPosY / 32.0;
            final double d3 = this.entity.realPosZ / 32.0;
            final double d4 = this.entity.serverPosX / 32.0;
            final double d5 = this.entity.serverPosY / 32.0;
            final double d6 = this.entity.serverPosZ / 32.0;
            final float f = this.entity.width / 2.0f;
            final AxisAlignedBB entityServerPos = new AxisAlignedBB(d4 - f, d5, d6 - f, d4 + f, d5 + this.entity.height, d6 + f);
            final Vec3 positionEyes = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
            final double currentX = MathHelper.clamp_double(positionEyes.xCoord, entityServerPos.minX, entityServerPos.maxX);
            final double currentY = MathHelper.clamp_double(positionEyes.yCoord, entityServerPos.minY, entityServerPos.maxY);
            final double currentZ = MathHelper.clamp_double(positionEyes.zCoord, entityServerPos.minZ, entityServerPos.maxZ);
            final AxisAlignedBB entityPosMe = new AxisAlignedBB(d0 - f, d2, d3 - f, d0 + f, d2 + this.entity.height, d3 + f);
            final double realX = MathHelper.clamp_double(positionEyes.xCoord, entityPosMe.minX, entityPosMe.maxX);
            final double realY = MathHelper.clamp_double(positionEyes.yCoord, entityPosMe.minY, entityPosMe.maxY);
            final double realZ = MathHelper.clamp_double(positionEyes.zCoord, entityPosMe.minZ, entityPosMe.maxZ);
            double distance = this.hitRange.getValue();
            if (!mc.thePlayer.canEntityBeSeen(this.entity)) {
                distance = ((distance > 3.0) ? 3.0 : distance);
            }
            final double collision = this.entity.getCollisionBorderSize();
            final double width = mc.thePlayer.width / 2.0f;
            final double mePosXForPlayer = mc.thePlayer.getLastServerPosition().xCoord + (mc.thePlayer.getSeverPosition().xCoord - mc.thePlayer.getLastServerPosition().xCoord) / MathHelper.clamp_int(mc.thePlayer.rotIncrement, 1, 3);
            final double mePosYForPlayer = mc.thePlayer.getLastServerPosition().yCoord + (mc.thePlayer.getSeverPosition().yCoord - mc.thePlayer.getLastServerPosition().yCoord) / MathHelper.clamp_int(mc.thePlayer.rotIncrement, 1, 3);
            final double mePosZForPlayer = mc.thePlayer.getLastServerPosition().zCoord + (mc.thePlayer.getSeverPosition().zCoord - mc.thePlayer.getLastServerPosition().zCoord) / MathHelper.clamp_int(mc.thePlayer.rotIncrement, 1, 3);
            AxisAlignedBB mePosForPlayerBox = new AxisAlignedBB(mePosXForPlayer - width, mePosYForPlayer, mePosZForPlayer - width, mePosXForPlayer + width, mePosYForPlayer + mc.thePlayer.height, mePosZForPlayer + width);
            mePosForPlayerBox = mePosForPlayerBox.expand(collision, collision, collision);
            final Vec3 entityPosEyes = new Vec3(d4, d5 + this.entity.getEyeHeight(), d6);
            final double bestX = MathHelper.clamp_double(entityPosEyes.xCoord, mePosForPlayerBox.minX, mePosForPlayerBox.maxX);
            final double bestY = MathHelper.clamp_double(entityPosEyes.yCoord, mePosForPlayerBox.minY, mePosForPlayerBox.maxY);
            final double bestZ = MathHelper.clamp_double(entityPosEyes.zCoord, mePosForPlayerBox.minZ, mePosForPlayerBox.maxZ);
            boolean b = false;
            if (entityPosEyes.distanceTo(new Vec3(bestX, bestY, bestZ)) > 3.0 || (mc.thePlayer.hurtTime < 8 && mc.thePlayer.hurtTime > 3)) {
                b = true;
            }
            if (!this.onlyWhenNeed.isEnabled()) {
                b = true;
            }
            if (b && positionEyes.distanceTo(new Vec3(realX, realY, realZ)) > positionEyes.distanceTo(new Vec3(currentX, currentY, currentZ)) && mc.thePlayer.getSeverPosition().distanceTo(new Vec3(d0, d2, d3)) < distance && !this.timeHelper.hasTimeElapsed(this.timerDelay.getValue())) {
                this.blockPackets = true;
            }
            else {
                this.blockPackets = false;
                this.resetPackets(this.packetListener);
                this.timeHelper.reset();
            }
        }
        super.onTickEvent(event);
    }
    @Override
    public void onRender3DEvent(Render3DEvent event) {
        if (this.esp.isEnabled()) {
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDisable(3553);
            GlStateManager.disableCull();
            GL11.glDepthMask(false);
            if (this.entity != null && this.blockPackets) {
                this.render(this.entity);
            }
            GL11.glDepthMask(true);
            GlStateManager.enableCull();
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDisable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(2848);
        }
        super.onRender3DEvent(event);
    }

    private boolean canAttacked(final Entity entity) {
        if (entity instanceof EntityLivingBase) {
            if (entity.isInvisible()) {
                return false;
            }
            if (((EntityLivingBase)entity).deathTime > 1) {
                return false;
            }
            if (entity instanceof EntityArmorStand && !this.armorStand.isEnabled()) {
                return false;
            }
            if (entity instanceof EntityAnimal && !this.animal.isEnabled()) {
                return false;
            }
            if (entity instanceof EntityMob && !this.mob.isEnabled()) {
                return false;
            }
            if (entity instanceof EntityPlayer && !this.player.isEnabled()) {
                return false;
            }
            if (entity instanceof EntityVillager && !this.villager.isEnabled()) {
                return false;
            }
            if (entity.ticksExisted < 50) {
                return false;
            }
            //if (entity instanceof EntityPlayer && mm.teams.isToggled() && mm.teams.getTeammates().contains(entity)) {
            //    return false;
            //}
            if (entity instanceof EntityPlayer && (entity.getName().equals("§aShop") || entity.getName().equals("SHOP") || entity.getName().equals("UPGRADES"))) {
                return false;
            }
            if (entity.isDead) {
                return false;
            }
            //if (entity instanceof EntityPlayer && mm.antiBot.isToggled() && AntiBot.bots.contains(entity)) {
            //    return false;
          //  }
           // if (entity instanceof EntityPlayer && !mm.midClick.noFiends && MidClick.friends.contains(entity.getName())) {
           //     return false;
          //  }
        }
        return entity instanceof EntityLivingBase && entity != mc.thePlayer && mc.thePlayer.getDistanceToEntity(entity) < this.range.getValue();
    }
    

    @Override
    public void onPacketReadEvent(PacketReadEvent eventReadPacket) {
        if (eventReadPacket.getNetHandler() != null) {
            this.packetListener = eventReadPacket.getNetHandler();
        }
        if (eventReadPacket.getDirection() != EnumPacketDirection.CLIENTBOUND) {
            return;
        }
        final Packet p = eventReadPacket.getPacket();
        if (p instanceof S08PacketPlayerPosLook) {
            this.resetPackets(eventReadPacket.getNetHandler());
        }
        if (p instanceof S14PacketEntity) {
            final S14PacketEntity packet = (S14PacketEntity)p;
            final Entity entity1 = mc.theWorld.getEntityByID(packet.getID());
            if (entity1 instanceof EntityLivingBase) {
                final EntityLivingBase entityLivingBase2;
                final EntityLivingBase entityLivingBase = entityLivingBase2 = (EntityLivingBase)entity1;
                entityLivingBase2.realPosX += packet.func_149062_c();
                final EntityLivingBase entityLivingBase3 = entityLivingBase;
                entityLivingBase3.realPosY += packet.func_149061_d();
                final EntityLivingBase entityLivingBase4 = entityLivingBase;
                entityLivingBase4.realPosZ += packet.func_149064_e();
            }
        }
        if (p instanceof S18PacketEntityTeleport) {
            final S18PacketEntityTeleport packet2 = (S18PacketEntityTeleport)p;
            final Entity entity1 = mc.theWorld.getEntityByID(packet2.getEntityId());
            if (entity1 instanceof EntityLivingBase) {
                final EntityLivingBase entityLivingBase = (EntityLivingBase)entity1;
                entityLivingBase.realPosX = packet2.getX();
                entityLivingBase.realPosY = packet2.getY();
                entityLivingBase.realPosZ = packet2.getZ();
            }
        }
        if (this.entity == null) {
            this.resetPackets(eventReadPacket.getNetHandler());
            return;
        }
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (this.lastWorld != mc.theWorld) {
                this.resetPackets(eventReadPacket.getNetHandler());
                this.lastWorld = mc.theWorld;
                return;
            }
            this.addPackets(p, eventReadPacket);
        }
        this.lastWorld = mc.theWorld;
        super.onPacketReadEvent(eventReadPacket);
    }
    @Override
    public void onEnable() {
        this.blockPackets = false;
        this.b = true;
        if (mc.theWorld != null && mc.thePlayer != null) {
            for (final Entity entity : mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityLivingBase) {
                    final EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                    entityLivingBase.realPosX = entityLivingBase.serverPosX;
                    entityLivingBase.realPosZ = entityLivingBase.serverPosZ;
                    entityLivingBase.realPosY = entityLivingBase.serverPosY;
                }
            }
        }
        super.onEnable();
    }


    @Override
    public void onDisable() {
        if (this.packets.size() > 0 && this.packetListener != null) {
            this.resetPackets(this.packetListener);
        }
        this.packets.clear();
        super.onDisable();
    }


    private void render(final EntityLivingBase entity) {
        final float red = 0.0f;
        final float green = 1.1333333f;
        final float blue = 0.0f;
        float lineWidth = 3.0f;
        final float alpha = 0.03137255f;
        if (mc.thePlayer.getDistanceToEntity(entity) > 1.0f) {
            double d0 = 1.0f - mc.thePlayer.getDistanceToEntity(entity) / 20.0f;
            if (d0 < 0.3) {
                d0 = 0.3;
            }
            lineWidth *= (float)d0;
        }
        RenderUtil.renderBoundingBox(entity,Color.GREEN,1);
    }

    private void resetPackets(final INetHandler netHandler) {
        if (this.packets.size() > 0) {
            while (this.packets.size() != 0) {
                final Packet packet = this.packets.get(0);
                try {
                    if (packet != null) {
                       // if (mm.velocity.isToggled() && (mm.velocity.mode.getSelected().equals("Spoof") || (mm.velocity.mode.getSelected().equals("Basic") && mm.velocity.XZValue.getValue() == 0.0 && mm.velocity.YValue.getValue() == 0.0))) {
                        //    if (!(packet instanceof S12PacketEntityVelocity)) {
                       //         if (!(packet instanceof S27PacketExplosion) || !mm.velocity.ignoreExplosion.isEnabled() || !mm.velocity.mode.getSelected().equals("Basic")) {
                      //              packet.processPacket(netHandler);
                         //       }
                      //      }
                     //   }
                      //  else {
                           packet.processPacket(netHandler);
                    //    }
                    }
                }
                catch (ThreadQuickExitException ex) {}
                this.packets.remove(this.packets.get(0));
            }
        }
    }

    private void addPackets(final Packet packet, final PacketReadEvent eventReadPacket) {
        synchronized (this.packets) {
            if (this.delayPackets(packet)) {
                this.aBoolean = true;
                this.packets.add(packet);
                eventReadPacket.cancel();
            }
        }
    }

    private boolean delayPackets(final Packet packet) {
        if (mc.currentScreen != null) {
            return false;
        }
        if (packet instanceof S03PacketTimeUpdate) {
            return this.packetTimeUpdate.isEnabled();
        }
        if (packet instanceof S00PacketKeepAlive) {
            return this.packetKeepAlive.isEnabled();
        }
        if (packet instanceof S12PacketEntityVelocity) {
            return this.packetVelocity.isEnabled();
        }
        if (packet instanceof S27PacketExplosion) {
            return this.packetVelocityExplosion.isEnabled();
        }
        if (packet instanceof S19PacketEntityStatus) {
            final S19PacketEntityStatus entityStatus = (S19PacketEntityStatus)packet;
            return entityStatus.getOpCode() != 2 || !(mc.theWorld.getEntityByID(entityStatus.getEntityId()) instanceof EntityLivingBase);
        }
        return !(packet instanceof S06PacketUpdateHealth) && !(packet instanceof S29PacketSoundEffect) && !(packet instanceof S3EPacketTeams) && !(packet instanceof S0CPacketSpawnPlayer);
    }











}