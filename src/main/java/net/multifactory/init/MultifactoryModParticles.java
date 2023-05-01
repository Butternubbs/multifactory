
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.multifactory.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.multifactory.client.particle.ScannerUpParticle;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MultifactoryModParticles {
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.register(MultifactoryModParticleTypes.SCANNER_UP.get(), ScannerUpParticle::provider);
	}
}
