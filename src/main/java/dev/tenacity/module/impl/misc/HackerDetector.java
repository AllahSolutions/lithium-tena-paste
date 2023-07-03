package dev.tenacity.module.impl.misc;

import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.player.MotionEvent;
import dev.tenacity.hackerdetector.Detection;
import dev.tenacity.hackerdetector.DetectionManager;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.MultipleBoolSetting;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.player.ChatUtil;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.player.PlayerUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class HackerDetector extends Module {

    private final DetectionManager detectionManager = new DetectionManager();
    private final TimerUtil timer = new TimerUtil();

    public int vl;
    private final MultipleBoolSetting detections = new MultipleBoolSetting("Detections",
            new BooleanSetting("Flight A", true),
            new BooleanSetting("Flight B", true),
            new BooleanSetting("Reach A", true));

    public HackerDetector() {
        super("HackerDetector", Category.MISC, "Detects people using cheats inside your game");
        this.addSettings(detections);
    }




    @Override
    public void onTickEvent(TickEvent event) {
        if(mc.theWorld == null || mc.thePlayer == null) return;

        for(Entity entity : mc.theWorld.getLoadedEntityList()) {
            if(entity instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer) entity;
             //   if(entityPlayer != mc.thePlayer) {
                    if(entityPlayer.onGround) {
                        timer.reset();
                    }
                    float nigger;
                    nigger = 0.41999998688697815f;

                    if(entityPlayer.motionY > nigger && entityPlayer.fallDistance <= 0) {
                        if (mc.thePlayer.ticksExisted % 10 == 0) {

                            ChatUtil.print(EnumChatFormatting.DARK_RED + entityPlayer.getName() + EnumChatFormatting.RED + "`s MotionY is Higher By " + (entityPlayer.motionY - nigger) );
                        }
                    }

                //Maybe do not use falldistance

                   if(!entityPlayer.onGround && (timer.hasTimeElapsed((long) 600)) && entityPlayer.fallDistance < 2) {
                       if (mc.thePlayer.ticksExisted % 10 == 0) {
                           ChatUtil.print(EnumChatFormatting.DARK_RED + entityPlayer.getName() + EnumChatFormatting.RED + " Failed Fly");
                       }
                   }

              //  }
            }
        }
    }

}
