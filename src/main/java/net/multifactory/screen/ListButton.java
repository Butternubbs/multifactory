package net.multifactory.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ListButton extends Button {

    private final int baseY;
    private int scrollAmt;
    
    public ListButton(int x, int y, int width, int height, Component text, OnPress onPress){
        super(x, y, width, height, text, onPress);
        baseY = y;
        System.out.println("listbutton created");
    }

    public void setScrollAmt(int scroll){
        this.scrollAmt = scroll;
        this.y = baseY - (this.getHeight() * scrollAmt);
    }
}
