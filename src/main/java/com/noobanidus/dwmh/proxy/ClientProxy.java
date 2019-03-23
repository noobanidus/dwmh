package com.noobanidus.dwmh.proxy;

import com.noobanidus.dwmh.client.render.particle.Particles;
import com.noobanidus.dwmh.commands.ClientEntityCommand;
import com.noobanidus.dwmh.util.ParticleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        Minecraft mc = Minecraft.getMinecraft();
        int i = Particles.getNextParticleID(mc);
        for (ParticleType type : ParticleType.values()) {
            type.setParticleId(i++);
        }

        mc.effectRenderer.registerParticle(ParticleType.AGING.getParticleId(), new Particles.AgingFactory());
        mc.effectRenderer.registerParticle(ParticleType.HEALING.getParticleId(), new Particles.HeartFactory());
        mc.effectRenderer.registerParticle(ParticleType.TAMING.getParticleId(), new Particles.TamingFactory());
        mc.effectRenderer.registerParticle(ParticleType.BREEDING.getParticleId(), new Particles.CarrotFactory());
    }

    @Override
    public void loadComplete(FMLLoadCompleteEvent event) {
        super.loadComplete(event);
        ClientCommandHandler.instance.registerCommand(new ClientEntityCommand());
    }
}