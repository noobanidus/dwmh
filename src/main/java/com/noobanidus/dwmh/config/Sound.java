package com.noobanidus.dwmh.config;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class Sound {
    public static SoundEvent WHISTLE1 = createSoundEvent("whistle.1");
    public static SoundEvent WHISTLE2 = createSoundEvent("whistle.2");
    public static SoundEvent WHISTLE3 = createSoundEvent("whistle.3");
    public static SoundEvent WHISTLE4 = createSoundEvent("whistle.4");
    public static SoundEvent WHISTLE5 = createSoundEvent("whistle.5");
    public static SoundEvent WHISTLE_SPECIAL = createSoundEvent("whistle.special");
    public static List<SoundEvent> WHISTLES = Arrays.asList(WHISTLE1, WHISTLE2, WHISTLE3, WHISTLE4, WHISTLE5);

    private static SoundEvent createSoundEvent (String sound) {
        ResourceLocation name = new ResourceLocation("dwmh", sound);
        return new SoundEvent(name).setRegistryName(name);
    }

    private static Random random = new Random();

    public static SoundEvent getRandomWhistle () {
        if (random.nextInt(100) == 0) {
            return WHISTLE_SPECIAL;
        }

        return WHISTLES.get(random.nextInt(WHISTLES.size()));
    }

    @SubscribeEvent
    public static void registerSounds (RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(WHISTLE_SPECIAL);
        for (SoundEvent w : WHISTLES) {
            event.getRegistry().register(w);
        }
    }

}
