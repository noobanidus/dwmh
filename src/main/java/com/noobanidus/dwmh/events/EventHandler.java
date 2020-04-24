package com.noobanidus.dwmh.events;

import com.noobanidus.dwmh.init.ItemRegistry;
import com.noobanidus.dwmh.items.ItemOcarina;
import com.noobanidus.dwmh.types.DimBlockPos;
import com.noobanidus.dwmh.util.EntityTracking;
import com.noobanidus.dwmh.util.Util;
import com.noobanidus.dwmh.world.EntityData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.UUID;

@Mod.EventBusSubscriber
@SuppressWarnings("unused")
public class EventHandler {
  @SubscribeEvent
  public static void handleRightClickEntity(PlayerInteractEvent.EntityInteract event) {
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
  public static void handleChunkUnload(ChunkEvent.Unload event) {
    World world = event.getWorld();
    if (!world.isRemote && (world instanceof WorldServer)) {
      WorldServer worldServer = (WorldServer) world;

      Chunk chunk = event.getChunk();
      // Handle this directly instead of through EntityTracking for speed
      EntityData data = EntityTracking.getData();

      for (ClassInheritanceMultiMap<Entity> entityList : chunk.getEntityLists()) {
        for (Entity entity : entityList) {
          if (data.trackedEntities.contains(entity.getUniqueID())) {
            DimBlockPos dbp = new DimBlockPos(entity.getPosition(), world.provider.getDimension());
            EntityTracking.storeEntity(entity, dbp);
          }
        }
      }
    }
  }
}
