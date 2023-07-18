package dev.tenacity.module.impl.combat;

import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.game.WorldEvent;
import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.network.PacketSendEvent;
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

public class Velocity extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Packet","Grim", "Packet", "Reverse", "Polar");
    private final NumberSetting horizontal = new NumberSetting("Horizontal", 0, 100, 0, 1);
    private final NumberSetting vertical = new NumberSetting("Vertical", 0, 100, 0, 1);
    private int count;
    private boolean velo;


    public Velocity() {
        super("Velocity", Category.COMBAT, "Reduces your velocity.");
        this.addSettings(mode, horizontal, vertical);
    }
    @Override
    public void onDisable() {
        count = 0;
        velo = false;
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        switch (mode.getMode()) {
            case "Grim": {
                if(velo) {
                    if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                        if (count <= 7) {
                            event.cancel();
                        }
                        count++;
                    }
                }else{
                    count = 0;
                }
            }
        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {

        Packet <?> packet = event.getPacket();
        if (packet instanceof S12PacketEntityVelocity) {
            velo = true;
        } else{
            velo = false;
        }
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
                    // s12.motionY *= vertical.getValue() / 100;
                    s12.motionZ *= -s12.motionZ;
                    break;
                }
                case "Grim": {
                    s12.motionX *= horizontal.getValue() / 100;
                    s12.motionY *= vertical.getValue() / 100;
                    s12.motionZ *= horizontal.getValue() / 100;
                    break;
                }
                default: {
                    System.out.println("Invalid velocity string.");
                    break;
                }
            }
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
                   // s12.motionY *= vertical.getValue() / 100;
                    s12.motionZ *= -s12.motionZ;
                    break;
                }
                case "Grim": {
                    s12.motionX *= horizontal.getValue() / 100;
                    s12.motionY *= vertical.getValue() / 100;
                    s12.motionZ *= horizontal.getValue() / 100;
                    break;
                }
                default: {
                    System.out.println("Invalid velocity string.");
                    break;
                }
            }
        }

        super.onPacketReceiveEvent(event);
    }

    @Override
    public void onTickEvent(TickEvent event) {

        if ("Polar".equals(this.mode.getMode()) && mc.thePlayer.hurtTime > 0) {
            mc.thePlayer.motionX /= (mc.thePlayer.hurtTime / 5.0D);
            mc.thePlayer.motionZ /= (mc.thePlayer.hurtTime / 5.0D);
        }

        super.onTickEvent(event);
    }
}
