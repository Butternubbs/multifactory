package net.multifactory.screen;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ButtonList extends AbstractWidget {
    
    private ArrayList<ListButton> buttons;
    private final int numDisplayed;
    private int scrollAmt;

    public ButtonList(int x, int y, int width, int height, int numDisplayed){
        super(x, y, width, height, Component.literal(""));
        buttons = new ArrayList<ListButton>();
        this.numDisplayed = numDisplayed;
    }

    public void addButton(int x, int y, int width, int height, String text, OnPress onPress){

        buttons.add(new ListButton(this.x + x, this.y + y, width, height, Component.literal(text), onPress));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        for(ListButton button : buttons){
            button.visible = false;
        }
        for(int i = 0; i < buttons.size() && i < numDisplayed; i++){
            ListButton button = buttons.get(i + scrollAmt);
            button.visible = true;
            button.renderButton(poseStack, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        System.out.println("Clicked list: " + mouseX + " " + mouseY);
        int indexClicked = (int)((mouseY - this.y)/18) + scrollAmt;
        if(indexClicked < buttons.size())
            buttons.get((int)((mouseY - this.y)/18) + scrollAmt).onPress();
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {
        // TODO Auto-generated method stub
    }

    public void setScrollAmt(int scrollAmt){
        this.scrollAmt = scrollAmt;
        for(ListButton button : buttons){
            button.setScrollAmt(scrollAmt);
        }
    }

    public int size(){
        return buttons.size();
    }

    public int numDisplay(){
        return numDisplayed;
    }

    public ArrayList<ListButton> getButtons(){
        return buttons;
    }
}