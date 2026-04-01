package com.example;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SkillData {
    private int miningXp;
    private int miningLevel;
    private int woodcuttingXp;
    private int woodcuttingLevel;

    public void addXP(String skill, int xp) {
        if ("Mining".equals(skill)) {
            this.miningXp += xp;
            this.miningLevel = calculateLevel(this.miningXp);
        } else if ("Woodcutting".equals(skill)) {
            this.woodcuttingXp += xp;
            this.woodcuttingLevel = calculateLevel(this.woodcuttingXp);
        }
    }

    private int calculateLevel(int xp) {
        if (xp >= 250) {
            return 2;
        } else if (xp >= 100) {
            return 1;
        }
        return 0;
    }

    public void writeTo(ValueOutput output) {
        output.putInt("MiningXp", miningXp);
        output.putInt("MiningLevel", miningLevel);
        output.putInt("WoodcuttingXp", woodcuttingXp);
        output.putInt("WoodcuttingLevel", woodcuttingLevel);
    }

    public void readFrom(ValueInput input) {
        this.miningXp = input.getIntOr("MiningXp", 0);
        this.miningLevel = input.getIntOr("MiningLevel", 0);
        this.woodcuttingXp = input.getIntOr("WoodcuttingXp", 0);
        this.woodcuttingLevel = input.getIntOr("WoodcuttingLevel", 0);
    }

    public int getMiningXp() { return miningXp; }
    public int getMiningLevel() { return miningLevel; }
    public int getWoodcuttingXp() { return woodcuttingXp; }
    public int getWoodcuttingLevel() { return woodcuttingLevel; }
}
