package com.example;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SkillData {
    private double miningXp;
    private int miningLevel;
    private double woodcuttingXp;
    private int woodcuttingLevel;
    private double constitutionXp;
    private int constitutionLevel;

    public void addXP(String skill, double xp) {
        if ("Mining".equals(skill)) {
            this.miningXp += xp;
            this.miningLevel = calculateLevel(this.miningXp);
        } else if ("Woodcutting".equals(skill)) {
            this.woodcuttingXp += xp;
            this.woodcuttingLevel = calculateLevel(this.woodcuttingXp);
        } else if ("Constitution".equals(skill)) {
            this.constitutionXp += xp;
            this.constitutionLevel = calculateLevel(this.constitutionXp);
        }
    }

    private int calculateLevel(double xp) {
        int level = 0;
        double nextLevelXp = 100.0;
        double currentTotalXp = 0.0;

        while (xp >= currentTotalXp + nextLevelXp) {
            currentTotalXp += nextLevelXp;
            level++;
            nextLevelXp = 100.0 * Math.pow(1.15, level);
        }
        return level;
    }

    public void writeTo(ValueOutput output) {
        output.putDouble("MiningXp", miningXp);
        output.putInt("MiningLevel", miningLevel);
        output.putDouble("WoodcuttingXp", woodcuttingXp);
        output.putInt("WoodcuttingLevel", woodcuttingLevel);
        output.putDouble("ConstitutionXp", constitutionXp);
        output.putInt("ConstitutionLevel", constitutionLevel);
    }

    public void readFrom(ValueInput input) {
        this.miningXp = input.getDoubleOr("MiningXp", 0.0);
        this.miningLevel = input.getIntOr("MiningLevel", 0);
        this.woodcuttingXp = input.getDoubleOr("WoodcuttingXp", 0.0);
        this.woodcuttingLevel = input.getIntOr("WoodcuttingLevel", 0);
        this.constitutionXp = input.getDoubleOr("ConstitutionXp", 0.0);
        this.constitutionLevel = input.getIntOr("ConstitutionLevel", 0);
    }

    public double getMiningXp() { return miningXp; }
    public int getMiningLevel() { return miningLevel; }
    public double getWoodcuttingXp() { return woodcuttingXp; }
    public int getWoodcuttingLevel() { return woodcuttingLevel; }
    public double getConstitutionXp() { return constitutionXp; }
    public int getConstitutionLevel() { return constitutionLevel; }

    public int getTotalLevel() {
        return miningLevel + woodcuttingLevel + constitutionLevel;
    }
}
