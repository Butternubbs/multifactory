package net.multifactory.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.multifactory.block.entity.ScannerBlockEntity;
import net.multifactory.init.MultifactoryModParticleTypes;

public class ScannerBottomClientDisplayRandomTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		BlockEntity entity = world.getBlockEntity(new BlockPos(x, y, z));
		if(entity instanceof ScannerBlockEntity){
			if(((ScannerBlockEntity) entity).getStructSize()[0] != 0)
				world.addParticle((SimpleParticleType) (MultifactoryModParticleTypes.SCANNER_UP.get()), (x + 0.5 + 0.2 * Math.random()), (y + 0.8), (z + 0.5 + 0.2 * Math.random()), 0, 1, 0);
		}
		
	}
}
