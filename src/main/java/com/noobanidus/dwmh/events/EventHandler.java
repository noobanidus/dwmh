package com.noobanidus.dwmh.events;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.init.ItemRegistry;
import com.noobanidus.dwmh.util.EntityTracking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@SuppressWarnings("unused")
public class EventHandler {
  @SubscribeEvent
  public static void onItemMappingsEvent (RegistryEvent.MissingMappings<Item> event) {
    for (RegistryEvent.MissingMappings.Mapping<Item> m : event.getAllMappings()) {
      if (m.key.getNamespace().equals(DWMH.MODID)) {
        if (m.key.getPath().equals("whistle")) {
          m.remap(ItemRegistry.OCARINA);
        }
      }
    }
  }

  @SubscribeEvent
  public static void handleEntityEvent(LivingEvent.LivingUpdateEvent event) {
    Entity entity = event.getEntity();
    if (entity.world.isRemote) return;
    if (EntityTracking.isTrackingEntity(entity.getUniqueID())) {
      EntityTracking.updateEntityId(entity);
    }
  }

  @SubscribeEvent
  public static void onRightClickEntity (PlayerInteractEvent.EntityInteract event) {
    EntityPlayer player = event.getEntityPlayer();
    ItemStack stack = player.getHeldItem(event.getHand());
    if (stack.isEmpty()) return;

    if (stack.getItem() == ItemRegistry.OCARINA && player.isSneaking()) {
      if (!player.world.isRemote) {
        ItemRegistry.OCARINA.rightClickEntity(player, event.getTarget(), stack);
      }
      event.setCancellationResult(EnumActionResult.SUCCESS);
      event.setCanceled(true);
    }
  }
}
