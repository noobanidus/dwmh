package com.noobanidus.dwmh.items;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.capability.CapabilityOcarinaHandler;
import com.noobanidus.dwmh.capability.CapabilityOcarina;
import com.noobanidus.dwmh.capability.CapabilityOwnHandler;
import com.noobanidus.dwmh.capability.CapabilityOwner;
import com.noobanidus.dwmh.client.keybinds.OcarinaKeybind;
import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.network.PacketHandler;
import com.noobanidus.dwmh.network.PacketOcarina;
import com.noobanidus.dwmh.util.MessageHandler;
import com.noobanidus.dwmh.util.OcarinaSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiFunction;

public class ItemOcarina extends ItemDWMHRepairable {
    private Map<UUID, PlayerMode> modeMap = new HashMap<>();
    private List<TextComponentTranslation> directions = new ArrayList<>();

    private static boolean unuseableItem(ItemStack item) {
        if (DWMHConfig.Ocarina.functionality.getMaxUses() == 0) return false;

        return !ItemDWMHRepairable.useableItem(item);
    }

    public static void onInteractOcarina(PlayerInteractEvent.EntityInteract event) {
        Entity entity = event.getTarget();
        EntityPlayer player = event.getEntityPlayer();
        ItemStack item = event.getItemStack();

        if (item.isEmpty() || !(item.getItem() instanceof ItemOcarina)) {
            return;
        }

        if (!entity.hasCapability(CapabilityOcarinaHandler.INSTANCE, null)) return;

        if (!DWMH.steedProxy.pseudoTaming(entity)) return;

        if (!player.isSneaking()) return;

        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);

        if (event.getWorld().isRemote) return;

        CapabilityOwner cap = entity.getCapability(CapabilityOwnHandler.INSTANCE, null);

        if (cap == null) return;

        boolean tamed = cap.hasOwner();
        boolean tamedBy = tamed && cap.getOwner() != null && cap.getOwner().equals(player.getUniqueID());

