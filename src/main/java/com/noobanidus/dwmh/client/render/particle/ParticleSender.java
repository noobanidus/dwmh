package com.noobanidus.dwmh.client.render.particle;

import com.noobanidus.dwmh.network.PacketHandler;
import com.noobanidus.dwmh.network.PacketParticles;
import com.noobanidus.dwmh.util.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ParticleSender {
    public static void generateParticles (Entity entity, ParticleType type) {
        PacketParticles.GenerateParticles packet = new PacketParticles.GenerateParticles(entity, type);
        PacketHandler.sendToAllAround(packet, new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 128));
    }
}
