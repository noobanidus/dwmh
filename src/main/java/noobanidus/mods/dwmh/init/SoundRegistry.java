package noobanidus.mods.dwmh.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import noobanidus.mods.dwmh.DWMH;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid= DWMH.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class SoundRegistry {
  public static SoundEvent WHISTLE1 = createSoundEvent("whistle.1");
  public static SoundEvent WHISTLE2 = createSoundEvent("whistle.2");
  public static SoundEvent WHISTLE3 = createSoundEvent("whistle.3");
  public static SoundEvent WHISTLE4 = createSoundEvent("whistle.4");
  public static SoundEvent WHISTLE5 = createSoundEvent("whistle.5");
  public static SoundEvent WHISTLE6 = createSoundEvent("whistle.6");
  public static SoundEvent WHISTLE7 = createSoundEvent("whistle.7");
  public static SoundEvent WHISTLE_SPECIAL = createSoundEvent("whistle.special");
  public static List<SoundEvent> WHISTLES = Arrays.asList(WHISTLE1, WHISTLE2, WHISTLE3, WHISTLE4, WHISTLE5, WHISTLE6, WHISTLE7);

  public static SoundEvent MINOR_WHISTLE1 = createSoundEvent("whistle.minor.1");
  public static SoundEvent MINOR_WHISTLE2 = createSoundEvent("whistle.minor.2");
  public static List<SoundEvent> MINOR_WHISTLES = Arrays.asList(MINOR_WHISTLE1, MINOR_WHISTLE2);

  public static SoundEvent WHISTLE_BROKEN = createSoundEvent("whistle.broken");
  private static Random random = new Random();

  private static SoundEvent createSoundEvent(String sound) {
    ResourceLocation name = new ResourceLocation("dwmh", sound);
    return new SoundEvent(name).setRegistryName(name);
  }

  public static SoundEvent getRandomWhistle() {
    if (random.nextInt(25) == 0) {
      return WHISTLE_SPECIAL;
    }

    return WHISTLES.get(random.nextInt(WHISTLES.size()));
  }

  public static SoundEvent getRandomMinorWhistle() {
    return MINOR_WHISTLES.get(random.nextInt(MINOR_WHISTLES.size()));
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
    IForgeRegistry<SoundEvent> registry = event.getRegistry();
    registry.register(WHISTLE_SPECIAL);
    registry.register(WHISTLE_BROKEN);
    WHISTLES.forEach(registry::register);
    MINOR_WHISTLES.forEach(registry::register);
  }

}
