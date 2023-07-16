package dev.tenacity.scripting.api.bindings;

import dev.tenacity.utils.Utils;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.FontUtil;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

import java.util.Arrays;

@Exclude(Strategy.NAME_REMAPPING)
public class FontBinding implements Utils {

    public AbstractFontRenderer getCustomFont(String fontName, int fontSize) {
        FontUtil.FontType fontType = Arrays.stream(FontUtil.FontType.values()).filter(fontType1 -> fontType1.name().equals(fontName)).findFirst().orElse(FontUtil.FontType.LITHIUM);
        return fontType.size(fontSize);
    }

    public AbstractFontRenderer getMinecraftFontRenderer() {
        return mc.fontRendererObj;
    }


    public AbstractFontRenderer getlithiumFont14() {return lithiumFont14; }
    public AbstractFontRenderer getlithiumFont16() {return lithiumFont16; }
    public AbstractFontRenderer getlithiumFont18() {return lithiumFont18; }
    public AbstractFontRenderer getlithiumFont20() {return lithiumFont20; }
    public AbstractFontRenderer getlithiumFont22() {return lithiumFont22; }
    public AbstractFontRenderer getlithiumFont24() {return lithiumFont24; }
    public AbstractFontRenderer getlithiumFont26() {return lithiumFont26; }
    public AbstractFontRenderer getlithiumFont28() {return lithiumFont28; }
    public AbstractFontRenderer getlithiumFont32() {return lithiumFont32; }
    public AbstractFontRenderer getlithiumFont40() {return lithiumFont40; }
    public AbstractFontRenderer getlithiumFont80() {return lithiumFont80; }
}
