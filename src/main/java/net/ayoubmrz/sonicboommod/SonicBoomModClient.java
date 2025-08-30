package net.ayoubmrz.sonicboommod;

import net.ayoubmrz.sonicboommod.block.ModBlocks;
import net.ayoubmrz.sonicboommod.particle.ModParticles;
import net.ayoubmrz.sonicboommod.particle.SonicBoomParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;

public class SonicBoomModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        BlockRenderLayerMap.putBlock(ModBlocks.EGLE_STATUE, BlockRenderLayer.CUTOUT);

        ParticleFactoryRegistry.getInstance().register(
                ModParticles.SONIC_BOOM_EFFECT,
                SonicBoomParticle.Factory::new
        );

    }
}
