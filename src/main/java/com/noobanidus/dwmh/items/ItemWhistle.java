package com.noobanidus.dwmh.items;

import com.google.common.base.Predicate;
import com.noobanidus.dwmh.DWMH;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.StringJoiner;

public class ItemWhistle extends Item {
    public void init () {
        setMaxStackSize(1);
        setCreativeTab(DWMH.TAB);
        setRegistryName("dwmh:whistle");
        setUnlocalizedName("dwmh.whistle");
    }

    private static double maxDistance = DWMH.CONFIG.get("Whistle", "MaxDistance", 200d, "Max distance to summon horses when using the horse whistle (set to 0 for infinite distance (excluding unloaded chunks and dimensions)).").getDouble(200d);
    private static boolean swap = DWMH.CONFIG.get("Whistle", "SwapSneak", false, "Set true to require sneaking to actively summon horses instead of printing horse information. This is useful if you don't wish to accidentally right-click and summon your steed(s) in an unsafe location.").getBoolean(false);
    public static boolean home = DWMH.CONFIG.get("Whistle", "SetHome", true, "Set to true to set the home and max wander distance to the location you most recently used the whistle for each horse teleported.").getBoolean(true);
    public static boolean skipDismount = DWMH.CONFIG.get("Whistle", "SkipDismount", false, "Set to true to skip detaching home points when dismounting a horse. Do this if you are having weird interactions/failure of functionality with HorseTweaks' home functionality.").getBoolean(false);
    private static int cooldown = DWMH.CONFIG.get("Whistle", "Cooldown", 0, "Specify a cooldown in ticks for usage of the whistle. Set to 0 to disable.").getInt(0);
    private static boolean quiet = DWMH.CONFIG.get("Whistle", "Quiet", false, "Set to true to disable messages when teleporting a horse to you.").getBoolean(true);
    private static boolean simple = DWMH.CONFIG.get("Whistle", "Simpler", false, "Set to true to prevent multiple messages when teleporting a horse to you, instead printing one message if any horses are teleported.").getBoolean(true);

    protected boolean isValidHorse (AbstractHorse entity, EntityPlayer player, boolean listing) {
        if (entity == null || entity.isDead || entity.isChild() || !entity.isTame()) {
            return false;
        }

        // I'm not sure under which circumstances dimensions may differ
        if (entity.dimension != player.dimension || (entity.getOwnerUniqueId() != null && !entity.getOwnerUniqueId().equals(player.getUniqueID()))) {
            return false;
        }

        if (listing) {
            return true;
        }

        if (!entity.isHorseSaddled() || entity.getLeashed() || entity.isBeingRidden()) {
            return false;
        }

        return true;
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick (World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            BlockPos pos = player.getPosition();
            boolean didStuff = false;

            if (player.isSneaking() && !swap || !player.isSneaking() && swap) {
                List<AbstractHorse> nearbyHorses = world.getEntities(AbstractHorse.class, (entity) -> isValidHorse(entity, player, true));
                for (AbstractHorse horse : nearbyHorses) {
                    didStuff = true;
                    ITextComponent result = new TextComponentString("Your " + TextFormatting.YELLOW);
                    result.appendSibling(new TextComponentTranslation(String.format("entity.%s.name", EntityList.getEntityString(horse))));
                    if (horse.hasCustomName()) {
                        result.appendText(TextFormatting.GOLD + String.format(" (named %s)", horse.getCustomNameTag()));
                    }
                    StringJoiner join = new StringJoiner(", ");
                    result.appendText(TextFormatting.WHITE + " is");




                    if (horse.isHorseSaddled()) { join.add(TextFormatting.AQUA + "saddled"); if (!horse.getLeashed() && !horse.isBeingRidden()) join.add(TextFormatting.GREEN + "summonable"); } else { join.add(TextFormatting.DARK_RED + "unsaddled"); join.add(TextFormatting.DARK_RED + "unsummonable"); }
                    if (horse.getLeashed() && horse.isHorseSaddled()) { join.add(TextFormatting.DARK_RED + "leashed"); join.add(TextFormatting.DARK_RED + "unsummonable"); }
                    if (horse.getLeashed() && !horse.isHorseSaddled()) { join.add(TextFormatting.DARK_RED + "leashed"); }
                    if (horse.isBeingRidden()) { join.add(TextFormatting.DARK_RED + "(being ridden by you)"); }
                    if (join.length() != 0) {
                        result.appendText(" " + join.toString());
                    }
                    BlockPos hpos = horse.getPosition();
                    result.appendText(TextFormatting.WHITE + " at " + TextFormatting.GOLD + String.format("X: %d, Y: %d, Z: %d", hpos.getX(), hpos.getY(), hpos.getZ()));
                    player.sendMessage(result);
                }
                if (!didStuff) {
                    player.sendMessage(new TextComponentString(TextFormatting.RED + "You don't have any horses in this dimension!"));
                } else {
                    player.swingArm(hand);
                }
            } else {
                List<AbstractHorse> nearbyHorses = world.getEntities(AbstractHorse.class, (entity) -> isValidHorse(entity, player, false));
                for (AbstractHorse horse : nearbyHorses) {
                    if (horse.getDistanceSq(player) < (maxDistance * maxDistance) || maxDistance == 0) {
                        horse.moveToBlockPosAndAngles(pos, horse.rotationYaw, horse.rotationPitch);
                        didStuff = true;
                        String message;
                        if (horse.hasCustomName()) {
                            message = String.format(TextFormatting.GOLD + "Teleported your steed, %s, to you!", horse.getCustomNameTag());
                        } else {
                            message = TextFormatting.GOLD + "Teleported your steed to you!";
                        }
                        if (!quiet && !simple) {
                            player.sendMessage(new TextComponentString(message));
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
                if (!didStuff) {
                    if (player.isRiding()) {
                        player.sendMessage(new TextComponentString(TextFormatting.RED + "No other eligible steeds are within range, loaded chunks or this dimension! Apart from the one you're riding."));
                    } else {
                        player.sendMessage(new TextComponentString(TextFormatting.RED + "No other eligible steeds are within range, loaded chunks or this dimension!"));
                    }
                } else if (simple) {
                    player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Teleported horse(s) to you!"));
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
