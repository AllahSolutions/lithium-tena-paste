package dev.tenacity.module.impl.movement;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.player.movement.MoveEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.NotificationsMod;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.player.ChatUtil;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.EnumChatFormatting;

public class AirStuck extends Module {



    public AirStuck() {
        super("Air stuck", Category.MISC, "Stuck u in the air");
    }



    @Override
    public void onMoveEvent(MoveEvent e) {
        e.setSpeed(0);
        e.setY(0);
        super.onMoveEvent(e);
    }



}
