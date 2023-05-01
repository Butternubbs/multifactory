
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.multifactory.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import net.multifactory.block.ScannerTopBlock;
import net.multifactory.block.ScannerBottomBlock;
import net.multifactory.MultifactoryMod;

public class MultifactoryModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, MultifactoryMod.MODID);
	public static final RegistryObject<Block> SCANNER_TOP = REGISTRY.register("scanner_top", () -> new ScannerTopBlock());
	public static final RegistryObject<Block> SCANNER_BOTTOM = REGISTRY.register("scanner_bottom", () -> new ScannerBottomBlock());
}
