package dev.tenacity.module.impl.combat;

import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.game.WorldEvent;
import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.event.impl.player.UpdateEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.Setting;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.player.MovementUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.MovingObjectPosition;

public class Velocity extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Packet","Intave", "Grim", "Packet", "Reverse", "Polar");
    private final NumberSetting horizontal = new NumberSetting("Horizontal", 0, 100, 0, 1);
    private final NumberSetting vertical = new NumberSetting("Vertical", 0, 100, 0, 1);

    public Velocity() {
        super("Velocity", Category.COMBAT, "Reduces your velocity.");
        this.addSettings(mode, horizontal, vertical);
    }
    private boolean attacked;

    /* Grim Velocity variables */
    private static int grim_ticks = 0;
    private static int grim_updates = 0;
    private static int grim_resets = 8;

    @Override
    public void onEnable() {
        grim_ticks = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {

        Packet <?> packet = event.getPacket();

        if (packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) packet;

            switch (mode.getMode()) {
                case "Packet": {
                    s12.motionX *= horizontal.getValue() / 100;
                    s12.motionY *= vertical.getValue() / 100;
                    s12.motionZ *= horizontal.getValue() / 100;
                    break;
                }
                case "Reverse": {
                    s12.motionX *= -s12.motionX;
                    s12.motionY *= vertical.getValue() / 100;
                    s12.motionZ *= -s12.motionZ;
                    break;
                }
                case "Grim": {
                    event.cancel();
                    grim_ticks = 6;
                    break;
                }
                default: {
                    break;
                }
            }
        }

        if (mode.is("Grim") && packet instanceof S32PacketConfirmTransaction && grim_ticks > 0) {
            event.cancel();
            --grim_ticks;
        }


        if (packet instanceof S27PacketExplosion) {
            S27PacketExplosion s12 = (S27PacketExplosion) packet;

            switch (mode.getMode()) {
                case "Packet": {
                    s12.motionX *= horizontal.getValue() / 100;
                    s12.motionY *= vertical.getValue() / 100;
                    s12.motionZ *= horizontal.getValue() / 100;
                    break;
                }
                case "Reverse": {
                    s12.motionX *= -s12.motionX;
                    s12.motionY *= vertical.getValue() / 100;
                    s12.motionZ *= -s12.motionZ;
                    break;
                }
                default: {
                    break;
                }
            }
        }

        super.onPacketReceiveEvent(event);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {

        if ("Polar".equals(this.mode.getMode()) && mc.thePlayer.hurtTime > 0) {
            if (mc.thePlayer.motionX >= 0.001 && mc.thePlayer.motionZ >= 0.001) {
                mc.thePlayer.motionX /= 1.2;
                mc.thePlayer.motionZ /= 1.2;
                mc.thePlayer.onGround = true;
            }
        }

        super.onMotionEvent(event);
    }


    @Override
    public void onUpdateEvent(UpdateEvent event) {
        ++grim_updates;

        if (grim_updates >= 0 || grim_updates >= grim_resets) {
            grim_updates = 0;

            if (grim_ticks > 0) {
                --grim_ticks;
            }
        }
        if(mode.is("Intave")) {
            if(mc.thePlayer.isSwingInProgress) {
                attacked = true;
            }
            if (mc.objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.ENTITY) && mc.thePlayer.hurtTime > 0 && !attacked) {
                mc.thePlayer.motionX *= 0.6D;
                mc.thePlayer.motionZ *= 0.6D;
                mc.thePlayer.setSprinting(false);
            }

            attacked = false;

            if(!mc.thePlayer.isSwingInProgress) {
                return;
            }
        }

        super.onUpdateEvent(event);
    }
}
