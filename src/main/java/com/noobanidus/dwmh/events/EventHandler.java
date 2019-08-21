package com.noobanidus.dwmh.events;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.init.ItemRegistry;
import com.noobanidus.dwmh.util.EntityTracking;
import com.noobanidus.dwmh.world.EntityData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumActionResult;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@SuppressWarnings("unused")
public class EventHandler {
  @SubscribeEvent
  public static void handleItemMappings (RegistryEvent.MissingMappings<Item> event) {
    for (RegistryEvent.MissingMappings.Mapping<Item> m : event.getAllMappings()) {
      if (m.key.getNamespace().equals(DWMH.MODID)) {
        if (m.key.getPath().equals("whistle")) {
          m.remap(ItemRegistry.OCARINA);
        }
      }
    }
  }

  @SubscribeEvent
  public static void handleEntityUpdate (LivingEvent.LivingUpdateEvent event) {
    Entity entity = event.getEntity();
    if (entity.world.isRemote) return;
    if (EntityTracking.isTrackingEntity(entity.getUniqueID())) {
      EntityTracking.updateEntityId(entity);
    }
  }

  @SubscribeEvent
  public static void handleRightClickEntity (PlayerInteractEvent.EntityInteract event) {
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

  @SubscribeEvent
  public static void handleChunkUnload (ChunkEvent.Unload event) {
    World world = event.getWorld();
    if (!world.isRemote) {
      Chunk chunk = event.getChunk();
      // Handle this directly instead of through EntityTracking for speed
      EntityData data = EntityTracking.getData();
      if (data == null) {
        return;
      }
      for (ClassInheritanceMultiMap<Entity> entityList : chunk.getEntityLists()) {
        for (Entity entity : entityList) {
          if (data.trackedEntities.contains(entity.getUniqueID())) {
            // Save it!
            data.storedEntities.add(entity.getUniqueID());
            NBTTagCompound savedEntity = entity.writeToNBT(new NBTTagCompound());
            data.savedEntities.put(entity.getUniqueID(), savedEntity);
          }
        }
      }
    }
  }

  @SubscribeEvent
  public static void handleEntityJoin (EntityJoinWorldEvent event) {
    World world = event.getWorld();
    if (!world.isRemote) {
      EntityData data = EntityTracking.getData();
      if (data == null) {
        return;
      }
      // Do nothing if nothing is saved
      if (data.restoredEntities.isEmpty() && data.storedEntities.isEmpty()) {
        return;
      }
      Entity entity = event.getEntity();
      if (data.restoredEntities.contains(entity.getUniqueID())) {
        event.setCanceled(true);
        data.restoredEntities.remove(entity.getUniqueID());
      } else if (data.storedEntities.contains(entity.getUniqueID())) {
        event.setCanceled(true);
        data.storedEntities.remove(entity.getUniqueID());
        data.savedEntities.remove(entity.getUniqueID());
      }
    }
  }
}
