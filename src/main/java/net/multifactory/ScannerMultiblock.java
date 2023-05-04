package net.multifactory;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import java.lang.Math;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import net.multifactory.block.entity.ScannerBlockEntity;
import net.multifactory.init.MultifactoryModBlocks;
import net.multifactory.init.MultifactoryModParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraft.world.level.Level;

//Handles methods related to the Scanner multiblock, such as assembly and disassembly.
public class ScannerMultiblock {
	/*
	 * Given a position in the world, checks nearby to find a Scanner Multiblock.
	 * If successful, sets stucture data in each of the attached scanner block entities.
	 */
	public static void assembleMultiblock(LevelAccessor world, double x, double y, double z) {
		//Find the connected scanner block with the lowest x and z values, and save that in the origin object.
		double[] origin = new double[]{x, y, z};
		Block placedType = world.getBlockState(new BlockPos(x, y, z)).getBlock();
		int xSearch = 0, zSearch = 0;
		while (xSearch < 12 && zSearch < 12) {
			if ((world.getBlockState(new BlockPos(origin[0] - 1, origin[1], origin[2]))).getBlock() == placedType) {
				xSearch++;
				origin[0] -= 1;
				continue;
			}
			if ((world.getBlockState(new BlockPos(origin[0], origin[1], origin[2] - 1))).getBlock() == placedType) {
				zSearch++;
				origin[2] -= 1;
				continue;
			}
			break; //origin position has been determined
		}
		System.out.println("origin: " + origin[0] + " " + origin[2]);

		//Find the rect of scanner blocks which is connected to the block that was just altered.
		//i.e. if the last block was a bottom block, find all of the connected bottom blocks.
		double[] rectsize = findRect(origin, placedType, world);
		if(rectsize != null) System.out.println("Rect 1 bound: " + rectsize[0] + " " + rectsize[2]);
		else {System.out.println("First rect invalid"); return;}
		
		//Find the other half of the multiblock.
		//i.e. if the last block was a bottom block, find the rect of top blocks above the bottom blocks.
		int yDirection = 0;
		Block searchType = null; 
		if(placedType == MultifactoryModBlocks.SCANNER_BOTTOM.get()){
			yDirection = 1;
			searchType = MultifactoryModBlocks.SCANNER_TOP.get();
		}   
		else if(placedType == MultifactoryModBlocks.SCANNER_TOP.get()){
			yDirection = -1;
			searchType = MultifactoryModBlocks.SCANNER_BOTTOM.get();
		} 
		int i;
		boolean foundSide = false;
		for(i = 1; i < 12; i++){
			if ((world.getBlockState(new BlockPos(origin[0], origin[1] + (i * yDirection), origin[2]))).getBlock() == searchType) {
				foundSide = true; 
				break;
			}
		}
		if(!foundSide) return;

		double[] topsize = findRect(new double[]{origin[0], origin[1] + (i*yDirection), origin[2]}, searchType, world);

		if(!(topsize == null)) System.out.println("Rect 2 bound: " + topsize[0] + " " + topsize[2]);
		else {System.out.println("Second rect invalid"); return;}

		int width = (int)Math.abs(rectsize[0] - origin[0]) + 1;
		int height = (int)Math.abs(topsize[1] - origin[1]) + 1;
		int depth = (int)Math.abs(rectsize[2] - origin[2]) + 1;
		int[] minpoint = new int[]{(int)Math.min(origin[0], topsize[0]),
								   (int)Math.min(origin[1], topsize[1]),
								   (int)Math.min(origin[2], topsize[2])};
		int[] maxpoint = new int[]{(int)Math.max(origin[0], topsize[0]),
									(int)Math.max(origin[1], topsize[1]),
									(int)Math.max(origin[2], topsize[2])};

		if(topsize[0] == rectsize[0] && topsize[2] == rectsize[2]){
			if(isClear(minpoint, maxpoint, world))
				System.out.println("Valid Structure Found");
		}
		else {System.out.println("Top and bottom are not the same size"); return;}

		storeMultiblock(MultifactoryModBlocks.SCANNER_BOTTOM.get(), width, height, depth, minpoint, minpoint, maxpoint, world);
		storeMultiblock(MultifactoryModBlocks.SCANNER_TOP.get(), width, height, depth, new int[]{minpoint[0], maxpoint[1], minpoint[2]}, minpoint, maxpoint, world);
		
		spawnParticles(minpoint, maxpoint, world, MultifactoryModParticleTypes.SCANNER_UP.get());
		
	}


