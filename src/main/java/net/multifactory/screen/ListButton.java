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

    @Override
    public void render(PoseStack p_93657_, int p_93658_, int p_93659_, float p_93660_) {
        //Do nothing. The ButtonList will handle this
    }
}
