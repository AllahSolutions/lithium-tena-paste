package dev.tenacity.module.impl.combat;

import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.Setting;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.time.TimerUtil;
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
import net.minecraft.entity.EntityLivingBase;

public class BackTrack extends Module {
    private TimerUtil timeHelper;
    private ArrayList<Packet> packets;
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
    public BooleanSetting targets;
    public BooleanSetting onlyKillAura;
    public NumberSetting range;
    public BooleanSetting packetVelocity;
    public BooleanSetting packetVelocityExplosion;
    public BooleanSetting packetTimeUpdate;
    public BooleanSetting packetKeepAlive;
    public BooleanSetting packetsToDelay;
    private EntityLivingBase entity;
    private boolean blockPackets;
    private WorldClient lastWorld;
    private INetHandler packetListener;

    public BooleanSetting blocksmc = new BooleanSetting("BlocksMC", true);

    public BackTrack() {
        super("BackTrack", Category.COMBAT, "Allows you to htis players in their old or new pos");
        this.timeHelper = new TimerUtil();
        this.packets = new ArrayList<Packet>();
        this.hitRange = new NumberSetting("MaxHitRange", 6.0, 3.0, 6.0, 2);
        this.timerDelay = new NumberSetting( "Time", 4000.0, 0.0, 30000.0, 0);
        this.esp = new BooleanSetting("Esp", true);
        this.onlyWhenNeed = new BooleanSetting("OnlyWhenNeed", true);
        this.player = new BooleanSetting("Player", true);
        this.mob = new BooleanSetting("Mob", true);
        this.animal = new BooleanSetting("Animal", true);
        this.villager = new BooleanSetting( "Villager", true);
        this.armorStand = new BooleanSetting("ArmorStand" , true);
       // this.targets = new BooleanSetting("Targets", new Setting[] { (Setting)this.player, (Setting)this.mob, (Setting)this.animal, (Setting)this.villager, (Setting)this.armorStand });
        this.onlyKillAura = new BooleanSetting( "OnlyKillAura", true);
        this.range = new NumberSetting( "PreAimRange", 4.0, 0.0, 15.0, 1);
        this.packetVelocity = new BooleanSetting("Velocity", true);
        this.packetVelocityExplosion = new BooleanSetting("ExplosionVelocity", true);
        this.packetTimeUpdate = new BooleanSetting("TimeUpdate", true);
        this.packetKeepAlive = new BooleanSetting( "KeepAlive", true);
        //this.packetsToDelay = new BooleanSetting(32, "PacketsToDelay", (Module)this, new Setting[] { (Setting)this.packetVelocity, (Setting)this.packetVelocityExplosion, (Setting)this.packetTimeUpdate, (Setting)this.packetKeepAlive });
        this.entity = null;
        this.packetListener = null;

        this.addSettings(blocksmc);
    }


}