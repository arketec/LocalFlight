package dev.arketec.localflight.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.arketec.localflight.LocalFlight;
import dev.arketec.localflight.blocks.entity.BlockEntityFlightController;
import dev.arketec.localflight.containers.ControllerContainer;
import dev.arketec.localflight.network.PacketScreenButtonPress;
import dev.arketec.localflight.registration.ModPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.net.NetworkInterface;

public class ControllerScreen extends AbstractContainerScreen<ControllerContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(LocalFlight.MODID, "textures/gui/controller.png");
    public ControllerScreen(ControllerContainer container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        leftPos = 0;
        topPos = 0;
        imageWidth = 156;
        imageHeight = 166;
    }

    @Override
    protected void renderBg(PoseStack stack, float mouseX, int mouseY, int ticks) {
        renderBackground(stack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(stack, leftPos, topPos, 0,0, imageWidth, imageHeight);
    }
    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float ticks) {
        super.render(stack, mouseX, mouseY, ticks);
        font.draw(stack, title, leftPos + 8, topPos + 3, 0x404040);
        font.draw(stack, playerInventoryTitle, leftPos + 8, topPos + 80, 0x404040);
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {

    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new ExtendedButton(imageWidth - 10, imageHeight - 3, 4, 4, new TextComponent("ON"), btn -> {
            ModPackets.INSTANCE.sendToServer(new PacketScreenButtonPress(menu.getPos()));
        }));
    }
}
