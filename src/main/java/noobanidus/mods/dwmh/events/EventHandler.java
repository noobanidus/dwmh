package noobanidus.mods.dwmh.events;

import noobanidus.mods.dwmh.DWMH;
import noobanidus.mods.dwmh.init.ItemRegistry;
import noobanidus.mods.dwmh.util.EntityTracking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DWMH.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@SuppressWarnings("unused")
public class EventHandler {
  @SubscribeEvent
  public static void handleEntityEvent(LivingEvent.LivingUpdateEvent event) {
    Entity entity = event.getEntity();
    if (entity.world.isRemote) return;
    if (EntityTracking.isTrackingEntity(entity.getUniqueID())) {
      EntityTracking.updateEntityId(entity);
    }
  }

  @SubscribeEvent
  public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
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
}
