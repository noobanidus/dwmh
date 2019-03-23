package com.noobanidus.dwmh.client.render.particle;

import com.noobanidus.dwmh.util.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class RenderManager {
    private static final Random rand = new Random();

    public static void renderParticles (Entity entity, ParticleType type) {
        if (type == ParticleType.NULL) return;

        for (int i = 0; i < 7; ++i) {
            double d0 = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;
            entity.world.spawnParticle(type.getParticleId(), false, entity.posX + (double) (rand.nextFloat() * entity.width * 2.0F) - (double) entity.width, entity.posY + 0.5D + (double) (rand.nextFloat() * entity.height), entity.posZ + (double) (rand.nextFloat() * entity.width * 2.0F) - (double) entity.width, d0, d1, d2);
        }
    }
}
