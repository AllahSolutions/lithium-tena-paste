package dev.tenacity.module.impl.misc;

import dev.tenacity.anticheat.DetectionManager;
import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.player.ChatReceivedEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.utils.player.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TestModule extends Module {



    public TestModule() {
        super("HackerRoast", Category.MISC, "basicly roasts people who call u a hacker lol");
    }

    @Override
    public void onChatReceivedEvent(ChatReceivedEvent event) {


        String ezz1 = "Hacking? Nah, i am just levitating above your skill level." ;
        String ezz2 = "i am just using badlione client lol";
        String ezz3 = "Your skills are like a potato trying to win a game of chess.";
        String ezz4 = "I make you look more lost than a villager without a job.";
        String ezz5 = "My skill level is higher than your render distance, u can't even see u coming.";
        String ezz6 = "I am not hacking stop folding maybe?";
        String ezz7 = "Just accept your bad lol";
        String ezz8 = "My skills are so good, i make u think i am hacking. it's like accusing a pig of flying.";
        String ezz9 = "I am on lunar lol";
        String ezz10 = "Father less Moment";

        String[] variables = {ezz1, ezz2, ezz3, ezz4, ezz5, ezz6, ezz7, ezz8, ezz9, ezz10};

        Random random = new Random();
        int index = random.nextInt(variables.length);

        String randomVariable = variables[index];
        String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (message.contains("Hacker")||message.contains("hacker")||message.contains(mc.thePlayer.getName() + " is hacking")||message.contains(mc.thePlayer.getName() + " is Hacking")||message.contains("Bhop")||message.contains("bhop")||message.contains("Speed")||message.contains("speed")||message.contains("Scaffold")||message.contains("scaffold")||message.contains("killaura")||message.contains("Killaura")||message.contains("staff " + mc.thePlayer.getName()+ " is hacking")) {
            mc.thePlayer.sendChatMessage(randomVariable);
        }

           // }
    }

}