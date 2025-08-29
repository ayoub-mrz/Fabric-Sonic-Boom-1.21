package net.ayoubmrz.sonicboommod.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {

    public static final SimpleParticleType SONIC_BOOM_EFFECT = FabricParticleTypes.simple(true);

    public static void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE,
                Identifier.of("sonicboommod", "sonic_boom_effect"),
                SONIC_BOOM_EFFECT);
    }
}