	public static double[] findRect(double[] origin, Block placedType, LevelAccessor world){
		double[] currPos = new double[]{origin[0], origin[1], origin[2]};
		int width = 0; //used to ensure consistent width across all rows
		boolean lastrow = false;
		for(int i = 0; i < 12; i++){
			currPos[0] = origin[0];
			
			for(int j = 0; j < 12; j++){
				if ((world.getBlockState(new BlockPos(currPos[0], currPos[1], currPos[2]))).getBlock() != placedType) { //found end of row, go to next row
					break;
				}

				if(i == 0){ //first row
					if ((world.getBlockState(new BlockPos(currPos[0], currPos[1], currPos[2] - 1))).getBlock() == placedType) { //invalid, block found above origin
						System.out.println("Shape is not rectangular, or the top rect does not match the bottom rect. (invalid -z)");
						return null;
					}
					width++;
				}

				if(j == 0){ //first column
					if ((world.getBlockState(new BlockPos(currPos[0] - 1, currPos[1], currPos[2]))).getBlock() == placedType) { //invalid, block found left of origin
						System.out.println("Shape is not rectangular, or the top rect does not match the bottom rect. (invalid -x)");
						return null;
					}
				}

				if ((world.getBlockState(new BlockPos(currPos[0], currPos[1], currPos[2] + 1))).getBlock() != placedType) { //no block found below, so this should be the last row
					if(j != 0 && !lastrow) return null; //inconsistent row width
					lastrow = true;
				}
				if ((world.getBlockState(new BlockPos(currPos[0], currPos[1], currPos[2] + 1))).getBlock() == placedType && lastrow) { //invalid, block found below supposed final row
					System.out.println("Shape is not rectangular, or the top rect does not match the bottom rect. (invalid +z)");
					return null;
				}

				
				currPos[0]++; // x++
			}
			currPos[2]++; // z++
			if(width != (currPos[0] - origin[0])) return null; //invalid, inconsistent row width
			if(lastrow) {
				currPos[0]--;
				currPos[2]--;
				return currPos;
			}
		}
		System.out.println("Shape is too wide. Limit should be 12 blocks");
		return null; //structure is too big!
	}


	public static boolean isClear(int[] min, int[] max, LevelAccessor world){
		System.out.println("Checking middle area... X: " + min[0] + "/" + max[0] + 
		                                          " Y: " + min[1] + "/" + max[1] + 
												  " Z: " + min[2] + "/" + max[2]);
		int blockCount = 0;
		for(int i = min[0]; i <= max[0]; i++){
			for(int j = min[1] + 1; j < max[1]; j++){
				for(int k = min[2]; k <= max[2]; k++){
					//Ensure that there are no overlapping scanner structures.
					if ((world.getBlockState(new BlockPos(i, j, k))).getBlock() == MultifactoryModBlocks.SCANNER_BOTTOM.get()) return false;
					if ((world.getBlockState(new BlockPos(i, j, k))).getBlock() == MultifactoryModBlocks.SCANNER_TOP.get()) return false;
					blockCount++;
				}
			}
		}
		System.out.println("Space valid. Bounds contain " + blockCount + " blocks");
		return true;
	}


