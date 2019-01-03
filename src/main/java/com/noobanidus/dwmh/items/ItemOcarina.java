package com.noobanidus.dwmh.items;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.config.Sound;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemOcarina extends ItemDWMHRepairable {
    private List<TextComponentTranslation> directions = new ArrayList<>();

    public void init () {
        setMaxStackSize(1);
        setCreativeTab(DWMH.TAB);
        // These are the only "whistles" that hang around because I don't
        // want to break any backwards compatibility to people upgrading.
        setRegistryName("dwmh:whistle");
        setUnlocalizedName("dwmh.whistle");
        registerPredicate("whistle_damage");
        updateConfig();
        setInternalDefault(DWMHConfig.Ocarina.functionality.repairItemDefault);

        for (int i = 0; i < 8; i++) {
            directions.add(new TextComponentTranslation(String.format("dwmh.strings.dir.%d", i)));
        }
    }

    public void updateConfig () {
        if (DWMHConfig.Ocarina.functionality.getMaxUses() != 0) {
            setMaxDamage(DWMHConfig.Ocarina.functionality.getMaxUses());
        }

        setInternalRepair(DWMHConfig.Ocarina.functionality.repairItem);
    }

    private boolean isValidHorse (Entity entity, EntityPlayer player) {
        return isValidHorse(entity, player, false);
    }

    private boolean isValidHorse (Entity entity, EntityPlayer player, boolean listing) {
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

    public static boolean useableItem (ItemStack item) {
        if (DWMHConfig.Ocarina.functionality.getMaxUses() == 0) return true;

        return ItemDWMHRepairable.useableItem(item);
    }

    public ItemStack getCostItem () {
        return parseItem(DWMHConfig.Ocarina.functionality.summonItem, DWMHConfig.Ocarina.functionality.summonItemStack);
    }

    public void checkCostItem () {
        parseItem(DWMHConfig.Ocarina.functionality.summonItem, DWMHConfig.Ocarina.functionality.summonItemStack, true);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick (World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        ActionResult<ItemStack> actionResult = new ActionResult<>(EnumActionResult.SUCCESS, stack);
        InventoryPlayer inv = player.inventory;

        if (!world.isRemote) {
            BlockPos pos = player.getPosition();
            boolean didStuff = false;

            ITextComponent temp;

            if (player.isSneaking() && !DWMHConfig.Ocarina.swap || !player.isSneaking() && DWMHConfig.Ocarina.swap) {
                List<Entity> nearbyHorses = world.getEntities(Entity.class, (entity) -> isValidHorse(entity, player, true));
                for (Entity horse : nearbyHorses) {
                    didStuff = true;

                    BlockPos hpos = horse.getPosition();

                    ITextComponent entityName = DWMH.steedProxy.getEntityTypeName(horse, player);
                    if (entityName == null) {
                        // This is triggered when an entity that SHOULD be skipped is not skipped
                        DWMH.LOG.error(String.format("Invalid response from proxy for entity %s", horse.getClass().getName()));
                        entityName = new TextComponentString("INVALID: " + horse.getClass().getName());
                    }

                    float dist = player.getDistance(horse);

                    ITextComponent result = new TextComponentTranslation("dwmh.strings.is_at", entityName, (DWMH.steedProxy.hasCustomName(horse)) ? new TextComponentTranslation("dwmh.strings.named", DWMH.steedProxy.getCustomNameTag(horse)) : "", DWMH.steedProxy.getResponseKey(horse, player), hpos.getX(), hpos.getY(), hpos.getZ());

                    if (DWMHConfig.Ocarina.responses.distance) {
                        double angle = Math.atan2(hpos.getZ() - pos.getZ(), hpos.getX() - pos.getX());
                        int index = (int) Math.round(angle / Math.PI * 4 + 10) % 8;
                        result.appendSibling(new TextComponentTranslation("dwmh.strings.blocks", (int) dist, directions.get(index)));
                    }

                    player.sendMessage(result);
                }
                if (!didStuff) {
                    temp = new TextComponentTranslation("dwmh.strings.no_eligible_to_list");
                    temp.getStyle().setColor(TextFormatting.RED);
                    player.sendMessage(temp);
                }
                player.swingArm(hand);
            } else {
                int totalConsumed = 0;

                ItemStack itemCost = getCostItem();

                int amountPer = DWMHConfig.Ocarina.functionality.getSummonCost();
                int amountIn = inv.mainInventory.stream().filter(i -> i.getItem() == itemCost.getItem() && i.getMetadata() == itemCost.getMetadata()).mapToInt(ItemStack::getCount).sum();

                // Early breakpoints: if there is an item cost but we don't have enough
                if (amountPer != 0) {
                    if (amountIn < amountPer) {
                        temp = new TextComponentTranslation("dwmh.strings.summon_item_missing", itemCost.getDisplayName());
                        temp.getStyle().setColor(TextFormatting.DARK_RED);
                        SoundType.MINOR.playSound(player, stack);
                        player.sendMessage(temp);
                        return actionResult;
                    }
                }

                // Early breakpoint: if the Ocarina is broken
                if (!useableItem(stack)) {
                    temp = new TextComponentTranslation("dwmh.strings.broken_whistle");
                    temp.getStyle().setColor(TextFormatting.BLUE);
                    player.sendMessage(temp);
                    SoundType.BROKEN.playSound(player, stack);
                    return actionResult;
                }

                List<Entity> nearbyHorses = world.getEntities(Entity.class, (entity) -> isValidHorse(entity, player));
                for (Entity entity : nearbyHorses) {
                    EntityAnimal horse = (EntityAnimal) entity;
                    double max = DWMHConfig.Ocarina.getMaxDistance();
                    if (horse.getDistanceSq(player) < (max * max) || max == 0) {
                        if (amountPer != 0) {
                            // Early breakpoint: if the number consumed thus far taken from the initial total is less than the amount, break
                            if (amountIn - totalConsumed < amountPer) {
                                if (totalConsumed == 0) {
                                    temp = new TextComponentTranslation("dwmh.strings.summon_item_missing", itemCost.getDisplayName());
                                } else {
                                    temp = new TextComponentTranslation("dwmh.strings.summon_item_missing_middle", itemCost.getDisplayName(), totalConsumed);
                                }
                                temp.getStyle().setColor(TextFormatting.DARK_RED);
                                player.sendMessage(temp);
                                SoundType.MINOR.playSound(player, stack);
                                return actionResult;
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
                        if (DWMHConfig.Ocarina.functionality.getMaxUses() != 0) damageItem(stack, player);
                        if (!DWMHConfig.Ocarina.responses.quiet && !DWMHConfig.Ocarina.responses.simple) {
                            if (DWMH.steedProxy.hasCustomName(horse)) {
                                temp = new TextComponentTranslation("dwmh.strings.teleport_with_name", DWMH.steedProxy.getCustomNameTag(horse));
                                temp.getStyle().setColor(TextFormatting.GOLD);
                            } else {
                                temp = new TextComponentTranslation("dwmh.strings.teleport");
                                temp.getStyle().setColor(TextFormatting.GOLD);
                            }
                            player.sendMessage(temp);
                        }
                        if (DWMHConfig.Ocarina.functionality.getCooldown() > 0) {
                            player.getCooldownTracker().setCooldown(this, DWMHConfig.Ocarina.functionality.getCooldown());
                        }
                        player.swingArm(hand);
                        horse.getNavigator().clearPath();
                        if (DWMHConfig.Ocarina.home) {
                            horse.setHomePosAndDistance(pos, 5);
                        }
                    }
                }
                if (didStuff) {
                    SoundType.NORMAL.playSound(player, stack);
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

                    SoundType.MINOR.playSound(player, stack);
                } else if (DWMHConfig.Ocarina.responses.simple) {
                    temp = new TextComponentTranslation("dwmh.strings.simplest_teleport");
                    temp.getStyle().setColor(TextFormatting.GOLD);
                    player.sendMessage(temp);
                }
            }
        }
        return actionResult;
    }

    public static void onInteractOcarina (PlayerInteractEvent.EntityInteract event) {
        if (!DWMH.animaniaProxy.isLoaded()) return;

        EntityPlayer player = event.getEntityPlayer();
        ItemStack item = event.getItemStack();

        if (item.isEmpty() || !(item.getItem() instanceof ItemOcarina) || !(DWMH.animaniaProxy.isMyMod(event.getTarget()))) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);

        if (event.getWorld().isRemote) return;

        if (!player.isSneaking()) return;

        AbstractHorse horse = (AbstractHorse) event.getTarget();

        event.setCanceled(true);

        boolean tamed = horse.isTame();
        boolean tamedBy = tamed && horse.getOwnerUniqueId() != null && horse.getOwnerUniqueId().equals(player.getUniqueID());

        ITextComponent temp;
        String name = String.format("%s's Steed", player.getName());
        if (DWMH.steedProxy.hasCustomName(horse)) {
            if ((tamed && !tamedBy) || (DWMH.steedProxy.getCustomNameTag(horse).contains("'s Steed") && !DWMH.steedProxy.getCustomNameTag(horse).equals(name))) {
                temp = new TextComponentTranslation("dwmh.strings.not_your_horse");
                temp.getStyle().setColor(TextFormatting.RED);
            } else if (!tamed) {
                DWMH.steedProxy.setCustomNameTag(horse, "");
                temp = new TextComponentTranslation("dwmh.strings.unnamed");
                temp.getStyle().setColor(TextFormatting.YELLOW);
            } else {
                temp = new TextComponentTranslation("dwmh.strings.unnamed_fail");
                temp.getStyle().setColor(TextFormatting.RED);
            }
            player.sendMessage(temp);
        } else if (!tamed){
            horse.setCustomNameTag(name);
            temp = new TextComponentTranslation("dwmh.strings.animania_named");
            temp.getStyle().setColor(TextFormatting.GOLD);
            player.sendMessage(temp);
        } else if (tamedBy) {
            temp = new TextComponentTranslation("dwmh.strings.animania_tamed");
            temp.getStyle().setColor(TextFormatting.GOLD);
            player.sendMessage(temp);
        } else {
            temp = new TextComponentTranslation("dwmh.strings.animania_claimed");
            temp.getStyle().setColor(TextFormatting.RED);
            player.sendMessage(temp);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack par1ItemStack, World world, List<String> stacks, ITooltipFlag flags) {
        if(GuiScreen.isShiftKeyDown()) {
            if (!useableItem(par1ItemStack) && DWMHConfig.Ocarina.functionality.getMaxUses() != 0) {
                stacks.add(TextFormatting.DARK_RED + I18n.format("dwmh.strings.carrot.tooltip.broken"));
            }

            String right_click;
            String sneak_right_click;

            if (DWMHConfig.Ocarina.swap) {
                right_click = TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.whistle.tooltip.list_horses");
                sneak_right_click = TextFormatting.GOLD + I18n.format("dwmh.strings.shift_right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.whistle.tooltip.teleport_horses");
            } else {
                right_click = TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.whistle.tooltip.teleport_horses");
                sneak_right_click = TextFormatting.GOLD + I18n.format("dwmh.strings.shift_right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.whistle.tooltip.list_horses");
            }

            stacks.add(right_click);
            stacks.add(sneak_right_click);

            if (DWMH.animaniaProxy.isLoaded()) {
                stacks.add(TextFormatting.GOLD + I18n.format("dwmh.strings.shift_right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.animania_naming"));
            }

            if (par1ItemStack.getItemDamage() != 0 || DWMHConfig.Ocarina.functionality.getMaxUses() != 0) {
                stacks.add(TextFormatting.AQUA + I18n.format("dwmh.strings.repair_carrot", getRepairItem().getDisplayName()));
            }

            if (DWMHConfig.Ocarina.functionality.getSummonCost() != 0) {
                stacks.add(TextFormatting.RED + I18n.format("dwmh.strings.summon_tooltip", getCostItem().getDisplayName(), DWMHConfig.Ocarina.functionality.getSummonCost()));
            }
        } else {
            stacks.add(TextFormatting.DARK_GRAY + I18n.format("dwmh.strings.hold_shift"));
        }
    }

    public enum SoundType {
        NORMAL(0),
        MINOR(1),
        SPECIAL(2),
        BROKEN(3),
        NONE(-1);

        public int type;

        SoundType (int type) {
            this.type = type;
        }

        public SoundEvent getSoundEvent () {
            switch (type) {
                case 0:
                    return Sound.getRandomWhistle();
                case 1:
                    return Sound.getRandomMinorWhistle();
                case 2:
                    return Sound.WHISTLE_SPECIAL;
                case 3:
                    return Sound.WHISTLE_BROKEN;
                case -1:
                default:
                    return null;
            }
        }

        public void playSound (EntityPlayer player, ItemStack stack) {
            // This is predicated on a !player.world.isRemote check

            if (!DWMHConfig.Ocarina.responses.sounds) return;

            SoundEvent e = this.getSoundEvent();
            if (e == null) return;

            long cur = MinecraftServer.getCurrentTimeMillis();

            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
                stack.setTagCompound(tag);
            }

            if (tag.hasKey("dwmh:last_played")) {
                long lastPlayed = tag.getLong("dwmh:last_played");
                if (cur - lastPlayed < DWMHConfig.Ocarina.responses.soundDelay * 1000) {
                    return;
                }
            }

            tag.setLong("dwmh:last_played", cur);

            player.world.playSound(null, player.getPosition(), e, SoundCategory.PLAYERS, 1.5f, 1);

        }
    }
}
