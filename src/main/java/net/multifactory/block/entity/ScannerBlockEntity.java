package net.multifactory.block.entity;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.multifactory.MultiblockItemStackHandler;
import net.multifactory.ScannerMultiblock;
import net.multifactory.init.MultifactoryModBlockEntities;
import net.multifactory.screen.ScannerMenu;
import javax.annotation.Nullable;
import javax.swing.plaf.basic.BasicComboBoxUI.ItemHandler;

import org.jetbrains.annotations.NotNull;

public class ScannerBlockEntity extends BlockEntity implements MenuProvider {

    ScannerBlockEntity leader = this;

	private final MultiblockItemStackHandler itemHandler = new MultiblockItemStackHandler(this, 4);

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
	private int[] structure = new int[6]; //Stores origin and limit of structure area, [xo yo zo xl yl zl]
	private int[] size = new int[3]; //Stores size of structure

	public ScannerBlockEntity(BlockPos position, BlockState state) {
		super(MultifactoryModBlockEntities.SCANNER.get(), position, state);
        System.out.println("Created a new ScannerBlockEntity");
        this.data = new ContainerData(){
            @Override
            public int get(int index) {
                return switch (index) {
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                }
            }

            @Override
            public int getCount() {
                return 0;
            }
        };
	}

	@Override
	public Component getDisplayName() {
		return Component.literal("Scanner");
	}

	@Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        System.out.println("created menu?");
        return new ScannerMenu(pContainerId, pInventory, this, this.data);
    }

	@Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

	@Override
    public void onLoad() {
        super.onLoad();
        this.resetLeader();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }
	
	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		itemHandler.deserializeNBT(compound.getCompound("inventory"));
		structure = compound.getIntArray("structure");
		size = compound.getIntArray("structsize");
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.put("inventory", itemHandler.serializeNBT());
		compound.putIntArray("structure", structure);
		compound.putIntArray("structsize", size);
	}

	public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithFullMetadata();
	}

	public static void tick(Level pLevel, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        //if(hasRecipe(pBlockEntity) && hasNotReachedStackLimit(pBlockEntity)) {
        //    craftItem(pBlockEntity);
        //}
    }

    //private static void craftItem(ScannerBlockEntity entity) {
    //    entity.itemHandler.extractItem(0, 1, false);
    //    entity.itemHandler.extractItem(1, 1, false);
    //    entity.itemHandler.getStackInSlot(2).hurt(1, new Random(), null);
//
    //    entity.itemHandler.setStackInSlot(3, new ItemStack(ModItems.CITRINE.get(),
    //            entity.itemHandler.getStackInSlot(3).getCount() + 1));
    //}

    //private static boolean hasRecipe(ScannerBlockEntity entity) {
    //    boolean hasItemInWaterSlot = PotionUtils.getPotion(entity.itemHandler.getStackInSlot(0)) == Potions.WATER;
    //    boolean hasItemInFirstSlot = entity.itemHandler.getStackInSlot(1).getItem() == MultifactoryModItems.RAW_CITRINE.get();
    //    boolean hasItemInSecondSlot = entity.itemHandler.getStackInSlot(2).getItem() == MultifactoryModItems.GEM_CUTTER_TOOL.get();
//
    //    return hasItemInWaterSlot && hasItemInFirstSlot && hasItemInSecondSlot;
    //}

    //private static boolean hasNotReachedStackLimit(ScannerBlockEntity entity) {
    //    return entity.itemHandler.getStackInSlot(3).getCount() < entity.itemHandler.getStackInSlot(3).getMaxStackSize();
    //}

	public int[] getStructure(){
		return structure;
	}
	public void setStructure(int[] s){
		this.structure = s;
	}
	public int[] getStructSize(){
		return size;
	}
	public void setStructSize(int[] s){
		this.size = s;
	}

    //Set the leader (inventory to mimic) for this entity
    public void setLeader(ScannerBlockEntity sbe){
        leader = sbe;
    }
    public void resetLeader(){
        ScannerMultiblock.assembleMultiblock(this.getLevel(), this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ());
    }
    public void clearLeader(){
        leader = this;
    }
    public ScannerBlockEntity getLeader(){
        return leader;
    }
    public ItemStackHandler getItemHandler(){
        return itemHandler;
    }
}
