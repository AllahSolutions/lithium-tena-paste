package dev.tenacity.module.impl.combat;

import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.UpdateEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.player.ChatUtil;
import dev.tenacity.utils.player.MovementUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MovingObjectPosition;

public class Velocity extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Packet","Vulcan","Vulcan2","Minemen", "Intave","Watchdog", "Grim", "Packet", "Reverse", "Polar", "Minemen");
    private final NumberSetting horizontal = new NumberSetting("Horizontal", 0, 100, 0, 1);
    private final NumberSetting vertical = new NumberSetting("Vertical", 0, 100, 0, 1);

    public Velocity() {
        super("Velocity", Category.COMBAT, "Reduces your velocity.");
        this.addSettings(mode, horizontal, vertical);
    }
    private boolean attacked;

    /* Grim Velocity variables */
    private static int grim_ticks = 0;


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
    public void onPacketSendEvent(PacketSendEvent event) {
        if(mode.is("Vulcan")) {
            if(mc.thePlayer.hurtTime>1) {
                if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                    event.cancel();
                   // ChatUtil.print("sex");
                }
            }
        }
        if(mc.thePlayer.hurtTime>1) {
            if (mode.is("Grim") && event.getPacket() instanceof C0FPacketConfirmTransaction) {
                if (grim_ticks < 6) {
                    event.cancel();
                }
                if (grim_ticks > 6) {
                    grim_ticks = 0;
                }
                ChatUtil.print(grim_ticks);
                grim_ticks++;
            }
        }

        super.onPacketSendEvent(event);
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
                    s12.motionX = -s12.motionX;
                  //  s12.motionY *= vertical.getValue() / 100;
                    s12.motionZ = -s12.motionZ;
                    break;
                }
                case"Watchdog":
                    if(!mc.thePlayer.onGround) {
                        //ChatUtil.print("0.0 air");
                        s12.motionX = 100;
                        s12.motionY = 100;
                        s12.motionZ = 100;
                    }
                    break;
                case"Vulcan": {
                    s12.motionX = 100;
                    s12.motionY = 100;
                    s12.motionZ = 100;
                    break;
                }
                case"Vulcan2": {
                    s12.motionX = 100;
                    s12.motionY = -s12.motionY;
                    s12.motionZ = 100;
                    break;
                }
                case "Grim": {
                    if(grim_ticks < 6) {
                        event.cancel();
                    }

                    break;
                }
                case "Minemen": {
                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                        event.cancel();
                    }
                    break;
                }

                default: {
                    break;
                }
            }
        }









      //  if (packet instanceof S12PacketEntityVelocity) {

       // }


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
                case "Minemen": {
                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                        event.cancel();
                    }
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
    public void onUpdateEvent(UpdateEvent event) {

        this.setSuffix(mode.getMode());
        if(mode.is("Watchdog")) {

            if (mc.thePlayer.hurtTime > 1) {
                if (mc.thePlayer.onGround) {
                    if(mc.thePlayer.ticksExisted % 3 == 0) {
                        ChatUtil.print("0.0");
                    }
                    // MovementUtils.strafe(MovementUtils.getBaseMoveSpeed());
                    mc.thePlayer.motionY = 0;

                }
            }
        }

        if (mode.is("Intave")) {
            if (mc.thePlayer.isSwingInProgress) {
                attacked = true;
            }

            if (mc.objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.ENTITY) && mc.thePlayer.hurtTime > 0 && !attacked) {
                mc.thePlayer.motionX *= 0.6D;
                mc.thePlayer.motionZ *= 0.6D;
                mc.thePlayer.setSprinting(false);
            }

            attacked = false;
        }

        if (mode.is("Polar")) {
            if (mc.thePlayer.isSwingInProgress) {
                attacked = true;
            }

            if (mc.objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.ENTITY) && mc.thePlayer.hurtTime > 0 && !attacked) {
                mc.thePlayer.motionX *= 0.45D;
                mc.thePlayer.motionZ *= 0.45D;
                mc.thePlayer.setSprinting(false);
            }

            attacked = false;
        }

        super.onUpdateEvent(event);
    }
}
