package com.noobanidus.dwmh.proxy;

import com.google.common.collect.Lists;
import com.noobanidus.dwmh.config.DWMHConfig;

import java.util.*;
import java.util.stream.Collectors;

public class DataStore {
    private static Map<String, String> PROXY_LIST = new HashMap<>();
    private static Map<String, Set<String>> CLASS_REFERENCE = new HashMap<>();

    static {
        DataStore.putAll("animania", "com.noobanidus.dwmh.proxy.steeds.AnimaniaProxy",
                "mocreatures", "com.noobanidus.dwmh.proxy.steeds.MOCProxy",
                "zawa", "com.noobanidus.dwmh.proxy.steeds.ZawaProxy",
                "ultimate_unicorn_mod", "com.noobanidus.dwmh.proxy.steeds.UnicornProxy",
                "atum", "com.noobanidus.dwmh.proxy.steeds.Atum2Proxy",
                "iceandfire", "com.noobanidus.dwmh.proxy.steeds.IceAndFireProxy",
                "dragonmounts", "com.noobanidus.dwmh.proxy.steeds.DragonMountProxy",
                "varodd", "com.noobanidus.dwmh.proxy.steeds.VaroddProxy",
                "moolands", "com.noobanidus.dwmh.proxy.steeds.MoolandProxy");

        CLASS_REFERENCE.put("zawa", new HashSet<>());
        CLASS_REFERENCE.put("animania", new HashSet<>());
        CLASS_REFERENCE.put("iceandfire", new HashSet<>());
        CLASS_REFERENCE.put("iceandfire_exclusions", new HashSet<>());
        CLASS_REFERENCE.put("ignore", new HashSet<>());
        CLASS_REFERENCE.put("blacklist", new HashSet<>());
    }

    private static void putAll(String... input) {
        assert input.length % 2 == 0;
        for (int i = 0; i < input.length; i += 2) {
            PROXY_LIST.put(input[i], input[i + 1]);
        }
    }

    public static List<Proxy> get() {
        return PROXY_LIST.entrySet().stream().map((a) -> new Proxy(a.getKey(), a.getValue())).collect(Collectors.toList());
    }

    public static List<String> mods () {
        return Lists.newArrayList(PROXY_LIST.keySet());
    }

    public static Set<String> set (String setName) {
        if (CLASS_REFERENCE.containsKey(setName)) return CLASS_REFERENCE.get(setName);

        return new HashSet<>();
    }

    public static class Proxy {
        private String modId;
        private String classPath;

        public Proxy (String modId, String classPath) {
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
            return DWMHConfig.instance.proxy(modId);
        }
    }
}
