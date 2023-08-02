package dev.tenacity.module.impl.combat;

import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.network.PacketSendEvent;

import dev.tenacity.event.impl.player.input.AttackEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.render.Render3DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.Breadcrumbs;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.time.TimerUtil;
import dev.tenacity.utils.tuples.Pair;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.util.Vec3;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.ArrayList;

public class BackTrack extends Module {
    public static EntityLivingBase target;
    public static List<Vec3> pastPositions = new ArrayList<>();
    public static List<Vec3> forwardPositions = new ArrayList<>();
    public static List<Vec3> positions = new ArrayList<>();
    private final Deque<Packet<?>> packets = new ArrayDeque<>();

    private final NumberSetting amount = new NumberSetting("Amount", 0, 100, 1, 1);
    private final NumberSetting forward = new NumberSetting("Forward", 0, 100, 1, 1);

    private int ticks;


    public BackTrack() {
        super("Backtrack", Category.COMBAT, "Fucks players in their last possition");
        addSettings(amount,forward);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        //if (mc.thePlayer.ticksExisted < 5) {
        //    onDisable();
       //     return;
      //  }

        if (target == null) return;

        pastPositions.add(new Vec3(target.posX, target.posY, target.posZ));

        final double deltaX = (target.posX - target.lastTickPosX) * 2;
        final double deltaZ = (target.posZ - target.lastTickPosZ) * 2;

        forwardPositions.clear();
        int i = 0;
        while (forward.getValue() > forwardPositions.size()) {
            i++;
            forwardPositions.add(new Vec3(target.posX + deltaX * i, target.posY, target.posZ + deltaZ * i));
        }

        while (pastPositions.size() > amount.getValue().intValue()) {
            pastPositions.remove(0);
        }

        positions.clear();
        positions.addAll(forwardPositions);
        positions.addAll(pastPositions);

        ticks++;
        super.onMotionEvent(event);
    }
    @Override
    public void onRender3DEvent(Render3DEvent event) {
        if (target != null && !positions.isEmpty()) {
           // RenderUtil.renderBoundingBox(target, HUDMod.getClientColors().getFirst(), 1);
            Pair<Color, Color> colors = HUDMod.getClientColors();
            Breadcrumbs.renderLine(positions, colors);
        }
        super.onRender3DEvent(event);
    }
    @Override
    public void onAttackEvent(AttackEvent event) {
        if (event.targetEntity instanceof EntityPlayer) target = (EntityLivingBase) event.targetEntity;
        ticks = 0;
        super.onAttackEvent(event);
    }


    @Override
    public void onDisable() {
        target = null;
        positions.clear();
        pastPositions.clear();
        forwardPositions.clear();
        packets.clear();
        super.onDisable();
    }











}