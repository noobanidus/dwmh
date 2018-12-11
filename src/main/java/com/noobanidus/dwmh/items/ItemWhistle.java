package com.noobanidus.dwmh.items;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemWhistle extends Item {

    private static double maxDistance = DWMH.CONFIG.get("Whistle", "MaxDistance", 200d, "Max distance to summon horses when using the horse whistle (set to 0 for infinite distance (excluding unloaded chunks and dimensions)).").getDouble(200d);
    private static boolean swap = DWMH.CONFIG.get("Whistle", "SwapSneak", false, "Set true to require sneaking to actively summon horses instead of printing horse information. This is useful if you don't wish to accidentally right-click and summon your steed(s) in an unsafe location.").getBoolean(false);
    public static boolean home = DWMH.CONFIG.get("Whistle", "SetHome", true, "Set to true to set the home and max wander distance to the location you most recently used the whistle for each horse teleported.").getBoolean(true);
    public static boolean skipDismount = DWMH.CONFIG.get("Whistle", "SkipDismount", false, "Set to true to skip detaching home points when dismounting a horse. Do this if you are having weird interactions/failure of functionality with HorseTweaks' home functionality.").getBoolean(false);
    private static int cooldown = DWMH.CONFIG.get("Whistle", "Cooldown", 0, "Specify a cooldown in ticks for usage of the whistle. Set to 0 to disable.").getInt(0);
    private static boolean quiet = DWMH.CONFIG.get("Whistle", "Quiet", false, "Set to true to disable messages when teleporting a horse to you.").getBoolean(true);
    private static boolean simple = DWMH.CONFIG.get("Whistle", "Simpler", false, "Set to true to prevent multiple messages when teleporting a horse to you, instead printing one message if any horses are teleported.").getBoolean(true);
    private static boolean otherRiders = DWMH.CONFIG.get("Whistle", "OtherRiders", false, "Set to true to enable summoning your horses that are being ridden by other people").getBoolean(false);

    public void init () {
        setMaxStackSize(1);
        setCreativeTab(DWMH.TAB);
        setRegistryName("dwmh:whistle");
        setUnlocalizedName("dwmh.whistle");
    }

    private boolean isValidHorse (AbstractHorse entity, EntityPlayer player, boolean listing) {
        if (entity == null || entity.isDead || entity.isChild() || !entity.isTame()) {
            return false;
        }

        // There are no vaguely possible dimensional shenanigans I CBF to implement
        if (entity.dimension != player.dimension || (entity.getOwnerUniqueId() != null && !entity.getOwnerUniqueId().equals(player.getUniqueID()))) {
            return false;
        }

        if (listing) {
            return true;
        }

        // Also prevents you from summoning your current mount that you're on to yourself.
        if (!entity.isHorseSaddled() || entity.getLeashed() || (entity.isBeingRidden() && entity.isRidingSameEntity(player))) {
            return false;
        }

        // And prevent you from summoning horses being ridden by other players
        if (entity.isBeingRidden() && !otherRiders) {
            return false;
        }

        // Compatibility for Horse Power device-attached horses
        if (entity.hasHome() && entity.world.getTileEntity(entity.getHomePosition()) != null) {
            return false;
        }

        return true;
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick (World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            BlockPos pos = player.getPosition();
            boolean didStuff = false;

            if (player.isSneaking() && !swap || !player.isSneaking() && swap) {
                List<AbstractHorse> nearbyHorses = world.getEntities(AbstractHorse.class, (entity) -> isValidHorse(entity, player, true));
                for (AbstractHorse horse : nearbyHorses) {
                    didStuff = true;

                    ITextComponent result = new TextComponentTranslation(String.format("entity.%s.name", EntityList.getEntityString(horse)));
                    result.getStyle().setColor(TextFormatting.YELLOW);

                    ITextComponent temp;
                    if (horse.hasCustomName()) {
                        temp = new TextComponentString(" (");
                        temp.appendSibling(new TextComponentTranslation("dwmh.strings.named"));
                        temp.appendText(horse.getCustomNameTag() + " )");
                        result.appendSibling(temp);
                    }

                    result.appendText(" ");
                    result.appendSibling(new TextComponentTranslation("dwmh.strings.is"));
                    result.appendText(" ");

                    TextComponentTranslation summonable;

                    if (horse.hasHome() && horse.world.getTileEntity(horse.getHomePosition()) != null) {
                        summonable = new TextComponentTranslation("dwmh.strings.unsummonable.working");
                        summonable.getStyle().setColor(TextFormatting.DARK_RED);
                    } else if (horse.getLeashed()) {
                        summonable = new TextComponentTranslation("dwmh.strings.unsummonable.leashed");
                        summonable.getStyle().setColor(TextFormatting.DARK_RED);
                    } else if (!horse.isHorseSaddled()) {
                        summonable = new TextComponentTranslation("dwmh.strings.unsummonable.unsaddled");
                        summonable.getStyle().setColor(TextFormatting.DARK_RED);
                    } else if (horse.isBeingRidden() && horse.isRidingSameEntity(player)) {
                        summonable = new TextComponentTranslation("dwmh.strings.unsummonable.ridden");
                        summonable.getStyle().setColor(TextFormatting.DARK_RED);
                    } else if (horse.isBeingRidden() && !otherRiders) {
                        summonable = new TextComponentTranslation("dwmh.strings.unsummonable.ridden_other");
                        summonable.getStyle().setColor(TextFormatting.DARK_RED);
                    } else {
                        summonable = new TextComponentTranslation("dwmh.strings.summonable");
                        summonable.getStyle().setColor(TextFormatting.AQUA);
                    }

                    result.appendSibling(summonable);
                    result.appendText(" ");
                    result.appendSibling(new TextComponentTranslation("dwmh.strings.at"));
                    result.appendText(" ");

                    BlockPos hpos = horse.getPosition();
                    result.appendText(TextFormatting.GOLD + String.format("X: %d, Y: %d, Z: %d", hpos.getX(), hpos.getY(), hpos.getZ()));
                    player.sendMessage(result);
                }
                if (!didStuff) {
                    ITextComponent temp = new TextComponentTranslation("dwmh.strings.no_eligible_to_list");
                    temp.getStyle().setColor(TextFormatting.RED);
                    player.sendMessage(temp);
                } else {
                    player.swingArm(hand);
                }
            } else {
                List<AbstractHorse> nearbyHorses = world.getEntities(AbstractHorse.class, (entity) -> isValidHorse(entity, player, false));
                for (AbstractHorse horse : nearbyHorses) {
                    if (horse.getDistanceSq(player) < (maxDistance * maxDistance) || maxDistance == 0) {
                        horse.moveToBlockPosAndAngles(pos, horse.rotationYaw, horse.rotationPitch);
                        didStuff = true;
                        if (!quiet && !simple) {
                            ITextComponent temp;
                            if (horse.hasCustomName()) {
                                temp = new TextComponentTranslation("dwmh.strings.complex_teleport_a");
                                temp.appendText(", " + horse.getCustomNameTag() + ", ");
                                temp.appendSibling(new TextComponentTranslation("dwmh.strings.complex_teleport_b"));
                                temp.getStyle().setColor(TextFormatting.GOLD);
                            } else {
                                temp = new TextComponentTranslation("dwmh.strings.simple_teleport");
                                temp.getStyle().setColor(TextFormatting.GOLD);
                            }
                            player.sendMessage(temp);
                        }
                        if (cooldown > 0) {
                            player.getCooldownTracker().setCooldown(this, cooldown);
                        }
                        player.swingArm(hand);
                        horse.getNavigator().clearPath();
                        if (home) {
                            horse.setHomePosAndDistance(pos, 5);
                        }
                    }
                }
                ITextComponent temp;
                if (!didStuff) {
                    if (player.isRiding()) {
                        temp = new TextComponentTranslation("dwmh.strings.no_eligible_to_teleport_riding");
                        temp.getStyle().setColor(TextFormatting.RED);
                    } else {
                        temp = new TextComponentTranslation("dwmh.strings.no_eligible_to_teleport");
                        temp.getStyle().setColor(TextFormatting.RED);
                    }
                    player.sendMessage(temp);
                } else if (simple) {
                    temp = new TextComponentTranslation("dwmh.strings.simplest_teleport");
                    temp.getStyle().setColor(TextFormatting.GOLD);
                    player.sendMessage(temp);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack par1ItemStack, World world, List<String> stacks, ITooltipFlag flags) {
        if(GuiScreen.isShiftKeyDown()) {
            String right_click;
            String sneak_right_click;

            if (swap) {
                right_click = TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.whistle.tooltip.list_horses");
                sneak_right_click = TextFormatting.GOLD + I18n.format("dwmh.strings.shift_right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.whistle.tooltip.teleport_horses");
            } else {
                right_click = TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.whistle.tooltip.teleport_horses");
                sneak_right_click = TextFormatting.GOLD + I18n.format("dwmh.strings.shift_right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.whistle.tooltip.list_horses");
            }

            stacks.add(right_click);
            stacks.add(sneak_right_click);
        } else {
            stacks.add(TextFormatting.DARK_GRAY + I18n.format("dwmh.strings.hold_shift"));
        }
    }

}
