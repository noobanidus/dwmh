package com.noobanidus.dwmh.config;

import com.google.common.collect.Lists;
import com.noobanidus.dwmh.DWMH;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@SuppressWarnings("WeakerAccess")
@Config(modid = DWMH.MODID)
@Mod.EventBusSubscriber(modid = DWMH.MODID)
public class DWMHConfig {

    @Config.Comment("Settings related to the Ocarina and its use")
    public static Ocarina Ocarina = new Ocarina();
    public static class Ocarina {
        @Config.Comment("Specify the maximum distance steeds can be summoned from (set to 0 for infinite). Excludes entities in unloaded chunks or different dimensions")
        @Config.RangeDouble(min=0d, max=100000d)
        @Config.Name("Maximum Summon Distance")
        public double maxDistance = 200d;

        @Config.Comment("Set to true to enable summoning of horses even if they are being ridden by someone else.")
        @Config.Name("Summon With Other Riders")
        public boolean otherRiders = false;

        @Config.Comment("Set to true to swap the effect of sneaking. Thus, right-clicking will list entities, while sneak-right-clicking will summon them.")
        @Config.Name("Swap Sneak Functionality")
        public boolean swap = false;

        @Config.Comment("Set to false to prevent setting a home when summoning steeds. This should prevent them wandering too far from where you summoned them to, but may cause interactions with other mods.")
        @Config.Name("Set Home Location When Summoned")
        public boolean home = true;

        @Config.Comment("Set to true to skip removing a home point when you dismount a horse. This may help with mods that set a home upon dismounting, as well. Otherwise, setting this to false will make horses believe their home is where you last summoned them to was.")
        @Config.Name("Skip Clearing Home on Dismount")
        public boolean skipDismount = false;

        @Config.Comment("Options related to the functionality of the Ocarina")
        public Functionality functionality = new Functionality();
        public class Functionality {
            @Config.Comment("Specify a cooldown for each usage of the Ocarina, or specify 0 to disable")
            @Config.Name("Ocarina Cooldown Duration")
            public int cooldown = 0;

            @Config.Comment("Specify the maximum durability of the Ocarina. One horse summoned costs one durability. Set to 0 to disable durability.")
            @Config.Name("Maximum Ocarina Durability")
            public int maxUses = 0;

            @Config.Comment("Specify the item that can be used to repair the Ocarina in an anvil. Items with NBT are not supported. Format mod:item:metadata (use \"minecraft\" for vanilla items), use 0 for no meteadata.")
            @Config.Name("Ocarina Repair Item")
            public String repairItem = "minecraft:golden_carrot:0";
        }

        @Config.Comment("Options related to audio and text output of the Ocarina")
        public Responses responses = new Responses();
        public class Responses {
            @Config.Comment("Set to true to disable messages when teleporting a horse to you.")
            @Config.Name("Disable PSummoned Messages")
            public boolean quiet = false;

            @Config.Comment("Set to true to compact multiple horse-summoned messages into one.")
            @Config.Name("Combine Summoned Messages")
            public boolean simple = false;

            @Config.Comment("Set to false to disable showing the distance and direction a horse is away from you")
            @Config.Name("Disable Distance/Direction")
            public boolean distance = true;

            @Config.Comment("Set to false to disable sounds being played when the Ocarina is use. These sounds are played on the PLAYERS channel.")
            @Config.Name("Disable Ocarina Tunes")
            public boolean sounds = true;
        }
    }

    @Config.Comment("Settings related to the Enchanted Carrot and its use")
    public static Carrot EnchantedCarrot = new Carrot();
    public static class Carrot {
        @Config.RequiresMcRestart
        @Config.Comment("Set to false to disable all the effects of the enchanted EnchantedCarrot. Disabling each effect individually has the same effect.")
        @Config.Name("Disable Carrot")
        public boolean enabled = true;

        @Config.Comment("Settings related to the durability of the EnchantedCarrot.")
        public Durability durability = new Durability();
        public class Durability {
            @Config.RequiresMcRestart
            @Config.RangeInt(min = 0)
            @Config.Comment("Maximum number of uses before the enchanted EnchantedCarrot is destroyed or, with the unbreakable setting, becomes unusable")
            @Config.Name("Maximum Carrot Durability")
            public int maxUses = 30;

            @Config.Comment("Set to true to prevent the EnchantedCarrot from breaking when reaching 0 durability")
            @Config.Name("Unuseable At Minimum Durability")
            public boolean unbreakable = false;

