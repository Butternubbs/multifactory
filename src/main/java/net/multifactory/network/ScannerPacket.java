package net.multifactory.network;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.multifactory.ScannerMultiblock;
import net.multifactory.block.entity.ScannerBlockEntity;

import java.nio.charset.Charset;
import java.util.function.Supplier;

import com.google.common.base.Utf8;
import com.ibm.icu.text.UTF16;

public class ScannerPacket {

    private BlockPos pos;
    private String recipeName;

    public ScannerPacket() {

    }

    public ScannerPacket(FriendlyByteBuf buf) {
        System.out.println("buffer: " + buf.toString());
    }

    public ScannerPacket(BlockPos pos, String recipeName) {
        this.pos = pos;
        this.recipeName = recipeName;
    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public static void encode(ScannerPacket message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.getPos().getX());
        buffer.writeInt(message.getPos().getY());
        buffer.writeInt(message.getPos().getZ());
        buffer.writeByteArray(message.getRecipeName().getBytes());
	}
	
	public static ScannerPacket decode(FriendlyByteBuf buffer) {
		return new ScannerPacket(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()), buffer.readUtf());
	}

    public BlockPos getPos(){
        return pos;
    }

    public String getRecipeName(){
        return recipeName;
    }

    public static boolean handle(ScannerPacket message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ScannerMultiblock.setRecipe(message.getPos(), message.getRecipeName(), context.getSender().getLevel());
        });
        return true;
    }
}