package com.aurarpg;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
public class SkillsScreen extends Screen {
    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/gui/skills.png");
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
        // Then super
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        // Draw background first
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        guiGraphics.centeredText(this.font, this.title, x + this.imageWidth / 2, y + 6, 4210752);

        int startXLeft = x + 10;
        int startXRight = x + this.imageWidth / 2 + 5;
        int startY = y + 20;
        int yOffsetLeft = 0;
        int yOffsetRight = 0;

        if (this.payload == null) {
            guiGraphics.text(this.font, "Loading...", startXLeft, startY, 0xFFFFFF);
            return;
        }

        // Draw Left Column Skills
        yOffsetLeft += drawSkill(guiGraphics, "Mining", payload.miningLevel(), payload.miningXp(), startXLeft, startY + yOffsetLeft);
        yOffsetLeft += drawSkill(guiGraphics, "Woodcutting", payload.woodcuttingLevel(), payload.woodcuttingXp(), startXLeft, startY + yOffsetLeft);
        yOffsetLeft += drawSkill(guiGraphics, "Excavation", payload.excavationLevel(), payload.excavationXp(), startXLeft, startY + yOffsetLeft);
        yOffsetLeft += drawSkill(guiGraphics, "Fishing", payload.fishingLevel(), payload.fishingXp(), startXLeft, startY + yOffsetLeft);

        // Draw Right Column Skills
        yOffsetRight += drawSkill(guiGraphics, "Combat", payload.combatLevel(), payload.combatXp(), startXRight, startY + yOffsetRight);
        yOffsetRight += drawSkill(guiGraphics, "Defense", payload.defenseLevel(), payload.defenseXp(), startXRight, startY + yOffsetRight);
        yOffsetRight += drawSkill(guiGraphics, "Constitution", payload.constitutionLevel(), payload.constitutionXp(), startXRight, startY + yOffsetRight);
        yOffsetRight += drawSkill(guiGraphics, "Agility", payload.agilityLevel(), payload.agilityXp(), startXRight, startY + yOffsetRight);

        int textY = startY + Math.max(yOffsetLeft, yOffsetRight) + 4;

        int totalLevel = payload.miningLevel() + payload.woodcuttingLevel() + payload.constitutionLevel() +
                         payload.excavationLevel() + payload.fishingLevel() + payload.combatLevel() +
                         payload.defenseLevel() + payload.agilityLevel();
        double auraRadius = 1.0 + (totalLevel * 0.1);
        guiGraphics.text(this.font, "Total Level: " + totalLevel, startXLeft, textY, 0xFFD700); // Gold
        textY += 12;
        guiGraphics.text(this.font, "Aura Radius: " + String.format("%.1f", auraRadius), startXLeft, textY, 0x00FFFF); // Cyan
    }

    private int drawSkill(GuiGraphicsExtractor guiGraphics, String name, int level, double totalXp, int startX, int startY) {
        double currentTotalXp = 0.0;
        for (int i = 0; i < level; i++) {
            currentTotalXp += 100.0 * Math.pow(1.15, i);
        }
        double nextLevelXp = 100.0 * Math.pow(1.15, level);
        double xpInCurrentLevel = totalXp - currentTotalXp;
        if (xpInCurrentLevel < 0) xpInCurrentLevel = 0;

        String text = "[*] " + name + ": Lvl " + level;
        guiGraphics.text(this.font, text, startX, startY, 0xFFFFFF);
        guiGraphics.text(this.font, "XP: " + (int)xpInCurrentLevel + "/" + (int)nextLevelXp, startX + 10, startY + 10, 0xAAAAAA);

        int barWidth = 60;
        int barHeight = 4;
        int barX = startX + 10;
        int barY = startY + 20;
        double progress = Math.min(1.0, xpInCurrentLevel / nextLevelXp);
        int filledWidth = (int)(barWidth * progress);

        // Draw background bar (dark grey)
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF555555);
        // Draw filled bar (green)
        guiGraphics.fill(barX, barY, barX + filledWidth, barY + barHeight, 0xFF55FF55);

        return 28; // Return amount to shift Y down for next skill
    }
}
