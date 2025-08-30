package net.ayoubmrz.sonicboommod.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class SonicBoomParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;

    protected SonicBoomParticle(ClientWorld world, double x, double y, double z,
                                double velocityX, double velocityY, double velocityZ,
                                SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.velocityX = velocityX + (Math.random() * 2.0 - 1.0) * 0.05;
        this.velocityY = velocityY + (Math.random() * 2.0 - 1.0) * 0.05;
        this.velocityZ = velocityZ + (Math.random() * 2.0 - 1.0) * 0.05;

        this.scale = 1.0f + this.random.nextFloat() * 0.6f;
        this.maxAge = 40 + this.random.nextInt(8);
        this.collidesWithWorld = false;

        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;

        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.setSpriteForAge(this.spriteProvider);

            float f = (float)this.age / (float)this.maxAge;
            this.alpha = 1.0f - f * f * 0.5f;

            this.scale *= 1.14f;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientWorld world,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new SonicBoomParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        }
    }
}