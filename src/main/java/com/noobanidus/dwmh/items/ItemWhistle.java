package com.noobanidus.dwmh.items;

import com.google.common.base.Predicate;
import com.noobanidus.dwmh.DWMH;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemWhistle extends Item {
    public void init () {
        setMaxStackSize(1);
        setCreativeTab(DWMH.TAB);
        setRegistryName("dwmh:whistle");
        setUnlocalizedName("dwmh.whistle");
    }

    public static double maxDistance = DWMH.CONFIG.get("Whistle", "MaxDistance", 200d, "Max distance to summon horses when using the horse whistle (set to 0 for infinite distance).").getDouble(200d);
    public static boolean enabled = DWMH.CONFIG.get("Whistle", "Enable", true, "Set to false to disable the whistle from being registered. But... why?").getBoolean(true);
    public static boolean dimensional = DWMH.CONFIG.get("Whistle", "AcrossDimensions", false, "Allow for cross-dimensional teleportation.").getBoolean(false);

    @Override
    public ActionResult<ItemStack> onItemRightClick (World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            BlockPos pos = player.getPosition();

            List<AbstractHorse> nearbyHorses = world.getEntities(AbstractHorse.class, (entity) -> !entity.isDead && !entity.isChild() && entity.isTame() && (dimensional || entity.dimension == player.dimension) && entity.isHorseSaddled() && !entity.getLeashed() && entity.getOwnerUniqueId() == player.getUniqueID());
            boolean didStuff = false;
            for (AbstractHorse horse : nearbyHorses) {
                if (horse.getDistanceSq(player) < (maxDistance*maxDistance) || maxDistance == 0) {
                    horse.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), horse.rotationYaw, horse.rotationPitch);
                    didStuff = true;
                    String message = null;
                    if (horse.hasCustomName()) {
                        message = String.format(TextFormatting.GOLD + "Teleported your steed, %s, to you!", horse.getName());
                    } else {
                        message = TextFormatting.GOLD + "Teleported your steed to you!";
                    }
                    player.sendMessage(new TextComponentString(message));
                }
            }
            if (!didStuff) {
                player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "No steeds within range!"));
            } else {
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }
}
