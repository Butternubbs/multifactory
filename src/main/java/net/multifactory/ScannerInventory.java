//package net.multifactory;
//import net.minecraft.world.Container;
//import net.minecraft.world.SimpleContainer;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.multifactory.init.MultifactoryModBlockEntities;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//
//public class ScannerInventory extends  implements Container {
//    private SimpleContainer container = new SimpleContainer(9);
//
//    public ScannerInventory(Level world) {
//        super(MultifactoryModBlockEntities.SCANNER.get(), world);
//        this.inventory = new SimpleInventory(9); // create a 27 slot inventory
//    }
//
//    @Override
//    public void clearContent() {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'clearContent'");
//    }
//
//    @Override
//    public int getContainerSize() {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'getContainerSize'");
//    }
//
//    @Override
//    public boolean isEmpty() {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'isEmpty'");
//    }
//
//    @Override
//    public ItemStack getItem(int p_18941_) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'getItem'");
//    }
//
//    @Override
//    public ItemStack removeItem(int p_18942_, int p_18943_) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'removeItem'");
//    }
//
//    @Override
//    public ItemStack removeItemNoUpdate(int p_18951_) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'removeItemNoUpdate'");
//    }
//
//    @Override
//    public void setItem(int p_18944_, ItemStack p_18945_) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'setItem'");
//    }
//
//    @Override
//    public boolean stillValid(Player p_18946_) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'stillValid'");
//    }
//
//}