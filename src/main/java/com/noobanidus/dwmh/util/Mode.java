package com.noobanidus.dwmh.util;

import net.minecraft.util.text.TextFormatting;

public enum Mode {
    LIST("list", TextFormatting.AQUA),
    SUMMON("summon", TextFormatting.GOLD),
    EJECT("eject", TextFormatting.DARK_RED),
    PACK("pack", TextFormatting.GREEN),
    EMPTY(null, null);

    private String type;
    private TextFormatting format;

    Mode(String type, TextFormatting format) {
        this.type = type;
        this.format = format;
    }

    public static Mode fromValue(String type) {
        try {
            return Enum.valueOf(Mode.class, type);
        } catch (IllegalArgumentException e) {
            for (Mode v : values()) {
                if (v != null && v.getType().equals(type)) return v;
            }
        }

        return EMPTY;
    }

    public String getType() {
        return type;
    }

    public String getTranslationKey () {
        if (this == EMPTY) return null;

        return String.format("dwmh.string.mode.%s", getType());
    }

    public TextFormatting getColour () {
        return format;
    }
}
