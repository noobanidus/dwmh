package noobanidus.mods.dwmh.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.dwmh.DWMH;
import noobanidus.mods.dwmh.init.ItemRegistry;
import noobanidus.mods.dwmh.types.DimBlockPos;
import noobanidus.mods.dwmh.util.EntityTracking;
import noobanidus.mods.dwmh.world.EntityData;

@SuppressWarnings("unused")
public class EventHandler {
  @SubscribeEvent
  public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
    PlayerEntity player = event.getPlayer();
    ItemStack stack = player.getHeldItem(event.getHand());
    if (stack.isEmpty()) return;

    if (stack.getItem() == ItemRegistry.OCARINA && player.isSneaking()) {
      if (!player.world.isRemote) {
        ItemRegistry.OCARINA.rightClickEntity(player, event.getTarget(), stack);
      }
      event.setCancellationResult(ActionResultType.SUCCESS);
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public void onEntityUnload (ChunkEvent.Unload event) {
    IWorld inWorld = event.getWorld();
    if (inWorld.isRemote() || !(inWorld instanceof ServerWorld)) {
      return;
    }

    ServerWorld world = (ServerWorld) inWorld;

    IChunk inChunk = event.getChunk();
    if (!(inChunk instanceof Chunk)) {
      return;
    }

    EntityData data = EntityTracking.getData();

    Chunk chunk = (Chunk) inChunk;
    for (ClassInheritanceMultiMap<Entity> entityList : chunk.getEntityLists()) {
      for (Entity entity : entityList) {
        if (data.trackedEntities.contains(entity.getUniqueID())) {
          DimBlockPos dbp = new DimBlockPos(entity.getPosition(), world.getDimensionKey());
          EntityTracking.storeEntity(entity, dbp);
        }
      }
    }
  }
}
