
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.multifactory.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.BlockItem;

import net.multifactory.MultifactoryMod;

public class MultifactoryModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MultifactoryMod.MODID);
	public static final RegistryObject<Item> SCANNER_TOP = block(MultifactoryModBlocks.SCANNER_TOP, CreativeModeTab.TAB_BUILDING_BLOCKS);
	public static final RegistryObject<Item> SCANNER_BOTTOM = block(MultifactoryModBlocks.SCANNER_BOTTOM, CreativeModeTab.TAB_REDSTONE);

	private static RegistryObject<Item> block(RegistryObject<Block> block, CreativeModeTab tab) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
	}
}
