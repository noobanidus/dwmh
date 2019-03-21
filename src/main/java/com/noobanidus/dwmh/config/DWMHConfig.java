package com.noobanidus.dwmh.config;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.minecraftforge.common.config.Config.*;

@SuppressWarnings("WeakerAccess")
@Config(modid = DWMH.MODID)
@Mod.EventBusSubscriber(modid = DWMH.MODID)
public class DWMHConfig {
    @Comment("Settings related to the Ocarina and its use")
    @Name("Ocarina Settings")
    @LangKey("config.category.ocarina")
    public static Ocarina Ocarina = new Ocarina();
    @Comment("Settings related to the Enchanted Carrot and its use")
    @Name("Enchanted Carrot Settings")
    @LangKey("config.category.carrot")
    public static Carrot EnchantedCarrot = new Carrot();
    @Comment("Options relating to the individual proxies.")
    @Name("Proxy Settings")
    @LangKey("config.category.proxy")
    public static Proxies proxies = new Proxies();
    @Comment("Specify a blacklist of mocClasses that should always be ignored, even if generally loaded by their proxy.")
    @Name("Entity Blacklist")
    @LangKey("config.base.blacklist")
    public static String[] blacklist = new String[]{};
    @RequiresMcRestart
    @Comment("Set to false to disable the craftable saddle recipes")
    @Name("Enable Saddle Recipe")
    @LangKey("config.base.saddle")
    public static boolean saddleRecipe = true;
    @Comment("Client-specific options about messages, visuals, etc")
    @Name("Client Settings")
    @LangKey("config.category.client") // TODO
    public static Client client = new Client();

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(DWMH.MODID)) {
            updateConfig();
        }
    }

    public static void updateConfig() {
        ConfigManager.sync(DWMH.MODID, Config.Type.INSTANCE);
        DWMH.resolveClasses();
        Registrar.ocarina.updateConfig();
        Registrar.carrot.updateConfig();
        Registrar.ocarina.checkRepairItem();
        Registrar.ocarina.checkCostItem();
        Registrar.carrot.checkRepairItem();

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null || !server.isSinglePlayer()) return;

        if (Ocarina.functionality.getMaxUses() != Ocarina.functionality.maxUses)
            DWMH.LOG.error(String.format("Invalid configuration value for Ocarina:maxUses: |%d|. Using default 0 instead.", Ocarina.functionality.maxUses));
        if (Ocarina.functionality.getCooldown() != Ocarina.functionality.cooldown)
            DWMH.LOG.error(String.format("Invalid configuration value for Ocarina:cooldown: |%d|. Using default 0 instead.", Ocarina.functionality.cooldown));
        if (Ocarina.functionality.getSummonCost() != Ocarina.functionality.summonCost)
            DWMH.LOG.error(String.format("Invalid configuration value for Ocarina:summonCost: |%d|. Using default 0 instead.", Ocarina.functionality.summonCost));
        if (EnchantedCarrot.durability.getMaxUses() != EnchantedCarrot.durability.maxUses)
            DWMH.LOG.error(String.format("Invalid configuration value for EnchantedCarrot:maxUses: |%d|. Using default 1 instead.", EnchantedCarrot.durability.maxUses));
    }

    // Don't expose these
    public static class Client {
        @LangKey("config.category.client.ocarina")
        @Comment("Client options related to the Ocarina")
        @Name("Ocarina Settings")
        public ClientOcarina clientOcarina = new ClientOcarina();

        @LangKey("config.category.client.carrot")
        @Comment("Client options related to the Enchanted Carrot")
        @Name("Carrot Settings")
        public ClientCarrot clientCarrot = new ClientCarrot();

        public class ClientCarrot {
            @LangKey("config.carrot.messages.taming")
            @Comment("Set to false to prevent messages while taming")
            @Name("Enable Eligible Entity Taming Message")
            public boolean taming = true;

            @LangKey("config.carrot.messages.healing")
            @Comment("Set to false to prevent messages while healing")
            @Name("Enable Eligible Entity Healing Message")
            public boolean healing = true;

            @LangKey("config.carrot.messages.ageing")
            @Comment("Set to false to prevent messages while ageing")
            @Name("Enable Eligible Entity Ageing Message")
            public boolean aging = true;

            @LangKey("config.carrot.messages.breeding")
            @Comment("Set to false to prevent messages while breeding")
            @Name("Enable Eligible Entity Breeding Message")
            public boolean breeding = true;

            @LangKey("config.carrot.glint")
            @Comment("Set to true to enable the enchantment glint. Useful if you are using a texture pack that overrides the Carrot animation. Client-side only.")
            @Name("Enable Enchantment Glint")
            public boolean glint = false;
        }

        public class ClientOcarina {
            @LangKey("config.ocarina.swap_sneak")
            @Comment("Set to true to swap the effect of sneaking. Thus, right-clicking will list mocClasses, while sneak-right-clicking will summon them.")
            @Name("Swap Sneak Functionality")
            public boolean swap = false;

            @LangKey("config.ocarina.responses.disable_summon_message")
            @Comment("Set to true to disable messages when teleporting a horse to you.")
            @Name("Disable Summoned Messages")
            public boolean quiet = false;

            @LangKey("config.ocarina.responses.combine_summon_message")
            @Comment("Set to true to compact multiple horse-summoned messages into one.")
            @Name("Combine Summoned Messages")
            public boolean simple = false;

            @LangKey("config.ocarina.responses.enable_distance")
            @Comment("Set to false to disable showing the distance and direction a horse is away from you")
            @Name("Enable Distance/Direction")
            public boolean distance = true;

            @LangKey("config.ocarina.responses.sounds")
            @Comment("Set to false to disable sounds being played when the Ocarina is use. These sounds are played on the PLAYERS channel.")
            @Name("Enable Ocarina Tunes")
            public boolean sounds = true;

            @LangKey("config.ocarina.responses.disable_llamas")
            @Comment("Set to true to prevent llamas from being listed with the Ocarina.")
            @Name("Disable Listing Llama")
            public boolean noLlamas = false;
        }
    }

    public static class Ocarina {
        @LangKey("config.ocarina.max_distance")
        @Comment("Specify the maximum distance steeds can be summoned from (set to 0 for infinite). Excludes mocClasses in unloaded chunks or different dimensions")
        @RangeDouble(min = 0d)
        @Name("Maximum Summon Distance")
        // TODO Relevant to client
        public double maxDistance = 200d;
        @LangKey("config.ocarina.other_riders")
        @Comment("Set to true to enable summoning of horses even if they are being ridden by someone else.")
        @Name("Summon With Other Riders")
        public boolean otherRiders = false;
        @LangKey("config.ocarina.set_home")
        @Comment("Set to false to prevent setting a home when summoning steeds. This should prevent them wandering too far from where you summoned them to, but may cause interactions with other mods.")
        @Name("Set Home Location When Summoned")
        public boolean home = true;
        @LangKey("config.ocarina.remove_home")
        @Comment("Set to true to skip removing a home point when you dismount a horse. This may help with mods that set a home upon dismounting, as well. Otherwise, setting this to false will make horses believe their home is where you last summoned them to was.")
        @Name("Skip Clearing Home on Dismount")
        public boolean skipDismount = false;
        @LangKey("config.ocarina.responses.sound_delay")
        @Comment("The delay in seconds between uses of the Ocarina causing a sound event. The Ocarina will still trigger, but silently, during this delay.")
        @Name("Ocarina SoundHandler Delay")
        public int soundDelay = 5;
        @LangKey("config.category.ocarina.functionality")
        @Comment("Options related to the functionality of the Ocarina")
        @Name("Functionality")
        public Functionality functionality = new Functionality();

        public double getMaxDistance() {
            return Math.max(DWMH.clientStorage.getDouble("Ocarina", "maxDistance"), 0.0d);
        }

        public class Functionality {
            @LangKey("config.ocarina.functionality.cooldown")
            @Comment("Specify a cooldown for each usage of the Ocarina, or specify 0 to disable")
            @Name("Ocarina Cooldown Duration")
            @RangeInt(min = 0)
            // TODO Relevant to client
            public int cooldown = 0;
            @LangKey("config.ocarina.functionality.max_durability")
            @Comment("Specify the maximum durability of the Ocarina. One horse summoned costs one durability. Set to 0 to disable durability.")
            @Name("Maximum Ocarina Durability")
            @RangeInt(min = 0)
            // TODO Relevant to client
            public int maxUses = 0;
            @LangKey("config.ocarina.functionality.repair_item")
            @Comment("Specify the item that can be used to repair the Ocarina in an anvil. Items with NBT are not supported. Format mod:item:metadata (use \"minecraft\" for vanilla items), use 0 for no meteadata.")
            @Name("Ocarina Repair Item")
            // TODO Relevant to client
            public String repairItem = "minecraft:golden_carrot:0";
            @Ignore
            public ItemStack repairItemDefault = new ItemStack(Items.GOLDEN_CARROT);
            @LangKey("config.ocarina.functionality.summon_item")
            @Comment("Specify the item to consume from the player's inventory before summoning a horse. Format: mod:item:metadata (use \"minecraft\" for vanilla items), use 0 for no metadata. Items with NBT are not supported.")
            @Name("Summon Item")
            // TODO Relevant to client
            public String summonItem = "minecraft:carrot:0";
            @Ignore
            public ItemStack summonItemStack = new ItemStack(Items.CARROT);
            @LangKey("config.ocarina.functionality.summon_cost")
            @Comment("Specify the quantity of the item to consume from the player's inventory before summoning a horse. Set to 0 to consume nothing.")
            @Name("Summon Cost")
            @RangeInt(min = 0)
            // TODO Relevant to client
            public int summonCost = 0;

            public int getCooldown() {
                return Math.max(DWMH.clientStorage.getInteger("Ocarina", "cooldown"), 0);
            }

            public int getMaxUses() {
                return Math.max(DWMH.clientStorage.getInteger("Ocarina", "maxUses"), 0);
            }

            public int getSummonCost() {
                return Math.max(DWMH.clientStorage.getInteger("Ocarina", "summonCost"), 0);
            }
        }
    }

    public static class Carrot {
        @RequiresMcRestart
        @LangKey("config.carrot.recipe")
        @Comment("Set to false to disable the Enchanted Carrot recipe from being registered.")
        @Name("Enable Carrot Recipe")
        public boolean enabled = true;
        @LangKey("config.category.carrot.durability")
        @Comment("Settings related to the durability of the Enchanted Carrot.")
        @Name("Durability settings")
        public Durability durability = new Durability();
        @LangKey("config.category.carrot.effects")
        @Comment("Allows fine-tuning of the individual effects of the Enchanted Carrot")
        @Name("Effects")
        public CarrotEffects effects = new CarrotEffects();

        public static class CarrotEffects {
            @LangKey("config.carrot.effects.taming")
            @Comment("Set to false to prevent the automatic taming of rideable mocClasses")
            @Name("Enable Eligible Entity Taming")
            // TODO Relevant to client
            public boolean taming = true;

            @LangKey("config.carrot.effects.healing")
            @Comment("Set to false to prevent the healing to full of injured rideable mocClasses")
            @Name("Enable Eligible Entity Healing")
            // TODO Relevant to client
            public boolean healing = true;

            @LangKey("config.carrot.effects.ageing")
            @Comment("Set to false to prevent the ageing to adulthood of rideable child mocClasses")
            @Name("Enable Eligible Entity Ageing")
            // TODO Relevant to client
            public boolean aging = true;

            @LangKey("config.carrot.effects.breeding")
            @Comment("Set to false to prevent putting tamed, adult rideable mocClasses into \"breeding\" mode")
            @Name("Enable Eligible Entity Breeding")
            // TODO Relevant to client
            public boolean breeding = true;
        }

        public class Durability {
            @RangeInt(min = 1)
            @LangKey("config.carrot.durability.maximum_durability")
            @Comment("Maximum number of uses before the enchanted Enchanted Carrot becomes unusable")
            @Name("Maximum Carrot Durability")
            // TODO Relevant to client
            public int maxUses = 30;

            @LangKey("config.carrot.durability.repair_item")
            @Comment("Specify the item that can be used to repair the Enchanted Carrot in an anvil. Items with NBT are not supported. Format: mod:item:metadata. Use \"minecraft\" for vanilla items, and 0 if no metadata is specified.")
            @Name("Carrot Repair Item")
            // TODO Relevant to client
            public String repairItem = "minecraft:gold_block:0";
            @Ignore
            public ItemStack repairItemDefault = new ItemStack(Blocks.GOLD_BLOCK);

            @LangKey("config.carrot.durability.breaks")
            @Comment("Set to true to have the carrot break when it reaches 0 durability instead of becoming unusable")
            @Name("Carrot Is Breakable")
            // TODO Relevant to client
            public boolean breakableCarrot = false;

            public int getMaxUses() {
                return Math.max(DWMH.clientStorage.getInteger("Carrot", "maxUses"), 1);
            }
        }
    }

    public static class Proxies {

        @LangKey("config.category.proxies.disable")
        @Comment("Overrides to specifically disable certain proxies")
        @Name("Enable/Disable Proxies")
        public Enable enable = new Enable();
        @LangKey("config.category.proxies.animania")
        @Comment("Options related specifically to the Animania ")
        @Name("Animania Settings")
        public Animania Animania = new Animania();
        @LangKey("config.category.proxies.zawa")
        @Comment("Options related specifically to ZAWA")
        @Name("ZAWA Settings")
        public ZAWA ZAWA = new ZAWA();
        @LangKey("config.category.proxies.mocreatures")
        @Comment("Options related specifically to Mo Creatures")
        @Name("Mo Creatures Settings")
        public MoCreatures MoCreatures = new MoCreatures();
        @LangKey("config.category.proxies.atum2") // TODO: FILL IN
        @Comment("Options related specifically to Atum 2")
        @Name("Atum 2 Settings")
        public Atum2 Atum2 = new Atum2();
        @LangKey("config.category.proxies.iceandfire") // TODO: FILL IN
        @Comment("Options related specifically to Ice & Fire")
        @Name("Ice & Fire Settings")
        public IceAndFire IceAndFire = new IceAndFire();

        public class Enable {
            @RequiresMcRestart
            @LangKey("config.proxies.disable.animania")
            @Comment("Set to false to disable the Animania proxy (even if it would normally load)")
            @Name("Animania")
            public boolean animania = true;

            @RequiresMcRestart
            @LangKey("config.proxies.disable.mocreatures")
            @Comment("Set to false to disable the Mo Creatures proxy (even if it would normally load)")
            @Name("Mo Creatures")
            public boolean mocreatures = true;

            @RequiresMcRestart
            @LangKey("config.proxies.disable.zawa")
            @Comment("Set to false to disable the ZAWA Rebuilt proxy (even if it would normally load)")
            @Name("ZAWA Rebuilt")
            public boolean zawa = true;

            @RequiresMcRestart
            @LangKey("config.proxies.disable.ultimate_unicorn_mod")
            @Comment("Set to false to disable the Ultimate Unicorn Mod proxy (even if it would normally load)")
            @Name("Ultimate Unicorn Mod")
            public boolean ultimate_unicorn_mod = true;

            @RequiresMcRestart
            @LangKey("config.proxies.disable.atum") // fill in TODO
            @Comment("Set to false to disable the Atum proxy (even if it would normally load)")
            @Name("Atum")
            public boolean atum = true;

            @RequiresMcRestart
            @LangKey("config.proxies.disable.iceandfire") // TODO: FILL IN
            @Comment("Set to false to disable the Ice & Fire proxy (even if it would normally load)")
            @Name("Ice & Fire")
            public boolean iceandfire = true;

            @RequiresMcRestart
            @LangKey("config.proxies.disable.dragonmounts") // TODO: FILL IN
            @Comment("Set to false to disable the Dragon Mounts proxy (even if it would normally load)")
            @Name("Dragon Mounts 2")
            public boolean dragonmounts = true;

            @RequiresMcRestart
            @LangKey("config.proxies.disable.varodd") // TODO: FILL IN
            @Comment("Set to false to disable the Various Oddities proxy (even if it would normally load)")
            @Name("Various Oddities")
            public boolean varodd = true;

            @RequiresMcRestart
            @LangKey("config.proxies.disable.moolands") // TODO: FILL IN
            @Comment("Set to false to disable the Moolands proxy (even if it would normally load)")
            @Name("Moolands")
            public boolean moolands = true;
        }

        public class Animania {
            @LangKey("config.proxies.animania.classes")
            @Comment("Specify list of Animania classes that are considered steeds. Use /dwmh entity while targetting to get the full name")
            @Name("Animania Classes")
            public String[] animaniaClasses = new String[]{"com.animania.common.entities.horses.EntityStallionDraftHorse", "com.animania.common.entities.horses.EntityMareDraftHorse"};
        }

        public class ZAWA {
            @LangKey("config.proxies.zawa.classes")
            @Comment("Specify list of ZAWA Rebuilt classes that are considered steeds. Use /dwmh entity while targetting to get the full name")
            @Name("ZAWA Classes")
            public String[] zawaClasses = new String[]{"org.zawamod.entity.land.EntityAsianElephant", "org.zawamod.entity.land.EntityGaur", "org.zawamod.entity.land.EntityGrevysZebra", "org.zawamod.entity.land.EntityOkapi", "org.zawamod.entity.land.EntityReticulatedGiraffe"};
        }

        public class MoCreatures {
            @LangKey("config.proxies.mocreatures.classes")
            @Comment("Specify list of entity translation keys which should be modified to insert spaces (where relevant)")
            @Name("Mo Creatures Entities") // TODO: Update lang
            public String[] mocClasses = new String[]{"entity.mocreatures:blackbear.name", "entity.mocreatures:grizzlybear.name", "entity.mocreatures:komododragon.name", "entity.mocreatures:petscorpion.name", "entity.mocreatures:wildhorse.name", "entity.mocreatures:wildpolarbear.name"};
        }

        public class Atum2 {
            @LangKey("config.proxies.atum2.classes") // TODO: FILL IN
            @Comment("Specify list of Atum 2 classes that are considered steeds. Use /dwmh entity while targetting to get the full name")
            @Name("Atum 2 Classes")
            public String[] atum2Classes = new String[]{"com.teammetallurgy.atum.entity.animal.EntityCamel", "com.teammetallurgy.atum.entity.animal.EntityDesertWolf"};
        }

        public class IceAndFire {
            @LangKey("config.proxies.iceandfire.classes")
            @Comment("Specify list of Ice and Fire classes that are considered steeds. Use /dwmh entity while targetting to get the full name")
            @Name("Ice & Fire Classes")
            public String[] iceandfireClasses = new String[]{"com.github.alexthe666.iceandfire.entity.EntityIceDragon", "com.github.alexthe666.iceandfire.entity.EntityFireDragon", "com.github.alexthe666.iceandfire.entity.EntityHippocampus", "com.github.alexthe666.iceandfire.entity.EntityHippogryph"};

            @LangKey("config.proxies.iceandfire.excluded")
            @Comment("Specify list of Ice and Fire classes that are excuded from Carrot taming, ageing and breeding. Use /dwmh entity while targetting to get the full name")
            @Name("Ice & Fire Exclusions")
            public String[] iceandfireExclusions = new String[]{"com.github.alexthe666.iceandfire.entity.EntityIceDragon", "com.github.alexthe666.iceandfire.entity.EntityFireDragon"};
        }
    }
}
