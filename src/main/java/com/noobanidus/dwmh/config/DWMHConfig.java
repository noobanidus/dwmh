package com.noobanidus.dwmh.config;

import com.noobanidus.dwmh.DWMH;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("WeakerAccess")
@Config(modid = DWMH.MODID)
@Mod.EventBusSubscriber(modid = DWMH.MODID)
public class DWMHConfig {

    @Config.Comment("Settings related to the Ocarina and its use")
    public static Ocarina ocarina = new Ocarina();

    public static class Ocarina {
        @Config.Comment("Specify the maximum distance steeds can be summoned from (set to 0 for infinite). Excludes entities in unloaded chunks or different dimensions")
        @Config.RangeDouble(min=0d, max=100000d)
        public double maxDistance = 200d;

        @Config.Comment("Set to true to enable summoning of horses even if they are being ridden by someone else.")
        public boolean otherRiders = false;

        @Config.Comment("Set to true to swap the effect of sneaking. Thus, right-clicking will list entities, while sneak-right-clicking will summon them.")
        public boolean swap = false;

        @Config.Comment("Set to false to prevent setting a home when summoning steeds. This should prevent them wandering too far from where you summoned them to, but may cause interactions with other mods.")
        public boolean home = true;

        @Config.Comment("Set to true to skip removing a home point when you dismount a horse. This may help with mods that set a home upon dismounting, as well. Otherwise, setting this to false will make horses believe their home is where you last summoned them to was.")
        public boolean skipDismount = false;

        @Config.Comment("Options related to the functionality of the Ocarina")
        public Functionality functionality = new Functionality();

        public class Functionality {
            @Config.Comment("Specify a cooldown for each usage of the ocarina, or specify 0 to disable")
            public int cooldown = 0;

            @Config.Comment("Specify the maximum durability of the Ocarina. One horse summoned costs one durability. Set to 0 to disable durability.")
            public int maxUses = 0;

            @Config.Comment("Specify the item that can be used to repair the Ocarina in an anvil. Items with NBT are not supported. Format mod:item:metadata (use \"minecraft\" for vanilla items), use 0 for no meteadata.")
            public String repairItem = "minecraft:golden_carrot:0";
        }

        @Config.Comment("Options related to audio and text output of the Ocarina")
        public Responses responses = new Responses();

        public class Responses {
            @Config.Comment("Set to true to disable messages when teleporting a horse to you.")
            public boolean quiet = false;

            @Config.Comment("Set to true to compact multiple horse-summoned messages into one.")
            public boolean simple = false;

            @Config.Comment("Set to false to disable showing the distance and direction a horse is away from you")
            public boolean distance = true;

            @Config.Comment("Set to false to disable sounds being played when the Ocarina is use. These sounds are played on the PLAYERS channel.")
            public boolean sounds = true;
        }
    }

    @Config.Comment("Settings related to the Enchanted Carrot and its use")
    public static Carrot carrot = new Carrot();

    public static class Carrot {
        @Config.RequiresMcRestart
        @Config.Comment("Set to false to disable all the effects of the enchanted carrot. Disabling each effect individually has the same effect.")
        public boolean enabled = true;

        @Config.Comment("Settings related to the durability of the carrot.")
        public Durability durability = new Durability();

        public class Durability {
            @Config.RequiresMcRestart
            @Config.RangeInt(min = 0)
            @Config.Comment("Maximum number of uses before the enchanted carrot is destroyed or, with the unbreakable setting, becomes unusable")
            public int maxUses = 30;

            @Config.Comment("Set to true to prevent the carrot from breaking when reaching 0 durability")
            public boolean unbreakable = false;

            @Config.Comment("Specify the item that can be used to repair the carrot in an anvil. Items with NBT are not supported. Format: mod:item:metadata. Use \"minecraft\" for vanilla items, and 0 if no metadata is specified.")
            public String repairItem = "minecraft:gold_block:0";
        }

        @Config.Comment("Allows fine-tuning of the individual effects of the Enchanted Carrot")
        public CarrotEffects effects = new CarrotEffects();

        public static class CarrotEffects {
            @Config.Comment("Set to false to prevent the automatic taming of rideable entities")
            public boolean taming = true;

            @Config.Comment("Set to false to prevent the healing to full of injured rideable entities")
            public boolean healing = true;

            @Config.Comment("Set to false to prevent the ageing to adulthood of rideable child entities")
            public boolean ageing = true;

            @Config.Comment("Set to false to prevent putting tamed, adult rideable entities into \"breeding\" mode")
            public boolean breeding = true;
        }

        @Config.Comment("Set to true to enable the enchantment glint. Useful if you are using a texture pack that overrides the Carrot animation.")
        public boolean glint = false;
    }

    @SubscribeEvent
    public static void onConfigChanged (ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(DWMH.MODID)) {
            ConfigManager.sync(DWMH.MODID, Config.Type.INSTANCE);
            // if (!event.isWorldRunning()) {
            // potential config sync need to go here
            // }
        }
    }
}
