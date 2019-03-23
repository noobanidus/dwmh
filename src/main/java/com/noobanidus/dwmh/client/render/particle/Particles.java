package com.noobanidus.dwmh.client.render.particle;

import com.noobanidus.dwmh.DWMH;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Particles extends Particle {
    public static final ResourceLocation particles = new ResourceLocation(DWMH.MODID, "textures/particle/particle.png");

    public static int getNextParticleID (Minecraft mc) {
        ParticleManager pm = mc.effectRenderer;
        return pm.particleTypes.keySet().stream().max(Integer::compare).orElse(-1);
    }

    float particleScaleOverTime;

    public Particles(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, float scale, int textureIndex) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.009999999776482582D;
        this.motionY *= 0.009999999776482582D;
        this.motionZ *= 0.009999999776482582D;
        this.motionY += 0.1D;
        this.particleScale *= 0.75F;
        this.particleScale *= scale;
        this.particleScaleOverTime = this.particleScale;
        this.particleMaxAge = 16;
        this.setParticleTextureIndex(textureIndex);
    }

    /**
     * Renders the particle
     */
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Minecraft.getMinecraft().renderEngine.bindTexture(particles);
        float f = ((float) this.particleAge + partialTicks) / (float) this.particleMaxAge * 32.0F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        this.particleScale = this.particleScaleOverTime * f;
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }

        this.move(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY) {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.motionX *= 0.8600000143051147D;
        this.motionY *= 0.8600000143051147D;
        this.motionZ *= 0.8600000143051147D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

    @SideOnly(Side.CLIENT)
    public static class HeartFactory implements IParticleFactory {
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new Particles(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, 2.0f, 0);
        }
    }

    @SideOnly(Side.CLIENT)
    public static class CarrotFactory implements IParticleFactory {
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new Particles(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, 2.0f, 2);
        }
    }

    @SideOnly(Side.CLIENT)
    public static class AgingFactory implements IParticleFactory {
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new Particles(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, 2.0f, 4);
        }
    }

    @SideOnly(Side.CLIENT)
    public static class TamingFactory implements IParticleFactory {
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new Particles(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, 2.0f, 6);
        }
    }
}

