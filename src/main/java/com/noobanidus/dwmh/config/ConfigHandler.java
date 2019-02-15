package com.noobanidus.dwmh.config;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {
    private static Configuration internalConfig = null;
    private static Map<String, Boolean> PROXY_MAP = new HashMap<>();

    public static Configuration getConfig() {
        if (internalConfig == null) {
            Method getConfig = ReflectionHelper.findMethod(ConfigManager.class, "getConfiguration", null, String.class, String.class);
            getConfig.setAccessible(true);
            Configuration config = null;
            try {
                config = (Configuration) getConfig.invoke(null, DWMH.MODID, null);
            } catch (ReflectiveOperationException e) {
                DWMH.LOG.info("Failed to use reflection to get configuration", e);
            }

            internalConfig = config;
        }

        return internalConfig;
    }

    public static boolean proxy(String proxy) {
        if (PROXY_MAP.containsKey(proxy)) return PROXY_MAP.get(proxy);

        Configuration config = getConfig();

        ConfigCategory cat = config.getCategory("general.proxy settings.enable/disable proxies");

        if (cat == null) return false;

        for (Map.Entry<String, Property> entry : cat.entrySet()) {
            Property prop = entry.getValue();
            String proxyName = DataStore.proxy(entry.getKey());
            PROXY_MAP.put(proxyName, prop.getBoolean());
        }

        return PROXY_MAP.getOrDefault(proxy, false);
    }

    public static void deserialize(NBTTagCompound compound) {

    }

    public static NBTTagCompound serialize() {
        NBTTagCompound output = new NBTTagCompound();

        NBTTagCompound ocarina = new NBTTagCompound();

        ocarina.setDouble("maxDistance", DWMHConfig.Ocarina.maxDistance);
        ocarina.setInteger("cooldown", DWMHConfig.Ocarina.functionality.getCooldown());
        ocarina.setInteger("maxUses", DWMHConfig.Ocarina.functionality.getMaxUses());
        ocarina.setString("repairItem", DWMHConfig.Ocarina.functionality.repairItem);
        ocarina.setString("summonItem", DWMHConfig.Ocarina.functionality.summonItem);
        ocarina.setInteger("summonCost", DWMHConfig.Ocarina.functionality.getSummonCost());

        output.setTag("Ocarina", ocarina);

        NBTTagCompound carrot = new NBTTagCompound();

        carrot.setBoolean("taming", DWMHConfig.EnchantedCarrot.effects.taming);
        carrot.setBoolean("healing", DWMHConfig.EnchantedCarrot.effects.healing);
        carrot.setBoolean("aging", DWMHConfig.EnchantedCarrot.effects.aging);
        carrot.setBoolean("breeding", DWMHConfig.EnchantedCarrot.effects.breeding);

        carrot.setInteger("maxUses", DWMHConfig.EnchantedCarrot.durability.getMaxUses());
        carrot.setString("repairItem", DWMHConfig.EnchantedCarrot.durability.repairItem);
        carrot.setBoolean("breakable", DWMHConfig.EnchantedCarrot.durability.breakableCarrot);

        output.setTag("Carrot", carrot);

        return output;
    }
}
