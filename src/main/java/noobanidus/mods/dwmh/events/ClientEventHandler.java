package noobanidus.mods.dwmh.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import noobanidus.mods.dwmh.config.ConfigManager;

@SuppressWarnings("ConstantConditions")
@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
  public static void onClientTick(TickEvent.ClientTickEvent event) {
    if (ConfigManager.doSwimBoost.get()) {
      Minecraft mc = Minecraft.getInstance();
      if (mc != null) {
        ClientPlayerEntity player = mc.player;
        if (player != null && player.getRidingEntity() instanceof AbstractHorseEntity) {
          AbstractHorseEntity horse = (AbstractHorseEntity) player.getRidingEntity();
          if (horse.isInLava() || horse.isInWater()) {
            Vec3d motion = horse.getMotion();
            float x = (float) (motion.x * 0.08f);
            float y = 0.0125f;
            float z = (float) (motion.z * 0.08f);
            horse.addVelocity(x, y, z);
          }
        }
      }
    }
  }
}