        ITextComponent temp;
        if (!tamed) {
            cap.setOwner(player.getUniqueID());
            temp = new TextComponentTranslation("dwmh.strings.animania_named");
            temp.getStyle().setColor(TextFormatting.GOLD);
            player.sendMessage(temp);
        } else if (tamedBy) {
            cap.setOwner(null);
            temp = new TextComponentTranslation("dwmh.strings.animania_untamed");
            temp.getStyle().setColor(TextFormatting.GOLD);
            player.sendMessage(temp);
        } else {
            temp = new TextComponentTranslation("dwmh.strings.animania_claimed");
            temp.getStyle().setColor(TextFormatting.RED);
            player.sendMessage(temp);
        }
    }

    public PlayerMode getPlayerMode (EntityPlayer player) {
        UUID uuid = player.getUniqueID();
        if (modeMap.containsKey(uuid)) {
            return modeMap.get(uuid);
        }
        PlayerMode mode = new PlayerMode();

        CapabilityOcarina cap = player.getCapability(CapabilityOcarinaHandler.INSTANCE, null);
        if (cap != null) {
            Mode main = cap.getMain();
            Mode sneak = cap.getSneak();
            if (main != null) {
                mode.setMain(main);
            }
            if (sneak != null) {
                mode.setSneak(sneak);
            }
        }

        modeMap.put(uuid, mode);
        return mode;
    }

    public void setMode (EntityPlayer player, Mode main, Mode sneak) {
        CapabilityOcarina cap = player.getCapability(CapabilityOcarinaHandler.INSTANCE, null);
        if (cap != null) {
            cap.setMain(main);
            cap.setSneak(sneak);
        }
        PlayerMode mode = getPlayerMode(player);
        mode.setModes(main, sneak);
    }

    public void cycleMode (EntityPlayer player, boolean isSneaking) {
        PlayerMode mode = getPlayerMode(player);
        if (isSneaking) {
            mode.cycleSneak();
        } else {
            mode.cycleMain();
        }
    }

    public void init() {
        setMaxStackSize(1);
        setCreativeTab(DWMH.TAB);
        // These are the only "whistles" that hang around because I don't
        // want to break any backwards compatibility to people upgrading.
        setRegistryName("dwmh:whistle");
        setTranslationKey("dwmh.whistle");
        registerPredicate("whistle_damage");
        updateConfig();
        setInternalDefault(DWMHConfig.Ocarina.functionality.repairItemDefault);

        for (int i = 0; i < 8; i++) {
            directions.add(new TextComponentTranslation(String.format("dwmh.strings.dir.%d", i)));
        }
    }

    @Override
    public void updateConfig() {
        if (DWMHConfig.Ocarina.functionality.getMaxUses() != 0) {
            setMaxDamage(DWMHConfig.Ocarina.functionality.getMaxUses());
        }

        setInternalRepair(DWMH.clientStorage.getString("Ocarina", "repairItem"));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }

    private boolean isValidHorse(Entity entity, EntityPlayer player) {
        return isValidHorse(entity, player, false);
    }

    private boolean isValidHorse(Entity entity, EntityPlayer player, boolean listing) {
        if (entity.isDead) return false;

        if (DWMH.steedProxy.isListable(entity, player)) {
            if (listing) {
                return true;
            }
        } else {
            return false;
        }

        return DWMH.steedProxy.isTeleportable(entity, player);
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    public ItemStack getCostItem() {
        return parseItem(DWMH.clientStorage.getString("Ocarina", "summonItem"), DWMHConfig.Ocarina.functionality.summonItemStack);
    }

    public void checkCostItem() {
        parseItem(DWMH.clientStorage.getString("Ocarina", "summonItem"), DWMHConfig.Ocarina.functionality.summonItemStack, true);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        if (world.isRemote) {
            PacketOcarina.Trigger packet = new PacketOcarina.Trigger(player, hand);
            PacketHandler.sendToServer(packet);
        }

        ItemStack stack = player.getHeldItem(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public void trigger (EntityPlayer player, EnumHand hand, boolean isSneaking) {
        assert !player.world.isRemote;

        switch (getPlayerMode(player).getMode(isSneaking)) {
            case LIST:
                doListing(player.world, player, hand, false);
                break;
            case LIST_PACK:
                doListing(player.world, player, hand, true);
                break;
            case SUMMON:
                doSummoning(player.world, player, hand, false);
                break;
            case SUMMON_PACK:
                doSummoning(player.world, player, hand, true);
                break;
            case EJECT:
                doEjecting(player.world, player, hand);
                break;
        }
    }

    public boolean doListing(World world, EntityPlayer player, @Nonnull EnumHand hand, boolean packAnimals) {
        BlockPos pos = player.getPosition();
        boolean didStuff = false;

        ITextComponent temp;

        /** Listing mocClasses. **/

        List<Entity> nearbyHorses = world.getEntities(Entity.class, (entity) -> isValidHorse(entity, player, true));
        for (Entity horse : nearbyHorses) {
            didStuff = true;

            if (packAnimals && !DWMH.steedProxy.isPackAnimal(horse, player)) continue;
            if (!packAnimals && DWMH.steedProxy.isPackAnimal(horse, player)) continue;

            BlockPos hpos = horse.getPosition();

            ITextComponent entityName = DWMH.steedProxy.getEntityTypeName(horse, player);
            if (entityName == null) {
                // This is triggered when an entity that SHOULD be skipped is not skipped
                DWMH.LOG.error(String.format("Invalid response from proxy for entity %s", horse.getClass().getName()));
                entityName = new TextComponentString("INVALID: " + horse.getClass().getName());
            }

            float dist = player.getDistance(horse);

            ITextComponent result1 = new TextComponentTranslation("dwmh.strings.is_at",
                    entityName,
                    (DWMH.steedProxy.hasCustomName(horse)) ? new TextComponentTranslation("dwmh.strings.named", DWMH.steedProxy.getCustomNameTag(horse)) : "",
                    DWMH.steedProxy.getResponseKey(horse, player),
                    hpos.getX(),
                    hpos.getY(),
                    hpos.getZ());


            double angle = Math.atan2(hpos.getZ() - pos.getZ(), hpos.getX() - pos.getX());
            int index = (int) Math.round(angle / Math.PI * 4 + 10) % 8;
            ITextComponent result2 = new TextComponentTranslation("dwmh.strings.blocks", (int) dist, directions.get(index));
            MessageHandler.sendListingMessage(player, result1, result2);
        }
        if (!didStuff) {
            temp = new TextComponentTranslation("dwmh.strings.no_eligible_to_list");
            temp.getStyle().setColor(TextFormatting.RED);
            player.sendMessage(temp);
        }
        player.swingArm(hand);

        return didStuff;
    }

    public boolean doSummoning(World world, EntityPlayer player, @Nonnull EnumHand hand, boolean packAnimals) {
        ItemStack stack = player.getHeldItem(hand);
        InventoryPlayer inv = player.inventory;

        BlockPos pos = player.getPosition();
        boolean didStuff = false;

        ITextComponent temp;

        int totalConsumed = 0;

        ItemStack itemCost = getCostItem();

        int amountPer = DWMHConfig.Ocarina.functionality.getSummonCost();

        if (player.capabilities.isCreativeMode) {
            amountPer = 0;
        }

        int amountIn = inv.mainInventory.stream().filter(i -> i.getItem() == itemCost.getItem() && i.getMetadata() == itemCost.getMetadata()).mapToInt(ItemStack::getCount).sum();

        // Early breakpoints: if there is an item cost but we don't have enough
        if (amountPer != 0) {
            if (amountIn < amountPer) {
                temp = new TextComponentTranslation("dwmh.strings.summon_item_missing", itemCost.getDisplayName(), amountPer);
                temp.getStyle().setColor(TextFormatting.DARK_RED);
                MessageHandler.sendOcarinaTune(stack, player, OcarinaSound.MINOR);
                player.sendMessage(temp);
                return false;
            }
        }

        // Early breakpoint: if the Ocarina is broken
        BiFunction<String, Boolean, Boolean> durabilityCheck = (key, playSound) -> {
            if (unuseableItem(stack)) {
                ITextComponent temp2 = new TextComponentTranslation(key); // );
                temp2.getStyle().setColor(TextFormatting.BLUE);
                player.sendMessage(temp2);
                MessageHandler.sendOcarinaTune(stack, player, OcarinaSound.BROKEN);
                return false;
            }

            return true;
        };

        if (!durabilityCheck.apply("dwmh.strings.broken_whistle", true)) return false;

        List<Entity> nearbyHorses = world.getEntities(Entity.class, (entity) -> isValidHorse(entity, player));
        for (Entity entity : nearbyHorses) {
            EntityLiving horse = (EntityLiving) entity;

            if (packAnimals && !DWMH.steedProxy.isPackAnimal(horse, player)) continue;
            if (!packAnimals && DWMH.steedProxy.isPackAnimal(horse, player)) continue;

            double max = DWMHConfig.Ocarina.getMaxDistance();
            if (horse.getDistanceSq(player) < (max * max) || max == 0) {
                if (amountPer != 0) {
                    // Early breakpoint: if the number consumed thus far taken from the initial total is less than the amount, break
                    if (amountIn - totalConsumed < amountPer) {
                        if (totalConsumed == 0) {
                            temp = new TextComponentTranslation("dwmh.strings.summon_item_missing", itemCost.getDisplayName(), amountPer);
                        } else {
                            temp = new TextComponentTranslation("dwmh.strings.summon_item_missing_middle", itemCost.getDisplayName(), totalConsumed);
                        }
                        temp.getStyle().setColor(TextFormatting.DARK_RED);
                        player.sendMessage(temp);
                        MessageHandler.sendOcarinaTune(stack, player, OcarinaSound.MINOR);
                        return false;
                    } else {
                        int cleared = inv.clearMatchingItems(itemCost.getItem(), itemCost.getMetadata(), amountPer, null);
                        if (cleared < amountPer) {
                            DWMH.LOG.error(String.format("Error: inventory should contain %d of %s, with %d to be removed, but only %d were removed.", (amountIn - totalConsumed), itemCost.getDisplayName(), amountIn, cleared));
                        }
                        totalConsumed += cleared;
                    }
                }
                horse.moveToBlockPosAndAngles(pos, horse.rotationYaw, horse.rotationPitch);
                didStuff = true;
                if (DWMHConfig.Ocarina.functionality.getMaxUses() != 0) {
                    damageItem(stack, player);
                    if (!durabilityCheck.apply("dwmh.strings.break_whistle", false)) return false;
                }
                // TODO
                ITextComponent result;
                if (DWMH.steedProxy.hasCustomName(horse)) {
                    result = new TextComponentTranslation("dwmh.strings.teleport_with_name", DWMH.steedProxy.getCustomNameTag(horse));
                    result.getStyle().setColor(TextFormatting.GOLD);
                } else {
                    result = new TextComponentTranslation("dwmh.strings.teleport");
                    result.getStyle().setColor(TextFormatting.GOLD);
                }
                MessageHandler.sendSummonMessage(player, result);

                if (DWMHConfig.Ocarina.functionality.getCooldown() > 0) {
                    player.getCooldownTracker().setCooldown(this, DWMHConfig.Ocarina.functionality.getCooldown());
                }
                player.swingArm(hand);
                horse.getNavigator().clearPath();
                if (DWMHConfig.Ocarina.home && horse instanceof EntityCreature) {
                    ((EntityCreature) horse).setHomePosAndDistance(pos, 5);
                }
            }
        }
        if (didStuff) {
            MessageHandler.sendOcarinaTune(stack, player, OcarinaSound.NORMAL);
        }
        if (didStuff && totalConsumed != 0) {
            temp = new TextComponentTranslation("dwmh.strings.summon_item_success", itemCost.getDisplayName(), totalConsumed);
            temp.getStyle().setColor(TextFormatting.RED);
            player.sendMessage(temp);
        }
        if (!didStuff) {
            if (player.isRiding()) {
                temp = new TextComponentTranslation("dwmh.strings.no_eligible_to_teleport_riding");
                temp.getStyle().setColor(TextFormatting.RED);
            } else {
                temp = new TextComponentTranslation("dwmh.strings.no_eligible_to_teleport");
                temp.getStyle().setColor(TextFormatting.RED);
            }
            player.sendMessage(temp);

            MessageHandler.sendOcarinaTune(stack, player, OcarinaSound.MINOR);
        } else {
            MessageHandler.sendGenericMessage(player, null, MessageHandler.Generic.SUMMONED, null, TextFormatting.GOLD);
        }

        return didStuff;
    }

    public boolean doEjecting (World world, EntityPlayer player, EnumHand hand) {
        boolean didStuff = false;
        int dismountCount = 0;

        List<Entity> nearbyHorses = world.getEntities(Entity.class, (entity) -> isValidHorse(entity, player, true));
        for (Entity horse : nearbyHorses) {
            didStuff = true;

            if (horse.isBeingRidden() && !horse.isRidingSameEntity(player)) {
                for (Entity entity : horse.getPassengers()) {
                    entity.dismountRidingEntity();
                    dismountCount++;
                }
            }
        }

        ITextComponent result;

        if (!didStuff || dismountCount == 0) {
            result = new TextComponentTranslation("dwmh.strings.dismount.failure");
            result.setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else {
            result = new TextComponentTranslation("dwmh.strings.dismount.success", dismountCount);
            result.setStyle(new Style().setColor(TextFormatting.GOLD));
        }

        player.sendMessage(result);
        player.swingArm(hand);

        return didStuff;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack ist) {
        if (!(entityLiving instanceof EntityPlayer)) return false;

        if (ist.getItem() != this) return false;

        EntityPlayer player = (EntityPlayer) entityLiving;

        if (!player.isSneaking()) return false;

        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack par1ItemStack, World world, List<String> stacks, ITooltipFlag flags) {
        if (GuiScreen.isShiftKeyDown()) {
            Minecraft mc = Minecraft.getMinecraft();

            if (unuseableItem(par1ItemStack) && DWMHConfig.Ocarina.functionality.getMaxUses() != 0) {
                stacks.add(TextFormatting.DARK_RED + I18n.format("dwmh.strings.carrot.tooltip.broken"));
            }

            PlayerMode mode = getPlayerMode(mc.player);

            stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format(mode.getMain().getLanguageKey()));
            stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.shift_right_click") + " " + TextFormatting.WHITE + I18n.format(mode.getSneak().getLanguageKey()));

            if (DWMH.steedProxy.pseudoTaming()) {
                stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.shift_right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.animania_naming"));
            }

            if (par1ItemStack.getItemDamage() != 0 || DWMHConfig.Ocarina.functionality.getMaxUses() != 0) {
                stacks.add(TextFormatting.AQUA + I18n.format("dwmh.strings.repair_carrot", getRepairItem().getDisplayName()));
            }

            if (DWMHConfig.Ocarina.functionality.getSummonCost() != 0) {
                stacks.add(TextFormatting.RED + I18n.format("dwmh.strings.summon_tooltip", getCostItem().getDisplayName(), DWMHConfig.Ocarina.functionality.getSummonCost()));
            }

            stacks.add(TextFormatting.RED + I18n.format("dwmh.strings.whistle.tooltip.mode", OcarinaKeybind.ocarinaKey.getDisplayName(), OcarinaKeybind.ocarinaKey.getDisplayName()));
        } else {
            stacks.add(TextFormatting.DARK_GRAY + I18n.format("dwmh.strings.hold_shift"));
        }
    }

    public enum Mode {
        LIST("dwmh.strings.whistle.tooltip.list_horses"),
        LIST_PACK("dwmh.strings.whistle.tooltip.list_horses_pack"),
        SUMMON("dwmh.strings.whistle.tooltip.teleport_horses"),
        SUMMON_PACK("dwmh.strings.whistle.tooltip.teleport_horses_pack"),
        EJECT("dwmh.strings.whistle.tooltip.eject");

        private String languageKey;

        Mode (String languageKey) {
            this.languageKey = languageKey;
        }

        public String getLanguageKey() {
            return languageKey;
        }

        public static Mode fromOrdinal (int index) {
            int i = 0;
            for (Mode mode : Mode.values()) {
                if (index == i++) return mode;
            }

            return null;
        }

        public Mode next () {
            int ord = this.ordinal();
            if (ord == 4) ord = 0;
            else ord++;
            return fromOrdinal(ord);
        }
    }

    public static class PlayerMode {
        private Mode main = Mode.SUMMON;
        private Mode sneak = Mode.LIST;

        public PlayerMode () {
        }

        public Mode getMain() {
            return main;
        }

        public Mode getSneak() {
            return sneak;
        }

        public Mode getMode (boolean isSneaking) {
            if (isSneaking) {
                return getSneak();
            }

            return getMain();
        }

        public void cycleMain () {
            this.main = main.next();
        }

        public void cycleSneak () {
            this.sneak = sneak.next();
        }

        public void setMain(Mode main) {
            this.main = main;
        }

        public void setSneak(Mode sneak) {
            this.sneak = sneak;
        }

        public void setModes (Mode main, Mode sneak) {
            this.main = main;
            this.sneak = sneak;
        }
    }
}
