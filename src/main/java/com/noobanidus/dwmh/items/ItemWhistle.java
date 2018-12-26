package com.noobanidus.dwmh.items;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemWhistle extends Item {

    public static double maxDistance = DWMH.CONFIG.get("Whistle", "MaxDistance", 200d, "Max distance to summon horses when using the horse whistle (set to 0 for infinite distance (excluding unloaded chunks and dimensions)).").getDouble(200d);
    public static boolean swap = DWMH.CONFIG.get("Whistle", "SwapSneak", false, "Set true to require sneaking to actively summon horses instead of printing horse information. This is useful if you don't wish to accidentally right-click and summon your steed(s) in an unsafe location.").getBoolean(false);
    public static boolean home = DWMH.CONFIG.get("Whistle", "SetHome", true, "Set to true to set the home and max wander distance to the location you most recently used the whistle for each horse teleported.").getBoolean(true);
    public static boolean skipDismount = DWMH.CONFIG.get("Whistle", "SkipDismount", false, "Set to true to skip detaching home points when dismounting a horse. Do this if you are having weird interactions/failure of functionality with HorseTweaks' home functionality.").getBoolean(false);
    public static int cooldown = DWMH.CONFIG.get("Whistle", "Cooldown", 0, "Specify a cooldown in ticks for usage of the whistle. Set to 0 to disable.").getInt(0);
    public static boolean quiet = DWMH.CONFIG.get("Whistle", "Quiet", false, "Set to true to disable messages when teleporting a horse to you.").getBoolean(true);
    public static boolean simple = DWMH.CONFIG.get("Whistle", "Simpler", false, "Set to true to prevent multiple messages when teleporting a horse to you, instead printing one message if any horses are teleported.").getBoolean(true);
    public static boolean otherRiders = DWMH.CONFIG.get("Whistle", "OtherRiders", false, "Set to true to enable summoning your horses that are being ridden by other people").getBoolean(false);
    public static boolean distance = DWMH.CONFIG.get("Whistle", "Distance", true, "Set to false to disable showing the distance horses are away from you when listing them.").getBoolean(true);
    // TODO: Default is false
    public static boolean path = DWMH.CONFIG.get("Whistle", "Pathing", true, "Set to false to have horses path towards the player at increased speed rather than be teleported.").getBoolean(true);
    public static double pathSpeed = DWMH.CONFIG.get("Whistle", "PathingSpeed", 3D, "The speed at which steeds should approach you when summoned with the ocarina's pathing functionality.").getDouble(3D);

    public static final String TAG_MOVING_TO_LOCATION = "dwmh:moving_to_location";
    private static final String TAG_MOVING_DESTINATION = "dwmh:moving_destination";
    private static final String TAG_ORIGINAL_RANGE = "dwmh:original_range";
    private static final String TAG_TICK_COUNTER = "dwmh:tick_counter";

    public void init () {
        setMaxStackSize(1);
        setCreativeTab(DWMH.TAB);
        setRegistryName("dwmh:whistle");
        setUnlocalizedName("dwmh.whistle");
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

    private boolean getPathToWhistle (EntityPlayer player, Entity horse) {
        if (!(horse instanceof EntityLiving)) return false;

        EntityLiving animal = (EntityLiving) horse;

        NBTTagCompound data = animal.getEntityData();

        if (data.getBoolean(TAG_MOVING_TO_LOCATION)) {
            return false;
        }

        data.setBoolean(TAG_MOVING_TO_LOCATION, true);
        data.setInteger(TAG_TICK_COUNTER, 0);

        BlockPos pos = player.getPosition();

        data.setIntArray(TAG_MOVING_DESTINATION, new int[]{pos.getX()-1, pos.getY(), pos.getZ()-1});

        PathNavigate navigator = animal.getNavigator();
        IAttributeInstance attr = animal.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        double oldValue = attr.getBaseValue();

        data.setDouble(TAG_ORIGINAL_RANGE, oldValue);

        double maxRange = (ItemWhistle.maxDistance == 0) ? 1000D : ItemWhistle.maxDistance;
        attr.setBaseValue(maxRange);
        boolean res = navigator.tryMoveToXYZ(pos.getX()-1, pos.getY(), pos.getZ()-1, pathSpeed);
        if (!res) {
            movementFailed(animal);
        }

        return res;
    }

    public static boolean isMoving (Entity entity) {
        if (!(entity instanceof EntityLiving)) return false;

        NBTTagCompound data = entity.getEntityData();
        return data.hasKey(TAG_MOVING_TO_LOCATION) && data.getBoolean(TAG_MOVING_TO_LOCATION);
    }

    public static void movementFailed (Entity entity) {
        DWMH.LOG.info("Horse wasn't able to path to the player.");
        entity.getEntityData().setBoolean(TAG_MOVING_TO_LOCATION, false);
    }

    public static void onLivingUpdate (LivingEvent.LivingUpdateEvent event) {
        if (!ItemWhistle.path) return;

        if (!(event.getEntity() instanceof EntityLiving)) return;

        EntityLiving animal = (EntityLiving) event.getEntityLiving();

        NBTTagCompound data = animal.getEntityData();

        if (data.hasKey(TAG_TICK_COUNTER)) {
            int tick = data.getInteger(TAG_TICK_COUNTER);
            if (tick < 50) {
                data.setInteger(TAG_TICK_COUNTER, tick+1);
                return;
            } else {
                data.setInteger(TAG_TICK_COUNTER, 0);
            }
        }

        if (isMoving(animal)) {
            int[] destArr = data.getIntArray(TAG_MOVING_DESTINATION);
            BlockPos pos = new BlockPos(destArr[0], destArr[1], destArr[2]);

            float dist = MathHelper.sqrt(animal.getDistanceSq(pos));

            //float dist = MathHelper.sqrt(animal.getNavigator())

            // we can stop!
            if (dist < 9) {
                animal.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(data.getDouble(TAG_ORIGINAL_RANGE));
                data.setBoolean(TAG_MOVING_TO_LOCATION, false);
                DWMH.LOG.info("Horse reached close enough to dsestination, clearing path!");
            } else {
                DWMH.LOG.info("Horse isn't close enough, moving it again");
                boolean res = animal.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), pathSpeed);
                if (!res) {
                    //movementFailed(animal);
                }
            }
        }
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

            ITextComponent temp;

            if (player.isSneaking() && !swap || !player.isSneaking() && swap) {
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
                    if (distance) {
                        int rel_x = pos.getX() - hpos.getY();
                        int rel_z = pos.getZ() - hpos.getZ();

                        String key;

                        // TODO: atan2(rel_x, rel_z) for angle. how this work

                        if (Math.abs(rel_x) > Math.abs(rel_z)) {
                            key = ((rel_x > 0) ? "east" : "west");
                        } else {
                            key = ((rel_z > 0) ? "north": "south");
                        }

                        result.appendText(" (");

                        result.appendText(String.format("%d", (int) dist));
                        result.appendText(" ");
                        temp = new TextComponentTranslation("dwmh.strings.blocks");
                        result.appendSibling(temp);
                        result.appendText(" ");
                        temp = new TextComponentTranslation(String.format("dwmh.strings.%s", key));
                        result.appendSibling(temp);
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
                List<Entity> nearbyHorses = world.getEntities(Entity.class, (entity) -> isValidHorse(entity, player));
                for (Entity entity : nearbyHorses) {
                    EntityAnimal horse = (EntityAnimal) entity;
                    if (horse.getDistanceSq(player) < (maxDistance * maxDistance) || maxDistance == 0) {
                        if (!path) {
                            horse.moveToBlockPosAndAngles(pos, horse.rotationYaw, horse.rotationPitch);
                        } else {
                            getPathToWhistle(player, horse);
                        }
                        didStuff = true;
                        if (!quiet && !simple) {
                            if (DWMH.proxy.hasCustomName(horse)) {
                                if (!path) {
                                    temp = new TextComponentTranslation("dwmh.strings.complex_teleport_a");
                                } else {
                                    temp = new TextComponentTranslation("dwmh.strings.complex_path_a");
                                }
                                temp.appendText(", " + DWMH.proxy.getCustomNameTag(horse) + ", ");
                                temp.appendSibling(new TextComponentTranslation("dwmh.strings.complex_teleport_b"));
                                temp.getStyle().setColor(TextFormatting.GOLD);
                            } else {
                                if (!path) {
                                    temp = new TextComponentTranslation("dwmh.strings.simple_teleport");
                                } else {
                                    temp = new TextComponentTranslation("dwmh.strings.simple_path");
                                }
                                temp.getStyle().setColor(TextFormatting.GOLD);
                            }
                            player.sendMessage(temp);
                        }
                        if (cooldown > 0) {
                            player.getCooldownTracker().setCooldown(this, cooldown);
                        }
                        player.swingArm(hand);
                        if (!path) horse.getNavigator().clearPath();
                        if (home) {
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
                } else if (simple) {
                    if (path) {
                        temp = new TextComponentTranslation("dwmh.strings.simplest_teleport");
                    } else {
                        temp = new TextComponentTranslation("dwmh.strings.simplest_path");
                    }
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

        if (item.isEmpty() || !(item.getItem() instanceof ItemWhistle) || !(event.getTarget() instanceof AbstractHorse)) {
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
            String right_click;
            String sneak_right_click;

            if (swap) {
                right_click = TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.whistle.tooltip.list_horses");
                if (!path) {
                    sneak_right_click = TextFormatting.GOLD + I18n.format("dwmh.strings.shift_right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.whistle.tooltip.teleport_horses");
                } else {
                    sneak_right_click = TextFormatting.GOLD + I18n.format("dwmh.strings.shift_right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.whistle.tooltip.summon_horses");
                }
            } else {
                if (!path) {
                    right_click = TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.whistle.tooltip.teleport_horses");
                } else {
                    right_click = TextFormatting.GOLD + I18n.format("dwmh.strings.right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.whistle.tooltip.summon_horses");
                }
                sneak_right_click = TextFormatting.GOLD + I18n.format("dwmh.strings.shift_right_click") + " " + TextFormatting.WHITE + I18n.format("dwmh.strings.whistle.tooltip.list_horses");
            }

            stacks.add(right_click);
            stacks.add(sneak_right_click);
        } else {
            stacks.add(TextFormatting.DARK_GRAY + I18n.format("dwmh.strings.hold_shift"));
        }
    }
}
