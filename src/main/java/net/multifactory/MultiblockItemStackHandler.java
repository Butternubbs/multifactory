package net.multifactory;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.multifactory.block.entity.ScannerBlockEntity;

public class MultiblockItemStackHandler extends ItemStackHandler {

    private ScannerBlockEntity owner;

    public MultiblockItemStackHandler(ScannerBlockEntity owner, int size){
        super(size);
        this.owner = owner;
    }

    @Override
    protected void onContentsChanged(int slot) {
        owner.setChanged();
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate){
        ScannerBlockEntity leader = owner.getLeader();
        if(leader.equals(owner)) return super.insertItem(slot, stack, simulate);
        else return leader.getItemHandler().insertItem(slot, stack, simulate);
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate){
        ScannerBlockEntity leader = owner.getLeader();
        if(leader.equals(owner)) return super.extractItem(slot, amount, simulate);
        else return leader.getItemHandler().extractItem(slot, amount, simulate);
    }
    
}
