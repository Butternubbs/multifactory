
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.multifactory.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;

import net.multifactory.MultifactoryMod;

public class MultifactoryModParticleTypes {
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MultifactoryMod.MODID);
	public static final RegistryObject<SimpleParticleType> SCANNER_UP = REGISTRY.register("scanner_up", () -> new SimpleParticleType(false));
}
