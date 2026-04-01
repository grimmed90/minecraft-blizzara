package com.example.mixin;

import com.example.IPlayerSkillData;
import com.example.SkillData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityMixin implements IPlayerSkillData {
    private SkillData skillData = new SkillData();

    @Override
    public SkillData getSkillData() {
        return this.skillData;
    }

    @Override
    public void setSkillData(SkillData skillData) {
        this.skillData = skillData;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    public void readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        ValueInput skillsInput = input.childOrEmpty("Skills");
        if (skillsInput != null) {
            this.skillData.readFrom(skillsInput);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    public void addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
        ValueOutput skillsOutput = output.child("Skills");
        this.skillData.writeTo(skillsOutput);
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    protected void jumpFromGround(CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayer player) {
            if (!player.isCreative()) {
                com.example.ExampleMod.addXpStatic(player, "Agility", 1.5);
                com.example.ExampleMod.updateMovementSpeedStatic(player);
            }
        }
    }

    @Inject(method = "attack", at = @At("HEAD"))
    public void onAttack(Entity target, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayer player) {
            if (player.isCreative()) return;
            int combatLevel = this.skillData.getCombatLevel();
            float critChance = combatLevel * 0.01f; // 1% per level
            if (player.getRandom().nextFloat() < critChance) {
                // Apply a quick bonus damage before standard attack resolves to mimic a crit
                float bonus = 2.0f + (combatLevel * 0.1f);
                target.hurtServer((net.minecraft.server.level.ServerLevel) player.level(), player.damageSources().playerAttack(player), bonus);
            }
        }
    }
}
