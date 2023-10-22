package dev.tenacity.module.impl.player;

import dev.tenacity.config.LocalConfig;
import dev.tenacity.event.impl.game.world.WorldEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.combat.KillAura;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.utils.player.ChatUtil;
import dev.tenacity.utils.player.InventoryUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Random;

public class AutoGrinder extends Module {

  //  private boolean hasend = false;

   // private final TimerUtil timer = new TimerUtil();

    private final BooleanSetting antilimbo = new BooleanSetting("Anti Limno", false);
    public AutoGrinder() {
        super("Auto Grinder", Category.PLAYER,"Auto maticly grinds on vortex hvh");
        addSettings(antilimbo);
    }

    @Override
    public void onDisable() {
       // timer.reset();
       // hasend = false;
        super.onDisable();
    }





    @Override
    public void onMotionEvent(MotionEvent e) {

        ItemStack heldItem = mc.thePlayer.inventory.getCurrentItem();

        boolean hasSword = false;

        for (int i = 0; i < mc.thePlayer.inventory.mainInventory.length; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemSword) {
                hasSword = true;
                break;
            }
        }

        if (!hasSword && (heldItem == null || !(heldItem.getItem() instanceof ItemSword))) {
            //20
            if (mc.thePlayer.ticksExisted % 20 == 0) {
                mc.thePlayer.sendChatMessage("Give me a sword " + (System.currentTimeMillis()));
            }
        }
    }









}
