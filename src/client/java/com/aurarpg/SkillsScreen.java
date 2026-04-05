package com.aurarpg;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SkillsScreen extends Screen {
    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/gui/skills_background.png");
    private final SkillUpdatePayload payload;
    private final int imageWidth = 176;
    private final int imageHeight = 166;

    public SkillsScreen(SkillUpdatePayload payload) {
        super(Component.literal("Skills"));
        this.payload = payload;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Custom background
        guiGraphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        guiGraphics.centeredText(this.font, this.title, x + this.imageWidth / 2, y + 6, 4210752);

        int textX = x + 10;
        int textY = y + 20;
        int spacing = 12;

        guiGraphics.text(this.font, "Mining: Lvl " + payload.miningLevel() + " (" + (int)payload.miningXp() + " XP)", textX, textY, 0xFFFFFF);
        textY += spacing;
        guiGraphics.text(this.font, "Woodcutting: Lvl " + payload.woodcuttingLevel() + " (" + (int)payload.woodcuttingXp() + " XP)", textX, textY, 0xFFFFFF);
        textY += spacing;
        guiGraphics.text(this.font, "Constitution: Lvl " + payload.constitutionLevel() + " (" + (int)payload.constitutionXp() + " XP)", textX, textY, 0xFFFFFF);
        textY += spacing;
        guiGraphics.text(this.font, "Excavation: Lvl " + payload.excavationLevel() + " (" + (int)payload.excavationXp() + " XP)", textX, textY, 0xFFFFFF);
        textY += spacing;
        guiGraphics.text(this.font, "Fishing: Lvl " + payload.fishingLevel() + " (" + (int)payload.fishingXp() + " XP)", textX, textY, 0xFFFFFF);
        textY += spacing;
        guiGraphics.text(this.font, "Combat: Lvl " + payload.combatLevel() + " (" + (int)payload.combatXp() + " XP)", textX, textY, 0xFFFFFF);
        textY += spacing;
        guiGraphics.text(this.font, "Defense: Lvl " + payload.defenseLevel() + " (" + (int)payload.defenseXp() + " XP)", textX, textY, 0xFFFFFF);
        textY += spacing;
        guiGraphics.text(this.font, "Agility: Lvl " + payload.agilityLevel() + " (" + (int)payload.agilityXp() + " XP)", textX, textY, 0xFFFFFF);
        textY += spacing + 4;

        int totalLevel = payload.miningLevel() + payload.woodcuttingLevel() + payload.constitutionLevel() +
                         payload.excavationLevel() + payload.fishingLevel() + payload.combatLevel() +
                         payload.defenseLevel() + payload.agilityLevel();
        double auraRadius = 1.0 + (totalLevel * 0.1);
        guiGraphics.text(this.font, "Total Level: " + totalLevel, textX, textY, 0xFFD700); // Gold
        textY += spacing;
        guiGraphics.text(this.font, "Aura Radius: " + String.format("%.1f", auraRadius), textX, textY, 0x00FFFF); // Cyan
    }
}
