package com.noobanidus.dwmh.config;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
    @Config.Name("Ocarina Settings")
    @Config.LangKey("config.category.ocarina")
    public static Ocarina Ocarina = new Ocarina();
    @Config.Comment("Settings related to the Enchanted Carrot and its use")
    @Config.Name("Enchanted Carrot Settings")
    @Config.LangKey("config.category.carrot")
    public static Carrot EnchantedCarrot = new Carrot();
    @Config.Comment("Options relating to the individual proxies.")
    @Config.Name("Proxy Settings")
    @Config.LangKey("config.category.proxy")
    public static Proxies proxies = new Proxies();
    @Config.Comment("Specify a blacklist of entities that should always be ignored, even if generally loaded by their proxy.")
    @Config.Name("Entity Blacklist")
    @Config.LangKey("config.base.blacklist")
    public static String[] blacklist = new String[]{};
    @Config.RequiresMcRestart
    @Config.Comment("Set to false to disable the craftable saddle recipes")
    @Config.Name("Enable Saddle Recipe")
    @Config.LangKey("config.base.saddle")
    public static boolean saddleRecipe = true;

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(DWMH.MODID)) {
            ConfigManager.sync(DWMH.MODID, Config.Type.INSTANCE);
            DWMH.resolveClasses();
            Registrar.ocarina.updateConfig();
            Registrar.carrot.updateConfig();
            Registrar.ocarina.checkRepairItem();
            Registrar.ocarina.checkCostItem();
            Registrar.carrot.checkRepairItem();

            if (Ocarina.functionality.getMaxUses() != Ocarina.functionality.maxUses)
                DWMH.LOG.error(String.format("Invalid configuration value for Ocarina:maxUses: |%d|. Using default 0 instead.", Ocarina.functionality.maxUses));
            if (Ocarina.functionality.getCooldown() != Ocarina.functionality.cooldown)
                DWMH.LOG.error(String.format("Invalid configuration value for Ocarina:cooldown: |%d|. Using default 0 instead.", Ocarina.functionality.cooldown));
            if (Ocarina.functionality.getSummonCost() != Ocarina.functionality.summonCost)
                DWMH.LOG.error(String.format("Invalid configuration value for Ocarina:summonCost: |%d|. Using default 0 instead.", Ocarina.functionality.summonCost));
            if (EnchantedCarrot.durability.getMaxUses() != EnchantedCarrot.durability.maxUses)
                DWMH.LOG.error(String.format("Invalid configuration value for EnchantedCarrot:maxUses: |%d|. Using default 1 instead.", EnchantedCarrot.durability.maxUses));
        }
    }

    public static class Ocarina {
        @Config.LangKey("config.ocarina.max_distance")
        @Config.Comment("Specify the maximum distance steeds can be summoned from (set to 0 for infinite). Excludes entities in unloaded chunks or different dimensions")
        @Config.RangeDouble(min = 0d)
        @Config.Name("Maximum Summon Distance")
        public double maxDistance = 200d;
        @Config.LangKey("config.ocarina.other_riders")
        @Config.Comment("Set to true to enable summoning of horses even if they are being ridden by someone else.")
        @Config.Name("Summon With Other Riders")
        public boolean otherRiders = false;
        @Config.LangKey("config.ocarina.swap_sneak")
        @Config.Comment("Set to true to swap the effect of sneaking. Thus, right-clicking will list entities, while sneak-right-clicking will summon them.")
        @Config.Name("Swap Sneak Functionality")
        public boolean swap = false;
        @Config.LangKey("config.ocarina.set_home")
        @Config.Comment("Set to false to prevent setting a home when summoning steeds. This should prevent them wandering too far from where you summoned them to, but may cause interactions with other mods.")
        @Config.Name("Set Home Location When Summoned")
        public boolean home = true;
        @Config.LangKey("config.ocarina.remove_home")
        @Config.Comment("Set to true to skip removing a home point when you dismount a horse. This may help with mods that set a home upon dismounting, as well. Otherwise, setting this to false will make horses believe their home is where you last summoned them to was.")
        @Config.Name("Skip Clearing Home on Dismount")
        public boolean skipDismount = false;
        @Config.LangKey("config.category.ocarina.functionality")
        @Config.Comment("Options related to the functionality of the Ocarina")
        @Config.Name("Functionality")
        public Functionality functionality = new Functionality();
        @Config.LangKey("config.category.ocarina.responses")
        @Config.Comment("Options related to audio and text output of the Ocarina")
        @Config.Name("Responses")
        public Responses responses = new Responses();

        public double getMaxDistance() {
            return Math.max(maxDistance, 0.0d);
        }

        public class Functionality {
            @Config.LangKey("config.ocarina.functionality.cooldown")
            @Config.Comment("Specify a cooldown for each usage of the Ocarina, or specify 0 to disable")
            @Config.Name("Ocarina Cooldown Duration")
            @Config.RangeInt(min = 0)
            public int cooldown = 0;
            @Config.LangKey("config.ocarina.functionality.max_durability")
            @Config.Comment("Specify the maximum durability of the Ocarina. One horse summoned costs one durability. Set to 0 to disable durability.")
            @Config.Name("Maximum Ocarina Durability")
            @Config.RangeInt(min = 0)
            public int maxUses = 0;
            @Config.LangKey("config.ocarina.functionality.repair_item")
            @Config.Comment("Specify the item that can be used to repair the Ocarina in an anvil. Items with NBT are not supported. Format mod:item:metadata (use \"minecraft\" for vanilla items), use 0 for no meteadata.")
            @Config.Name("Ocarina Repair Item")
            public String repairItem = "minecraft:golden_carrot:0";
            @Config.Ignore
            public ItemStack repairItemDefault = new ItemStack(Items.GOLDEN_CARROT);
            @Config.LangKey("config.ocarina.functionality.summon_item")
            @Config.Comment("Specify the item to consume from the player's inventory before summoning a horse. Format: mod:item:metadata (use \"minecraft\" for vanilla items), use 0 for no metadata. Items with NBT are not supported.")
            @Config.Name("Summon Item")
            public String summonItem = "minecraft:carrot:0";
            @Config.Ignore
            public ItemStack summonItemStack = new ItemStack(Items.CARROT);
            @Config.LangKey("config.ocarina.functionality.summon_cost")
            @Config.Comment("Specify the quantity of the item to consume from the player's inventory before summoning a horse. Set to 0 to consume nothing.")
            @Config.Name("Summon Cost")
            @Config.RangeInt(min = 0)
            public int summonCost = 0;

            public int getCooldown() {
                return Math.max(cooldown, 0);
            }

            public int getMaxUses() {
                return Math.max(maxUses, 0);
            }

            public int getSummonCost() {
                return Math.max(summonCost, 0);
            }
        }

        public class Responses {
            @Config.LangKey("config.ocarina.responses.disable_summon_message")
            @Config.Comment("Set to true to disable messages when teleporting a horse to you.")
            @Config.Name("Disable Summoned Messages")
            public boolean quiet = false;

            @Config.LangKey("config.ocarina.responses.combine_summon_message")
            @Config.Comment("Set to true to compact multiple horse-summoned messages into one.")
            @Config.Name("Combine Summoned Messages")
            public boolean simple = false;

            @Config.LangKey("config.ocarina.responses.enable_distance")
            @Config.Comment("Set to false to disable showing the distance and direction a horse is away from you")
            @Config.Name("Enable Distance/Direction")
            public boolean distance = true;

            @Config.LangKey("config.ocarina.responses.sounds")
            @Config.Comment("Set to false to disable sounds being played when the Ocarina is use. These sounds are played on the PLAYERS channel.")
            @Config.Name("Enable Ocarina Tunes")
            public boolean sounds = true;

            @Config.LangKey("config.ocarina.responses.sound_delay")
            @Config.Comment("The delay in seconds between uses of the Ocarina causing a sound event. The Ocarina will still trigger, but silently, during this delay.")
            @Config.Name("Ocarina Sound Delay")
            public int soundDelay = 5;

            @Config.LangKey("config.ocarina.responses.disable_llamas")
            @Config.Comment("Set to true to prevent llamas from being listed with the Ocarina.")
            @Config.Name("Disable Listing Llama")
            public boolean noLlamas = false;
        }
    }

    public static class Carrot {
        @Config.RequiresMcRestart
        @Config.LangKey("config.carrot.recipe")
        @Config.Comment("Set to false to disable the Enchanted Carrot recipe from being registered.")
        @Config.Name("Enable Carrot Recipe")
        public boolean enabled = true;
        @Config.LangKey("config.category.carrot.durability")
        @Config.Comment("Settings related to the durability of the Enchanted Carrot.")
        @Config.Name("Durability settings")
        public Durability durability = new Durability();
        @Config.LangKey("config.category.carrot.effects")
        @Config.Comment("Allows fine-tuning of the individual effects of the Enchanted Carrot")
        @Config.Name("Effects")
        public CarrotEffects effects = new CarrotEffects();
        @Config.LangKey("config.category.carrot.messages")
        @Config.Comment("Determine whether messages are displayed when certain functions of the carrot are used")
        @Config.Name("Messages")
        public CarrotMessages messages = new CarrotMessages();
        @Config.LangKey("config.carrot.glint")
        @Config.Comment("Set to true to enable the enchantment glint. Useful if you are using a texture pack that overrides the Carrot animation. Client-side only.")
        @Config.Name("Enable Enchantment Glint")
        public boolean glint = false;

        public static class CarrotEffects {
            @Config.LangKey("config.carrot.effects.taming")
            @Config.Comment("Set to false to prevent the automatic taming of rideable entities")
            @Config.Name("Enable Eligible Entity Taming")
            public boolean taming = true;

            @Config.LangKey("config.carrot.effects.healing")
            @Config.Comment("Set to false to prevent the healing to full of injured rideable entities")
            @Config.Name("Enable Eligible Entity Healing")
            public boolean healing = true;

            @Config.LangKey("config.carrot.effects.ageing")
            @Config.Comment("Set to false to prevent the ageing to adulthood of rideable child entities")
            @Config.Name("Enable Eligible Entity Ageing")
            public boolean aging = true;

            @Config.LangKey("config.carrot.effects.breeding")
            @Config.Comment("Set to false to prevent putting tamed, adult rideable entities into \"breeding\" mode")
            @Config.Name("Enable Eligible Entity Breeding")
            public boolean breeding = true;
        }

        public static class CarrotMessages {
            @Config.LangKey("config.carrot.messages.taming")
            @Config.Comment("Set to false to prevent messages while taming")
            @Config.Name("Enable Eligible Entity Taming Message")
            public boolean taming = true;

            @Config.LangKey("config.carrot.messages.healing")
            @Config.Comment("Set to false to prevent messages while healing")
            @Config.Name("Enable Eligible Entity Healing Message")
            public boolean healing = true;

            @Config.LangKey("config.carrot.messages.ageing")
            @Config.Comment("Set to false to prevent messages while ageing")
            @Config.Name("Enable Eligible Entity Ageing Message")
            public boolean aging = true;

            @Config.LangKey("config.carrot.messages.breeding")
            @Config.Comment("Set to false to prevent messages while breeding")
            @Config.Name("Enable Eligible Entity Breeding Message")
            public boolean breeding = true;
        }

        public class Durability {
            @Config.RangeInt(min = 1)
            @Config.LangKey("config.carrot.durability.maximum_durability")
            @Config.Comment("Maximum number of uses before the enchanted Enchanted Carrot becomes unusable")
            @Config.Name("Maximum Carrot Durability")
            public int maxUses = 30;

            @Config.LangKey("config.carrot.durability.repair_item")
            @Config.Comment("Specify the item that can be used to repair the Enchanted Carrot in an anvil. Items with NBT are not supported. Format: mod:item:metadata. Use \"minecraft\" for vanilla items, and 0 if no metadata is specified.")
            @Config.Name("Carrot Repair Item")
            public String repairItem = "minecraft:gold_block:0";
            @Config.Ignore
            public ItemStack repairItemDefault = new ItemStack(Blocks.GOLD_BLOCK);

            @Config.LangKey("config.carrot.durability.breaks")
            @Config.Comment("Set to true to have the carrot break when it reaches 0 durability instead of becoming unusable")
            @Config.Name("Carrot Is Breakable")
            public boolean breakableCarrot = false;

            public int getMaxUses() {
                return Math.max(maxUses, 1);
            }
        }
    }

    public static class Proxies {

        @Config.LangKey("config.category.proxies.disable")
        @Config.Comment("Overrides to specifically disable certain proxies")
        @Config.Name("Enable/Disable Proxies")
        public Enable enable = new Enable();
        @Config.LangKey("config.category.proxies.animania")
        @Config.Comment("Options related specifically to the Animania ")
        @Config.Name("Animania Settings")
        public Animania Animania = new Animania();
        @Config.LangKey("config.category.proxies.zawa")
        @Config.Comment("Options related specifically to ZAWA")
        @Config.Name("ZAWA Settings")
        public ZAWA ZAWA = new ZAWA();
        @Config.LangKey("config.category.proxies.mocreatures")
        @Config.Comment("Options related specifically to Mo Creatures")
        @Config.Name("Mo Creatures Settings")
        public MoCreatures MoCreatures = new MoCreatures();
        @Config.LangKey("config.category.proxies.atum2") // TODO: FILL IN
        @Config.Comment("Options related specifically to Atum 2")
        @Config.Name("Atum 2 Settings")
        public Atum2 Atum2 = new Atum2();
        @Config.LangKey("config.category.proxies.iceandfire") // TODO: FILL IN
        @Config.Comment("Options related specifically to Ice & Fire")
        @Config.Name("Ice & Fire Settings")
        public IceAndFire IceAndFire = new IceAndFire();

        public class Enable {
            @Config.RequiresMcRestart
            @Config.LangKey("config.proxies.disable.animania")
            @Config.Comment("Set to false to disable the Animania proxy (even if it would normally load)")
            @Config.Name("Animania")
            public boolean animania = true;

            @Config.RequiresMcRestart
            @Config.LangKey("config.proxies.disable.mocreatures")
            @Config.Comment("Set to false to disable the Mo Creatures proxy (even if it would normally load)")
            @Config.Name("Mo Creatures")
            public boolean mocreatures = true;

            @Config.RequiresMcRestart
            @Config.LangKey("config.proxies.disable.zawa")
            @Config.Comment("Set to false to disable the ZAWA Rebuilt proxy (even if it would normally load)")
            @Config.Name("ZAWA Rebuilt")
            public boolean zawa = true;

            @Config.RequiresMcRestart
            @Config.LangKey("config.proxies.disable.unicorn")
            @Config.Comment("Set to false to disable the Ultimate Unicorn Mod proxy (even if it would normally load)")
            @Config.Name("Ultimate Unicorn Mod")
            public boolean ultimate_unicorn_mod = true;

            @Config.RequiresMcRestart
            @Config.LangKey("config.proxies.disable.atum2") // fill in TODO
            @Config.Comment("Set to false to disable the Atum 2 proxy (even if it would normally load)")
            @Config.Name("Atum 2")
            public boolean atum2 = true;

            @Config.RequiresMcRestart
            @Config.LangKey("config.proxies.disable.iceandfire") // TODO: FILL IN
            @Config.Comment("Set to false to disable the Ice & Fire proxy (even if it would normally load)")
            @Config.Name("Ice & Fire")
            public boolean iceandfire = true;

            @Config.RequiresMcRestart
            @Config.LangKey("config.proxies.disable.dragonmounts") // TODO: FILL IN
            @Config.Comment("Set to false to disable the Dragon Mounts proxy (even if it would normally load)")
            @Config.Name("Dragon Mounts 2")
            public boolean dragon = true;

            @Config.RequiresMcRestart
            @Config.LangKey("config.proxies.disable.varodd") // TODO: FILL IN
            @Config.Comment("Set to false to disable the Various Oddities proxy (even if it would normally load)")
            @Config.Name("Various Oddities")
            public boolean varodd = true;

            @Config.RequiresMcRestart
            @Config.LangKey("config.proxies.disable.mooland") // TODO: FILL IN
            @Config.Comment("Set to false to disable the Moolands proxy (even if it would normally load)")
            @Config.Name("Moolands")
            public boolean moolands = true;
        }

        public class Animania {
            @Config.LangKey("config.proxies.animania.classes")
            @Config.Comment("Specify list of Animania classes that are considered steeds. Use /dwmh entity while targetting to get the full name")
            @Config.Name("Animania Classes")
            public String[] classes = new String[]{"com.animania.common.entities.horses.EntityStallionDraftHorse", "com.animania.common.entities.horses.EntityMareDraftHorse"};
        }

        public class ZAWA {
            @Config.LangKey("config.proxies.zawa.classes")
            @Config.Comment("Specify list of ZAWA Rebuilt classes that are considered steeds. Use /dwmh entity while targetting to get the full name")
            @Config.Name("ZAWA Classes")
            public String[] classes = new String[]{"org.zawamod.entity.land.EntityAsianElephant", "org.zawamod.entity.land.EntityGaur", "org.zawamod.entity.land.EntityGrevysZebra", "org.zawamod.entity.land.EntityOkapi", "org.zawamod.entity.land.EntityReticulatedGiraffe"};
        }

        public class MoCreatures {
            @Config.LangKey("config.proxies.mocreatures.classes")
            @Config.Comment("Specify list of entity translation keys which should be modified to insert spaces (where relevant)")
            @Config.Name("Mo Creatures Entities") // TODO: Update lang
            public String[] entities = new String[]{"entity.mocreatures:blackbear.name", "entity.mocreatures:grizzlybear.name", "entity.mocreatures:komododragon.name", "entity.mocreatures:petscorpion.name", "entity.mocreatures:wildhorse.name", "entity.mocreatures:wildpolarbear.name"};
        }

        public class Atum2 {
            @Config.LangKey("config.proxies.atum2.classes") // TODO: FILL IN
            @Config.Comment("Specify list of Atum 2 classes that are considered steeds. Use /dwmh entity while targetting to get the full name")
            @Config.Name("Atum 2 Classes")
            public String[] classes = new String[]{"com.teammetallurgy.atum.entity.animal.EntityCamel", "com.teammetallurgy.atum.entity.animal.EntityDesertWolf"};
        }

        public class IceAndFire {
            @Config.LangKey("config.proxies.iceandfire.classes")
            @Config.Comment("Specify list of Ice and Fire classes that are considered steeds. Use /dwmh entity while targetting to get the full name")
            @Config.Name("Ice & Fire Classes")
            public String[] classes = new String[]{"com.github.alexthe666.iceandfire.entity.EntityIceDragon", "com.github.alexthe666.iceandfire.entity.EntityFireDragon", "com.github.alexthe666.iceandfire.entity.EntityHippocampus", "com.github.alexthe666.iceandfire.entity.EntityHippogryph"};

            @Config.LangKey("config.proxies.iceandfire.excluded")
            @Config.Comment("Specify list of Ice and Fire classes that are excuded from Carrot taming, ageing and breeding. Use /dwmh entity while targetting to get the full name")
            @Config.Name("Ice & Fire Exclusions")
            public String[] exclusions = new String[]{"com.github.alexthe666.iceandfire.entity.EntityIceDragon", "com.github.alexthe666.iceandfire.entity.EntityFireDragon"};
        }
    }
}
