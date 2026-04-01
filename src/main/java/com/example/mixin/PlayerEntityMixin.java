package com.example.mixin;

import com.example.IPlayerSkillData;
import com.example.SkillData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
}
