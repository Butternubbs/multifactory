package net.multifactory.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.multifactory.MultifactoryMod;
import net.multifactory.screen.ScrollButton.IScrollListener;

public class ScannerScreen extends AbstractContainerScreen<ScannerMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MultifactoryMod.MODID,"textures/gui/scanner_gui2.png");
    ScrollButton scrollButton;
    ButtonList buttonList;

    public ScannerScreen(ScannerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        //int numbuttons = 5;
        //buttons = new Button[numbuttons];
        //for(int i = 0; i < numbuttons; i++){
        //    buttons[i] = new Button(17, 21, 84, 16, Component.literal("button" + i), new Button.OnPress() {
        //        public void onPress(Button button){
        //            System.out.println("button clicked");
        //        }
        //    });
        //}
        //scrollButton = new ScrollButton(106, 19, 12, 70, TEXTURE, 173, 1, 12, 15, 1, true, 1, null);
    }

    @Override
    protected void init() {
        this.imageWidth = 176;
        this.imageHeight = 192;
        this.titleLabelY = 7;
        this.inventoryLabelY = 95;
        super.init();

        buttonList = new ButtonList(19+this.leftPos, 18+this.topPos, 86, 70, 4);
        buttonList.addButton(0, 0, 86, 18, "Button 1", new Button.OnPress() {
            @Override
            public void onPress(Button p_93751_) {
                System.out.println("Pressed button 1");
            }
        });
        buttonList.addButton(0, 18, 86, 18, "Button 2", new Button.OnPress() {
            @Override
            public void onPress(Button p_93751_) {
                System.out.println("Pressed button 2");
            }
        });
        buttonList.addButton(0, 36, 86, 18, "Button 3", new Button.OnPress() {
            @Override
            public void onPress(Button p_93751_) {
                System.out.println("Pressed button 3");
            }
        });
        buttonList.addButton(0, 54, 86, 18, "Button 4", new Button.OnPress() {
            @Override
            public void onPress(Button p_93751_) {
                System.out.println("Pressed button 3");
            }
        });
        buttonList.addButton(0, 72, 86, 18, "Button 5", new Button.OnPress() {
            @Override
            public void onPress(Button p_93751_) {
                System.out.println("Pressed button 3");
            }
        });
        buttonList.addButton(0, 90, 86, 18, "Button 6", new Button.OnPress() {
            @Override
            public void onPress(Button p_93751_) {
                System.out.println("Pressed button 3");
            }
        });
        this.addRenderableWidget(buttonList);

        scrollButton = new ScrollButton(106+this.leftPos, 19+this.topPos, 12, 70, TEXTURE, 176, 0, 12, 15, 1, true, 0.1F, new IScrollListener() {

            @Override
            public void onScroll(ScrollButton button, float percent) {
                buttonList.setScrollAmt((int)(button.scrollPercent * (buttonList.size() - buttonList.numDisplay() + 0.999)));
            }
            
        });
        this.addRenderableWidget(scrollButton);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        scrollButton.dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}