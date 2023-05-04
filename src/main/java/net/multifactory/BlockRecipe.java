package net.multifactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.multifactory.config.CommonConfig;
import net.multifactory.init.MultifactoryModBlocks;
import net.multifactory.block.entity.ScannerBlockEntity;

public class BlockRecipe {
    private String name;
    private Block[] blockTypes;
	private int[][][] blockData;
    private ItemStack outputItem;

    public BlockRecipe(String name, String filename, ItemStack item){
        this(name, getNBTFromFile(filename), item);
    }

    public BlockRecipe(String name, CompoundTag nbt, ItemStack item){
        this.name = name;
        outputItem = item;
        blockTypes = fillBlocks(nbt);
		blockData = fillBlockData(nbt);
    }

    public static ArrayList<BlockRecipe> getBlockRecipes(int[] size){
        ArrayList<BlockRecipe> blockRecipes = new ArrayList<>();
        for(String recipeStr : CommonConfig.RECIPES.get()){
            String shapeName = recipeStr.split(" -> ")[0];
            String itemName = recipeStr.split(" ")[2];
            int itemCount = Integer.parseInt(recipeStr.split(" ")[3]);
            BlockRecipe recipe = new BlockRecipe(shapeName, "../src/main/resources/data/multifactory/structures/" + shapeName + ".nbt", 
                    new ItemStack(RegistryObject.create(new ResourceLocation(itemName), ForgeRegistries.ITEMS).get(), itemCount));
            if(recipe.blockData.length == size[0] && recipe.blockData[0].length == size[1] - 2 && recipe.blockData[0][0].length == size[2]) blockRecipes.add(recipe);
        }
        return blockRecipes;
    }

    public static CompoundTag getNBTFromFile(String filename){
        File filetest = new File("..");
        System.out.println(filetest.getAbsolutePath());
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

    public static Block[] fillBlocks(CompoundTag nbt){
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
	public static int[][][] fillBlockData(CompoundTag nbt){
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

    public static boolean checkShape(LevelAccessor world, BlockPos pos){
		//Look at block entity data to get structure info
		Block pingedType = world.getBlockState(pos).getBlock();
		ScannerBlockEntity be;
		if(pingedType == MultifactoryModBlocks.SCANNER_BOTTOM.get() ||
		   pingedType == MultifactoryModBlocks.SCANNER_TOP.get()){
			be = (ScannerBlockEntity) world.getBlockEntity(pos);
			System.out.println("probably retrieved data from a scanner block");
		}
        else return false;
		//If the block is not part of a structure, return
		if(be.getActiveRecipe() == "") {System.out.println("No active recipe"); return false;}
		int[] bounds = be.getStructure();
		int[] size = be.getStructSize();

		//Get the recipe from a specified .nbt file
        BlockRecipe activeRecipe = null;
		ArrayList<BlockRecipe> recipes = getBlockRecipes(size);
        for(BlockRecipe recipe : recipes){
            if(recipe.getName().equals(be.getActiveRecipe())){
                activeRecipe = recipe;
                break;
            }
        }
		if(activeRecipe == null){
			System.out.println("No recipe found.");
			return false;
		}

		if(!validate(bounds, activeRecipe.getBlockTypes(), activeRecipe.getBlockData(), world)) return false;
		clearBoundary(bounds, world);
        be.insertItem(activeRecipe.outputItem);
		return true;
	}

	public static boolean validate(int[] bounds, Block[] blockTypes, int[][][] blockData, LevelAccessor world){
		//Compare the file data to the structure's bounded area. If it matches, good job!
		for(int x = bounds[0]; x <= bounds[3]; x++){
			for(int y = bounds[1] + 1; y < bounds[4]; y++){
				for(int z = bounds[2]; z <= bounds[5]; z++){
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

    public String getName(){
        return name;
    }

    public int[][][] getBlockData(){
        return blockData;
    }

    public Block[] getBlockTypes(){
        return blockTypes;
    }

    public static void main(String[] args){
    }
}
