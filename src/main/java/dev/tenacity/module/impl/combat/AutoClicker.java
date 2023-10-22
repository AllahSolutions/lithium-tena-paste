package dev.tenacity.module.impl.combat;

import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.event.impl.player.input.AttackEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.FlagfolUtil.Utils.MathUtil;
import dev.tenacity.utils.player.PlayerUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.util.MovingObjectPosition;


public class AutoClicker extends Module {
    public static final NumberSetting cps2 = new NumberSetting("Min Cps", 8, 20, 1, 1);
    public static final NumberSetting cps3 = new NumberSetting("Max Cps", 8, 20, 1, 1);
    public static final BooleanSetting rightclick = new BooleanSetting("Rick Click", false);
    public static final BooleanSetting leftclick = new BooleanSetting("left CLick", false);

    private final TimerUtil clickStopWatch = new TimerUtil();
    private int ticksDown, attackTicks;
    private long nextSwing;



    public AutoClicker() {
        super("Clicker", Category.COMBAT, "Auto Clicks");
        addSettings(cps2,cps3,leftclick,rightclick);
    }

    @Override
    public void onAttackEvent(AttackEvent e) {
        this.attackTicks = 0;
        super.onAttackEvent(e);
    }



    @Override
    public void onTickEvent(TickEvent e) {
        this.setSuffix(cps2.getValue().floatValue() + " - " + cps3.getValue().floatValue());
        this.attackTicks++;

        if (clickStopWatch.finished(this.nextSwing) ||
                (mc.thePlayer.hurtTime > 0 && clickStopWatch.finished(this.nextSwing)) && mc.currentScreen == null) {
            final long clicks = (long) (Math.round(MathUtil.getRandom(this.cps2.getValue().intValue(), this.cps3.getValue().intValue())) * 1.5);

            if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                ticksDown++;
            } else {
                ticksDown = 0;
            }

            this.nextSwing = 1000 / clicks;


            if (rightclick.isEnabled() && mc.gameSettings.keyBindUseItem.isKeyDown() && !mc.gameSettings.keyBindAttack.isKeyDown()) {
                PlayerUtils.sendClick(1, true);

                if (Math.random() > 0.9) {
                    PlayerUtils.sendClick(1, true);
                }
            }

            if (leftclick.isEnabled() && ticksDown > 1 && (Math.sin(nextSwing) + 1 > Math.random() || Math.random() > 0.25 || clickStopWatch.finished(4 * 50)) && !mc.gameSettings.keyBindUseItem.isKeyDown() && (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)) {
                PlayerUtils.sendClick(0, true);
            }

            this.clickStopWatch.reset();
        }
        super.onTickEvent(e);
    }


}