            @Config.Comment("Specify the item that can be used to repair the EnchantedCarrot in an anvil. Items with NBT are not supported. Format: mod:item:metadata. Use \"minecraft\" for vanilla items, and 0 if no metadata is specified.")
            @Config.Name("Carrot Repair Item")
            public String repairItem = "minecraft:gold_block:0";
        }

        @Config.Comment("Allows fine-tuning of the individual effects of the Enchanted Carrot")
        public CarrotEffects effects = new CarrotEffects();
        public static class CarrotEffects {
            @Config.Comment("Set to false to prevent the automatic taming of rideable entities")
            @Config.Name("Enable Eligible Entity Taming")
            public boolean taming = true;

            @Config.Comment("Set to false to prevent the healing to full of injured rideable entities")
            @Config.Name("Enable Eligible Entity Healing")
            public boolean healing = true;

            @Config.Comment("Set to false to prevent the aging to adulthood of rideable child entities")
            @Config.Name("Enable Eligible Entity Aging")
            public boolean aging = true;

            @Config.Comment("Set to false to prevent putting tamed, adult rideable entities into \"breeding\" mode")
            @Config.Name("Enable Eligible Entity Breeding")
            public boolean breeding = true;
        }

        @Config.Comment("Set to true to enable the enchantment glint. Useful if you are using a texture pack that overrides the Carrot animation.")
        @Config.Name("Enable Enchantment Glint")
        public boolean glint = false;
    }

    @Config.Comment("Options relating to the individual proxies.")
    public static Proxies proxies = new Proxies();
    public static class Proxies {

        @Config.Comment("Overrides to specifically disable certain proxies")
        public Enable enable = new Enable();
        public class Enable {
            @Config.Comment("Set to false to disable the Animania proxy (even if it would normally load)")
            @Config.Name("Animania")
            public boolean animania = true;

            @Config.Comment("Set to false to disable the Mo Creatures proxy (even if it would normally load)")
            @Config.Name("Mo Creatures")
            public boolean mocreatures = true;

            @Config.Comment("Set to false to disable the ZAWA Rebuilt proxy (even if it would normally load)")
            @Config.Name("ZAWA Rebuilt")
            public boolean zawa = true;

            @Config.Comment("Set to false to disable the Ultimate Unicorn Mod proxy (even if it would normally load)")
            @Config.Name("Ultimate Unicorn Mod")
            public boolean ultimate_unicorn_mod = true;
        }

        @Config.Comment("Options related specifically to the Animania ")
        public Animania Animania = new Animania();
        public class Animania {
            @Config.Comment("Specify list of Animania classes that are considered steeds. Use /dwmh entity while targetting to get the full name")
            public String[] classes = new String[]{"com.animania.common.entities.horses.EntityMareBase", "com.animania.common.entities.horses.EntityStallionBase"};

            @Config.Comment("Set to false to enable the listing of every Animania entity, not just those you are able to summon. This may produce an excess number of entries.")
            @Config.Name("Only Named")
            public boolean onlyNamed = true;
        }

        @Config.Comment("Options related specifically to ZAWA")
        public ZAWA ZAWA = new ZAWA();
        public class ZAWA {
            @Config.Comment("Specify list of ZAWA Rebuilt classes that are considered steeds. Use /dwmh entity while targetting to get the full name")
            public String[] classes = new String[]{"org.zawamod.entity.land.EntityAsianElephant", "org.zawamod.entity.land.EntityGaur", "org.zawamod.entity.land.EntityGrevysZebra", "org.zawamod.entity.land.EntityOkapi", "org.zawamod.entity.land.EntityReticulatedGiraffe"};
        }

        @Config.Comment("Options related specifically to Mo Creatures")
        public MoCreatures MoCreatures = new MoCreatures();
        public class MoCreatures {
            @Config.Comment("Specify list of entity translation keys which should be modified to insert spaces (where relevant)")
            public String[] entities = new String[]{"entity.mocreatures:blackbear.name", "entity.mocreatures:grizzlybear.name", "entity.mocreatures:komododragon.name", "entity.mocreatures:petscorpion.name", "entity.mocreatures:wildhorse.name", "entity.mocreatures:wildpolarbear.name"};
        }

        @Config.Comment("Specify a blacklist of entities that should always be ignored, even if generally loaded by their proxy.")
        @Config.Name("Entity Blacklist")
        public String[] blacklist = new String[]{};
    }

    @SubscribeEvent
    public static void onConfigChanged (ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(DWMH.MODID)) {
            ConfigManager.sync(DWMH.MODID, Config.Type.INSTANCE);
            DWMH.resolveClasses();
        }
    }
}
