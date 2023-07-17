package dev.tenacity.module.impl.misc;

import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.anticheat.DetectionManager;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.utils.player.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.stream.Collectors;

public class AntiCheat extends Module {

    private final DetectionManager detectionManager = new DetectionManager();

    public AntiCheat() {
        super("AntiCheat", Category.MISC, "Detects people using cheats inside your game");
    }

    @Override
    public void onTickEvent(TickEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (mc.thePlayer.ticksExisted % 5 != 0) {
            return;
        }

        List<EntityPlayer> players = mc.theWorld.loadedEntityList

                .stream()

                .filter(entity -> entity instanceof EntityPlayer)

                .map(entity -> (EntityPlayer) entity)

                .collect(Collectors.toList());

        players.forEach(player -> {
            this.detectionManager.getDetections().forEach(detection -> {
                boolean result = detection.runCheck(player);

                if (result) {
                    ChatUtil.print(EnumChatFormatting.WHITE + player.getName() + EnumChatFormatting.GRAY + " has failed " + EnumChatFormatting.WHITE + detection.getName());
                }
            });
        });
    }

}