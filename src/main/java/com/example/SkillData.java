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

    public boolean addXP(String skill, double xp) {
        boolean leveledUp = false;
        if ("Mining".equals(skill)) {
            this.miningXp += xp;
            int newLevel = calculateLevel(this.miningXp);
            if (newLevel > this.miningLevel) {
                leveledUp = true;
                this.miningLevel = newLevel;
            }
        } else if ("Woodcutting".equals(skill)) {
            this.woodcuttingXp += xp;
            int newLevel = calculateLevel(this.woodcuttingXp);
            if (newLevel > this.woodcuttingLevel) {
                leveledUp = true;
                this.woodcuttingLevel = newLevel;
            }
        } else if ("Constitution".equals(skill)) {
            this.constitutionXp += xp;
            int newLevel = calculateLevel(this.constitutionXp);
            if (newLevel > this.constitutionLevel) {
                leveledUp = true;
                this.constitutionLevel = newLevel;
            }
        }
        return leveledUp;
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
