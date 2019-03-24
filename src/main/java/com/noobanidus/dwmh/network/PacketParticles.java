package com.noobanidus.dwmh.network;

import com.noobanidus.dwmh.client.render.particle.RenderManager;
import com.noobanidus.dwmh.util.ParticleType;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketParticles {
    public static class GenerateParticles implements IMessage {
        private byte particleId;
        private int entityId;

        public GenerateParticles() {
        }

        public GenerateParticles (Entity entity, ParticleType particle) {
            this.entityId = entity.getEntityId();
            this.particleId = (byte) particle.getInternalId();
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.entityId = buf.readInt();
            this.particleId = buf.readByte();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(this.entityId);
            buf.writeByte(this.particleId);
        }

        @SideOnly(Side.CLIENT)
        public ParticleType getParticle () {
            return ParticleType.byId((int) this.particleId);
        }

        @SideOnly(Side.CLIENT)
        public Entity getEntity (World world) {
            return world.getEntityByID(this.entityId);
        }

        public static class Handler extends PacketHandler.ClientHandler<GenerateParticles> {
            @Override
            @SideOnly(Side.CLIENT)
            public void processMessage(GenerateParticles message, MessageContext ctx) {
                Minecraft mc = Minecraft.getMinecraft();
                Entity entity = message.getEntity(mc.world);
                if (entity != null) {
                    RenderManager.renderParticles(entity, message.getParticle());
                }
            }
        }
    }
}
