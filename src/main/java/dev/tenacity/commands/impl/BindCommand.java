package dev.tenacity.commands.impl;

import dev.tenacity.Tenacity;
import dev.tenacity.commands.Command;
import dev.tenacity.module.Module;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

public class BindCommand extends Command {

    public BindCommand() {
        super("bind", "Binds a module to a certain key", ".bind [module] [key]");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            usage();
        } else {
            String stringModule = String.join(" ", Arrays.copyOfRange(args, 0, args.length - 1));
            String key = args[args.length - 1];

            try {
                Module module = Tenacity.INSTANCE.getModuleCollection().getModuleByName(stringModule);
                module.getKeybind().setCode(Keyboard.getKeyIndex(key.toUpperCase()));
                sendChatWithPrefix("Set keybind for " + module.getName() + " to " + key.toUpperCase());
            } catch (Exception e) {
                usage();
            }
        }
    }

}
