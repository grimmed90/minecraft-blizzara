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
    private double excavationXp;
    private int excavationLevel;
    private double fishingXp;
    private int fishingLevel;
    private double combatXp;
    private int combatLevel;
    private double defenseXp;
    private int defenseLevel;
    private double agilityXp;
    private int agilityLevel;

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
        } else if ("Excavation".equals(skill)) {
            this.excavationXp += xp;
            int newLevel = calculateLevel(this.excavationXp);
            if (newLevel > this.excavationLevel) {
                leveledUp = true;
                this.excavationLevel = newLevel;
            }
        } else if ("Fishing".equals(skill)) {
            this.fishingXp += xp;
            int newLevel = calculateLevel(this.fishingXp);
            if (newLevel > this.fishingLevel) {
                leveledUp = true;
                this.fishingLevel = newLevel;
            }
        } else if ("Combat".equals(skill)) {
            this.combatXp += xp;
            int newLevel = calculateLevel(this.combatXp);
            if (newLevel > this.combatLevel) {
                leveledUp = true;
                this.combatLevel = newLevel;
            }
        } else if ("Defense".equals(skill)) {
            this.defenseXp += xp;
            int newLevel = calculateLevel(this.defenseXp);
            if (newLevel > this.defenseLevel) {
                leveledUp = true;
                this.defenseLevel = newLevel;
            }
        } else if ("Agility".equals(skill)) {
            this.agilityXp += xp;
            int newLevel = calculateLevel(this.agilityXp);
            if (newLevel > this.agilityLevel) {
                leveledUp = true;
                this.agilityLevel = newLevel;
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
        output.putDouble("ExcavationXp", excavationXp);
        output.putInt("ExcavationLevel", excavationLevel);
        output.putDouble("FishingXp", fishingXp);
        output.putInt("FishingLevel", fishingLevel);
        output.putDouble("CombatXp", combatXp);
        output.putInt("CombatLevel", combatLevel);
        output.putDouble("DefenseXp", defenseXp);
        output.putInt("DefenseLevel", defenseLevel);
        output.putDouble("AgilityXp", agilityXp);
        output.putInt("AgilityLevel", agilityLevel);
    }

    public void readFrom(ValueInput input) {
        this.miningXp = input.getDoubleOr("MiningXp", 0.0);
        this.miningLevel = input.getIntOr("MiningLevel", 0);
        this.woodcuttingXp = input.getDoubleOr("WoodcuttingXp", 0.0);
        this.woodcuttingLevel = input.getIntOr("WoodcuttingLevel", 0);
        this.constitutionXp = input.getDoubleOr("ConstitutionXp", 0.0);
        this.constitutionLevel = input.getIntOr("ConstitutionLevel", 0);
        this.excavationXp = input.getDoubleOr("ExcavationXp", 0.0);
        this.excavationLevel = input.getIntOr("ExcavationLevel", 0);
        this.fishingXp = input.getDoubleOr("FishingXp", 0.0);
        this.fishingLevel = input.getIntOr("FishingLevel", 0);
        this.combatXp = input.getDoubleOr("CombatXp", 0.0);
        this.combatLevel = input.getIntOr("CombatLevel", 0);
        this.defenseXp = input.getDoubleOr("DefenseXp", 0.0);
        this.defenseLevel = input.getIntOr("DefenseLevel", 0);
        this.agilityXp = input.getDoubleOr("AgilityXp", 0.0);
        this.agilityLevel = input.getIntOr("AgilityLevel", 0);
    }

    public double getMiningXp() { return miningXp; }
    public int getMiningLevel() { return miningLevel; }
    public double getWoodcuttingXp() { return woodcuttingXp; }
    public int getWoodcuttingLevel() { return woodcuttingLevel; }
    public double getConstitutionXp() { return constitutionXp; }
    public int getConstitutionLevel() { return constitutionLevel; }
    public double getExcavationXp() { return excavationXp; }
    public int getExcavationLevel() { return excavationLevel; }
    public double getFishingXp() { return fishingXp; }
    public int getFishingLevel() { return fishingLevel; }
    public double getCombatXp() { return combatXp; }
    public int getCombatLevel() { return combatLevel; }
    public double getDefenseXp() { return defenseXp; }
    public int getDefenseLevel() { return defenseLevel; }
    public double getAgilityXp() { return agilityXp; }
    public int getAgilityLevel() { return agilityLevel; }

    public int getTotalLevel() {
        return miningLevel + woodcuttingLevel + constitutionLevel + excavationLevel + fishingLevel + combatLevel + defenseLevel + agilityLevel;
    }
}
