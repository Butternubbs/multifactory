/**
 * The code of this mod element is always locked.
 *
 * You can register new events in this class too.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser -> New... and make sure to make the class
 * outside net.multifactory as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 *
 * This class will be added in the mod root package.
*/
package net.multifactory;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;

import java.io.*;

import net.multifactory.block.entity.ScannerBlockEntity;
import net.multifactory.init.MultifactoryModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TestBoundingBox {
	public static CompoundTag getNBTFromFile(String filename){
		File file = new File(filename);
		CompoundTag nbt = null;
		try {
			nbt = NbtIo.readCompressed(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block, shhh don't tell anyone
			e.printStackTrace();
		}
		return nbt;
	}

	public static boolean checkShape(LevelAccessor world, BlockPos pos){
		//Look at block entity data to get structure info
		Block pingedType = world.getBlockState(pos).getBlock();
		BlockEntity be;
		CompoundTag nbt = new CompoundTag();
		if(pingedType == MultifactoryModBlocks.SCANNER_BOTTOM.get() ||
		   pingedType == MultifactoryModBlocks.SCANNER_TOP.get()){
			be = world.getBlockEntity(pos);
			((ScannerBlockEntity) be).saveAdditional(nbt);
			System.out.println("probably retrieved data from a scanner block");
		}
		//If the block is not part of a structure, return
		if(!nbt.contains("structure")) {System.out.println("Error: no data found in scanner block"); return false;}
		int[] bounds = nbt.getIntArray("structure");
		int[] size = nbt.getIntArray("structsize");

		//Get the recipe from a specified .nbt file
		CompoundTag recipe = getNBTFromFile("C:/Users/Butternubbs/MCreatorWorkspaces/multifactory/src/main/resources/data/multifactory/structures/hopper2.nbt");
		Block[] blockTypes = getBlocks(recipe);
		int[][][] blockData = getBlockData(recipe);

		if(blockData == null){
			System.out.println("No recipe found.");
			return false;
		}

		//Make sure that the structure is the right size for the recipe
		if(size[0] != blockData.length || size[1] != blockData[0].length + 2 || size[2] != blockData[0][0].length){
			System.out.println("Recipe and multiblock are not the same size");
			return false;
		}

		if(!validate(bounds, blockTypes, blockData, world)) return false;
		
		clearBoundary(bounds, world);
		return true;
	}

	public static boolean validate(int[] bounds, Block[] blockTypes, int[][][] blockData, LevelAccessor world){
		//Compare the file data to the structure's bounded area. If it matches, good job!
		for(int x = bounds[0]; x <= bounds[3]; x++){
			for(int y = bounds[1] + 1; y < bounds[4]; y++){
				for(int z = bounds[2]; z <= bounds[5]; z++){
					//System.out.println("Looking at: " + x + " " + y + " " + z);
					//System.out.println("Found block: " + world.getBlockState(new BlockPos(x, y, z)).getBlock().getDescriptionId());
					//System.out.println("Should be: " + blockTypes[blockData[x-bounds[0]][y-bounds[1]-1][z-bounds[2]]].getDescriptionId());
					if((world.getBlockState(new BlockPos(x, y, z))).getBlock() != blockTypes[blockData[x-bounds[0]][y-bounds[1]-1][z-bounds[2]]]) {
						System.out.println("Input incorrect");
						return false;
					}
				}
			}
		}
		System.out.println("Input is correct");
		return true;
	}

	//Clears the space inside the multiblock
	public static void clearBoundary(int[] bounds, LevelAccessor world){
		for(int x = bounds[0]; x <= bounds[3]; x++){
			for(int y = bounds[1] + 1; y < bounds[4]; y++){
				for(int z = bounds[2]; z <= bounds[5]; z++){
					System.out.println("Clearing: " + x + " " + y + " " + z);
					world.destroyBlock(new BlockPos(x, y, z), false);
				}
			}
		}
	}

	public static Block[] getBlocks(CompoundTag nbt){
		ListTag palette = (ListTag) nbt.get("palette");
		Block[] blocks = new Block[palette.size()];
		for(int i = 0; i < palette.size(); i++){
			CompoundTag block = (CompoundTag) palette.get(i);
			String str = block.getString("Name");
			//System.out.println(str);
			blocks[i] = RegistryObject.create(new ResourceLocation(str), ForgeRegistries.BLOCKS).get();
		}
		return blocks;
	}

	//Return 3D array representing structure, indexed [x][y][z].
	public static int[][][] getBlockData(CompoundTag nbt){
		ListTag size = (ListTag) nbt.get("size");
		int[][][] blockData = new int[size.getInt(0)][size.getInt(1)][size.getInt(2)];
		ListTag blocks = (ListTag) nbt.get("blocks");
		for(int i = 0; i < blocks.size(); i++){
			CompoundTag block = (CompoundTag) blocks.get(i);
			ListTag pos = (ListTag) block.get("pos");
			blockData[pos.getInt(0)][pos.getInt(1)][pos.getInt(2)] = block.getInt("state");
		}
		return blockData;
	}

	//for testing only
	//public static void main(String[] args){
	//	int[] yuh = new int[6];
	//	for(int b : yuh) System.out.println(b);
	//}

	//@SubscribeEvent
	//public static void init(FMLCommonSetupEvent event) {
	//	new TestBoundingBox();
	//}
//
	//@Mod.EventBusSubscriber
	//private static class ForgeBusEvents {
	//	@SubscribeEvent
	//	public static void serverLoad(ServerStartingEvent event) {
	//	}
//
	//	@OnlyIn(Dist.CLIENT)
	//	@SubscribeEvent
	//	public static void clientLoad(FMLClientSetupEvent event) {
	//	}
	//}
}
