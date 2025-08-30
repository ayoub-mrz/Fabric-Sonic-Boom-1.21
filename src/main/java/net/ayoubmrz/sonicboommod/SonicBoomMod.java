package net.ayoubmrz.sonicboommod;

import net.ayoubmrz.sonicboommod.block.ModBlocks;
import net.ayoubmrz.sonicboommod.particle.ModParticles;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SonicBoomMod implements ModInitializer {
	public static final String MOD_ID = "sonicboommod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModParticles.registerParticles();
		ModBlocks.registerModBlocks();
	}
}