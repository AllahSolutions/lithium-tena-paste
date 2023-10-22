package dev.tenacity.module.impl.movement;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.NotificationsMod;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.player.ChatUtil;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.EnumChatFormatting;

public class FlagCheck extends Module {
    private int flag;



    public FlagCheck() {
        super("Flag Detector", Category.MISC, "Disables shit on flag");
    }

    @Override
    public void onDisable() {
        flag=0;
        super.onDisable();
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
            if(Tenacity.INSTANCE.isEnabled(Flight.class)) {
                return;
            }
            if(e.getPacket() instanceof S08PacketPlayerPosLook) {
                flag++;
                if(Tenacity.INSTANCE.isEnabled(NotificationsMod.class)) {
                    NotificationManager.post(NotificationType.WARNING, "LagBack", "Disabled Speed");
                } else{
                    ChatUtil.print(EnumChatFormatting.RED + "Flag " + flag);
                }

            }




    }



}