	public static void storeMultiblock(Block placedType, int width, int height, int depth, int[] origin, int[] minPoint, int[] maxPoint, LevelAccessor world){
		int[] structureBounds = new int[]{minPoint[0], minPoint[1], minPoint[2], maxPoint[0], maxPoint[1], maxPoint[2]};
		int[] structSize = new int[]{width, height, depth};
		System.out.println("Storing: " + structureBounds.toString() + ", " + structSize.toString());
		for(int i = 0; i < width; i++){
			for(int j = 0; j < depth; j++){
				System.out.println("Storing structure data at: " + (origin[0] + i) + " " + origin[1] + " " + (origin[2] + j));
				if(placedType == MultifactoryModBlocks.SCANNER_BOTTOM.get() ||
				   placedType == MultifactoryModBlocks.SCANNER_TOP.get()){
					ScannerBlockEntity be = (ScannerBlockEntity) world.getBlockEntity(new BlockPos(origin[0] + i, origin[1], origin[2] + j));
					be.setStructure(structureBounds);
					be.setStructSize(structSize);
					be.setLeader((ScannerBlockEntity) world.getBlockEntity(new BlockPos(minPoint[0], minPoint[1], minPoint[2])));
					((Level) world).sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), Block.UPDATE_CLIENTS);
				}
			}
		}
	}

	public static void setRecipe(BlockPos changedPos, String recipeName, LevelAccessor world){
		ScannerBlockEntity changedEntity = (ScannerBlockEntity) world.getBlockEntity(changedPos);
		int[] structureBounds = changedEntity.getStructure();
		int[] structSize = changedEntity.getStructSize();
		for (BlockPos pos : BlockPos.betweenClosed(
			new BlockPos(structureBounds[0], structureBounds[1], structureBounds[2]),
			new BlockPos(structureBounds[3], structureBounds[1], structureBounds[5])
		)) { 
			ScannerBlockEntity be = (ScannerBlockEntity) world.getBlockEntity(pos);
			be.setActiveRecipe(recipeName);
			((Level) world).sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), Block.UPDATE_CLIENTS);
		}
		for (BlockPos pos : BlockPos.betweenClosed(
			new BlockPos(structureBounds[0], structureBounds[4], structureBounds[2]),
			new BlockPos(structureBounds[3], structureBounds[4], structureBounds[5])
		)) { 
			ScannerBlockEntity be = (ScannerBlockEntity) world.getBlockEntity(pos);
			be.setActiveRecipe(recipeName);
			((Level) world).sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), Block.UPDATE_CLIENTS);
		}
	}

	//Spawn particles for when the multiblock is broken or assembled
	@OnlyIn(Dist.CLIENT)
	public static void spawnParticles(int[] minpoint, int[] maxpoint, LevelAccessor world, SimpleParticleType particle){
		for (BlockPos pos : BlockPos.betweenClosed(
			new BlockPos(minpoint[0], minpoint[1], minpoint[2]),
			new BlockPos(maxpoint[0], minpoint[1], maxpoint[2])
		)) { 
			world.addParticle(particle, pos.getX(), pos.getY() + 2.0D, pos.getZ(), 0.0D, 0.0D, 0.0D);
		}
		for (BlockPos pos : BlockPos.betweenClosed(
			new BlockPos(minpoint[0], maxpoint[1], minpoint[2]),
			new BlockPos(maxpoint[0], maxpoint[1], maxpoint[2])
		)) { 
			world.addParticle(particle, pos.getX(), pos.getY() + 2.0D, pos.getZ(), 0.0D, 0.0D, 0.0D);
		}

	}

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event){
		if (event.getState().getBlock() == MultifactoryModBlocks.SCANNER_BOTTOM.get() || 
			event.getState().getBlock() == MultifactoryModBlocks.SCANNER_TOP.get()) {
			System.out.println("Broke a scanner block");
			BlockPos scannerPos = event.getPos();
			LevelAccessor world = event.getLevel();
			
			clearStructure(scannerPos, world);
		}
	}

	@SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        LevelAccessor world = event.getLevel();
        List<BlockPos> affectedBlocks = event.getAffectedBlocks();

        for (BlockPos pos : affectedBlocks) {
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() == MultifactoryModBlocks.SCANNER_BOTTOM.get() || 
				state.getBlock() == MultifactoryModBlocks.SCANNER_TOP.get()) {
                clearStructure(pos, world);
            }
        }
    }

	@SubscribeEvent
	public static void onBlockPlace(BlockEvent.EntityPlaceEvent event){
		if (event.getState().getBlock() == MultifactoryModBlocks.SCANNER_BOTTOM.get() || 
			event.getState().getBlock() == MultifactoryModBlocks.SCANNER_TOP.get()) {
			System.out.println("Placed a scanner block");
			BlockPos scannerPos = event.getPos();
			LevelAccessor world = event.getLevel();

			clearStructure(scannerPos.offset(-1,0,0), world);
			clearStructure(scannerPos.offset(1,0,0), world);
			clearStructure(scannerPos.offset(0,0,-1), world);
			clearStructure(scannerPos.offset(0,0,1), world);
			ScannerMultiblock.assembleMultiblock(world, scannerPos.getX(), scannerPos.getY(), scannerPos.getZ());
		}
	}

	public static void clearStructure(BlockPos blockPos, LevelAccessor world){
		// get the block entity at the scanner block position
		BlockEntity scannerEntity = world.getBlockEntity(blockPos);
		if (scannerEntity instanceof ScannerBlockEntity) {
			System.out.println("Found a scanner block entity");
			// get the size and bounds data
			int[] bounds = ((ScannerBlockEntity) scannerEntity).getStructure();
			// iterate through all scanner blocks in the multiblock
			for (BlockPos pos : BlockPos.betweenClosed(
			new BlockPos(bounds[0], bounds[1], bounds[2]),
			new BlockPos(bounds[3], bounds[1], bounds[5])
			)) { 
				System.out.println("Clearing data at: " + pos.toShortString());
				BlockEntity entity = world.getBlockEntity(pos);
				if(entity instanceof ScannerBlockEntity){
					((ScannerBlockEntity) entity).setStructure(new int[6]);
					((ScannerBlockEntity) entity).setStructSize(new int[]{0,0,0});
					((ScannerBlockEntity) entity).setActiveRecipe("");
					((ScannerBlockEntity) entity).clearLeader();
					((Level) world).sendBlockUpdated(pos, entity.getBlockState(), entity.getBlockState(), Block.UPDATE_CLIENTS);
				}
			}
			for (BlockPos pos : BlockPos.betweenClosed(
					new BlockPos(bounds[0], bounds[4], bounds[2]),
					new BlockPos(bounds[3], bounds[4], bounds[5])
			)) { 
				System.out.println("Clearing data at: " + pos.toShortString());
				BlockEntity entity = world.getBlockEntity(pos);
				if(entity instanceof ScannerBlockEntity){
					((ScannerBlockEntity) entity).setStructure(new int[6]);
					((ScannerBlockEntity) entity).setStructSize(new int[]{0,0,0});
					((ScannerBlockEntity) entity).setActiveRecipe("");
					((ScannerBlockEntity) entity).clearLeader();
					((Level) world).sendBlockUpdated(pos, entity.getBlockState(), entity.getBlockState(), Block.UPDATE_CLIENTS);
				}
			}
		}
	}
}
