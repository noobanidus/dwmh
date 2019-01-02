package com.noobanidus.dwmh.util;

public class KeyTransforms {
    public static String resolveEntityKey(String entityKey) {
        if (entityKey.equalsIgnoreCase("entity.mocreatures:manticorepet.name"))
            return "entity.mocreatures:manticore.name";

        return entityKey;
    }
}
