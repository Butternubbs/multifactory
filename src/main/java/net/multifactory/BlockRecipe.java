package net.multifactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.multifactory.config.CommonConfig;

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

    public String getName(){
        return name;
    }

    public static void main(String[] args){
        File file = new File(".");
        System.out.println(file.getAbsolutePath());
    }
}
