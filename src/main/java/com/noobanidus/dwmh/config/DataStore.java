package com.noobanidus.dwmh.config;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;

public class DataStore {
    private static Map<String, String> PROXY_LIST = new HashMap<>();
    private static Map<String, String> CONFIG_LIST = new HashMap<>();
    private static Map<String, Set<String>> CLASS_REFERENCE = new HashMap<>();

    static {
        DataStore.putAll(PROXY_LIST, "animania", "com.noobanidus.dwmh.proxy.steeds.AnimaniaProxy",
                "mocreatures", "com.noobanidus.dwmh.proxy.steeds.MOCProxy",
                "zawa", "com.noobanidus.dwmh.proxy.steeds.ZawaProxy",
                "ultimate_unicorn_mod", "com.noobanidus.dwmh.proxy.steeds.UnicornProxy",
                "atum", "com.noobanidus.dwmh.proxy.steeds.Atum2Proxy",
                "iceandfire", "com.noobanidus.dwmh.proxy.steeds.IceAndFireProxy",
                "dragonmounts", "com.noobanidus.dwmh.proxy.steeds.DragonMountProxy",
                "varodd", "com.noobanidus.dwmh.proxy.steeds.VaroddProxy",
                "moolands", "com.noobanidus.dwmh.proxy.steeds.MoolandProxy",
                "resourcehogs", "com.noobanidus.dwmh.proxy.steeds.HogProxy",
                "merpig", "com.noobanidus.dwmh.proxy.steeds.MerpigProxy");

        DataStore.putAll(CONFIG_LIST,
                "Animania", "animania",
                "Mo Creatures", "mocreatures",
                "ZAWA Rebuilt", "zawa",
                "Ultimate Unicorn Mod", "ultimate_unicorn_mod",
                "Atum", "atum",
                "Ice & Fire", "iceandfire",
                "Dragon Mounts 2", "dragonmounts",
                "Various Oddities", "varodd",
                "Moolands", "moolands",
                "Resource Hogs", "resourcehogs",
                "Merpig", "merpig");

        CLASS_REFERENCE.put("zawa", new HashSet<>());
        CLASS_REFERENCE.put("animania", new HashSet<>());
        CLASS_REFERENCE.put("atum", new HashSet<>());
        CLASS_REFERENCE.put("iceandfire", new HashSet<>());
        CLASS_REFERENCE.put("iceandfire_exclusions", new HashSet<>());
        CLASS_REFERENCE.put("ignore", new HashSet<>());
        CLASS_REFERENCE.put("blacklist", new HashSet<>());
    }

    private static void putAll(Map<String, String> map, String... input) {
        assert input.length % 2 == 0;
        for (int i = 0; i < input.length; i += 2) {
            map.put(input[i], input[i + 1]);
        }
    }

    public static List<Proxy> get() {
        return PROXY_LIST.entrySet().stream().map((a) -> new Proxy(a.getKey(), a.getValue())).collect(Collectors.toList());
    }

    public static List<String> mods() {
        return Lists.newArrayList(PROXY_LIST.keySet());
    }

    public static Set<String> set(String setName) {
        if (CLASS_REFERENCE.containsKey(setName)) return CLASS_REFERENCE.get(setName);

        return null;
    }

    public static String proxy(String configName) {
        if (CONFIG_LIST.containsKey(configName)) return CONFIG_LIST.get(configName);

        return null;
    }

    public static class Proxy {
        private String modId;
        private String classPath;

        public Proxy(String modId, String classPath) {
            this.modId = modId;
            this.classPath = classPath;
        }

        public String getModId() {
            return modId;
        }

        public String getClassPath() {
            return classPath;
        }

        public boolean isEnabled() {
            return ConfigHandler.proxy(modId);
        }
    }
}
