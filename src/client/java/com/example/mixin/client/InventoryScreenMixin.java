package com.example.mixin.client;

import com.example.OpenSkillsMenuPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu> {

    private static final Identifier SKILL_BUTTON_TEXTURE = Identifier.fromNamespaceAndPath("modid", "textures/gui/skill_button.png");

    public InventoryScreenMixin(InventoryMenu menu, net.minecraft.world.entity.player.Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void initSkillsButton(CallbackInfo ci) {
        // Position relative to the recipe book button
        int x = this.leftPos + 104;
        int y = this.topPos + 61; // Below the recipe book usually

        this.addRenderableWidget(new ImageButton(
            x, y, 20, 18,
            new net.minecraft.client.gui.components.WidgetSprites(SKILL_BUTTON_TEXTURE, SKILL_BUTTON_TEXTURE),
            (button) -> {
                ClientPlayNetworking.send(new OpenSkillsMenuPayload());
            },
            Component.literal("Skills")
        ));
    }
}
