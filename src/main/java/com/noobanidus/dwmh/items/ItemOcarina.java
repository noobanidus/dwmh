package com.noobanidus.dwmh.items;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.config.Sound;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
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



    public List<TextComponentTranslation> directions = new ArrayList<>();

    public void init () {
        setMaxStackSize(1);
        setCreativeTab(DWMH.TAB);
        // These are the only "whistles" that hang around because I don't
        // want to break any backwards compatibility to people upgrading.
        setRegistryName("dwmh:whistle");
        setUnlocalizedName("dwmh.whistle");
        if (DWMHConfig.ocarina.functionality.maxUses != 0) {
            setMaxDamage(DWMHConfig.ocarina.functionality.maxUses);
            setInternalRepair(DWMHConfig.ocarina.functionality.repairItem);
        }
        registerPredicate("whistle_damage");

        for (int i = 0; i < 8; i++) {
            directions.add(new TextComponentTranslation(String.format("dwmh.strings.dir.%d", i)));
        }
    }

    private boolean isValidHorse (Entity entity, EntityPlayer player) {
        return isValidHorse(entity, player, false);
    }

    private boolean isValidHorse (Entity entity, EntityPlayer player, boolean listing) {
        if (DWMH.proxy.isListable(entity, player)) {
            if (listing) {
                return true;
            }
        } else {
            return false;
        }

        return DWMH.proxy.isTeleportable(entity, player);
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    public static boolean useableItem (ItemStack item) {
        if (DWMHConfig.ocarina.functionality.maxUses == 0) return true;

        return ItemDWMHRepairable.useableItem(item);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick (World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            BlockPos pos = player.getPosition();
            boolean didStuff = false;

            ITextComponent temp;

            if (player.isSneaking() && !DWMHConfig.ocarina.swap || !player.isSneaking() && DWMHConfig.ocarina.swap) {
                List<Entity> nearbyHorses = world.getEntities(Entity.class, (entity) -> isValidHorse(entity, player, true));
                for (Entity horse : nearbyHorses) {
                    didStuff = true;

                    ITextComponent result = new TextComponentTranslation(String.format("entity.%s.name", EntityList.getEntityString(horse)));
                    result.getStyle().setColor(TextFormatting.YELLOW);

                    if (DWMH.proxy.hasCustomName(horse)) {
                        temp = new TextComponentString(" (");
                        temp.appendSibling(new TextComponentTranslation("dwmh.strings.named"));
                        temp.appendText(" " + DWMH.proxy.getCustomNameTag(horse) + ")");
                        result.appendSibling(temp);
                    }

                    result.appendText(" ");
                    temp = new TextComponentTranslation("dwmh.strings.is");
                    temp.getStyle().setColor(TextFormatting.WHITE);
                    result.appendSibling(temp);
                    result.appendText(" ");

                    ITextComponent summonable = DWMH.proxy.getResponseKey(horse, player);

                    if (summonable != null) {
                        result.appendSibling(summonable);
                        result.appendText(" ");
                    }
                    temp = new TextComponentTranslation("dwmh.strings.at");
                    temp.getStyle().setColor(TextFormatting.WHITE);
                    result.appendSibling(temp);
                    result.appendText(" ");

                    BlockPos hpos = horse.getPosition();

                    float dist = player.getDistance(horse);

                    result.appendText(TextFormatting.WHITE + String.format("%d, %d, %d", hpos.getX(), hpos.getY(), hpos.getZ()));
                    if (DWMHConfig.ocarina.responses.distance) {
                        result.appendText(" (");

                        double angle = Math.atan2(hpos.getZ() - pos.getZ(), hpos.getX() - pos.getX());
                        int index = (int) Math.round(angle / Math.PI * 4 + 10) % 8;

                        result.appendText(String.format("%d", (int) dist));
                        result.appendText(" ");
                        temp = new TextComponentTranslation("dwmh.strings.blocks");
                        result.appendSibling(temp);
                        result.appendText(" ");
                        result.appendSibling(directions.get(index));
                        result.appendText(")");
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
                if (DWMHConfig.ocarina.responses.sounds) {
                    player.world.playSound(null, player.getPosition(), Sound.getRandomWhistle(), SoundCategory.PLAYERS, 16.0f, 1.0f);
                }

                if (!useableItem(stack)) {
                    temp = new TextComponentTranslation("dwmh.strings.broken_whistle");
                    temp.getStyle().setColor(TextFormatting.BLUE);
                    player.sendMessage(temp);
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
                List<Entity> nearbyHorses = world.getEntities(Entity.class, (entity) -> isValidHorse(entity, player));
                for (Entity entity : nearbyHorses) {
                    EntityAnimal horse = (EntityAnimal) entity;
                    double max = DWMHConfig.ocarina.maxDistance;
                    if (horse.getDistanceSq(player) < (max * max) || max == 0) {
                        horse.moveToBlockPosAndAngles(pos, horse.rotationYaw, horse.rotationPitch);
                        didStuff = true;
                        if (DWMHConfig.ocarina.functionality.maxUses != 0) damageItem(stack, player);
                        if (!DWMHConfig.ocarina.responses.quiet && !DWMHConfig.ocarina.responses.simple) {
                            if (DWMH.proxy.hasCustomName(horse)) {
                                temp = new TextComponentTranslation("dwmh.strings.complex_teleport_a");
                                temp.appendText(", " + DWMH.proxy.getCustomNameTag(horse) + ", ");
                                temp.appendSibling(new TextComponentTranslation("dwmh.strings.complex_teleport_b"));
                                temp.getStyle().setColor(TextFormatting.GOLD);
                            } else {
                                temp = new TextComponentTranslation("dwmh.strings.simple_teleport");
                                temp.getStyle().setColor(TextFormatting.GOLD);
                            }
                            player.sendMessage(temp);
                        }
                        if (DWMHConfig.ocarina.functionality.cooldown > 0) {
                            player.getCooldownTracker().setCooldown(this, DWMHConfig.ocarina.functionality.cooldown);
                        }
                        player.swingArm(hand);
                        horse.getNavigator().clearPath();
                        if (DWMHConfig.ocarina.home) {
                            horse.setHomePosAndDistance(pos, 5);
                        }
                    }
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
                } else if (DWMHConfig.ocarina.responses.simple) {
                    temp = new TextComponentTranslation("dwmh.strings.simplest_teleport");
                    temp.getStyle().setColor(TextFormatting.GOLD);
                    player.sendMessage(temp);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public static void onInteractOcarina (PlayerInteractEvent.EntityInteract event) {
        if (!DWMH.animaniaProxy.isLoaded()) return;

        EntityPlayer player = event.getEntityPlayer();
        ItemStack item = event.getItemStack();

        if (item.isEmpty() || !(item.getItem() instanceof ItemOcarina) || !(event.getTarget() instanceof AbstractHorse)) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);

        if (event.getWorld().isRemote) return;

        if (!player.isSneaking()) return;

        AbstractHorse horse = (AbstractHorse) event.getTarget();

        event.setCanceled(true);

        ITextComponent temp;
        String name = String.format("%s's Steed", player.getName());
        if (DWMH.proxy.hasCustomName(horse)) {
            if (DWMH.proxy.getCustomNameTag(horse).contains("'s Steed") && !DWMH.proxy.getCustomNameTag(horse).equals(name)) {
                temp = new TextComponentTranslation("dwmh.strings.not_your_horse");
                temp.getStyle().setColor(TextFormatting.RED);
            } else {
                DWMH.proxy.setCustomNameTag(horse, "");
                temp = new TextComponentTranslation("dwmh.strings.unnamed");
                temp.getStyle().setColor(TextFormatting.YELLOW);
            }
            player.sendMessage(temp);
        } else {
            horse.setCustomNameTag(name);
            temp = new TextComponentTranslation("dwmh.strings.animania_named");
            temp.getStyle().setColor(TextFormatting.GOLD);
            player.sendMessage(temp);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack par1ItemStack, World world, List<String> stacks, ITooltipFlag flags) {
        if(GuiScreen.isShiftKeyDown()) {
            if (!useableItem(par1ItemStack) && DWMHConfig.ocarina.functionality.maxUses != 0) {
                stacks.add(TextFormatting.DARK_RED + I18n.format("dwmh.strings.carrot.tooltip.broken"));
            }

            String right_click;
            String sneak_right_click;

            if (DWMHConfig.ocarina.swap) {
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

            stacks.add(TextFormatting.AQUA + I18n.format("dwmh.strings.repair_carrot", getRepairItem().getDisplayName()));
        } else {
            stacks.add(TextFormatting.DARK_GRAY + I18n.format("dwmh.strings.hold_shift"));
        }
    }

}
