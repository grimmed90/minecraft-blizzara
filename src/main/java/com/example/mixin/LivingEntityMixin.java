package com.example.mixin;

import com.example.IPlayerSkillData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float modifyFallDistance(float fallDistance) {
        if ((Object) this instanceof ServerPlayer player) {
            if (player instanceof IPlayerSkillData skillDataPlayer) {
                int agilityLevel = skillDataPlayer.getSkillData().getAgilityLevel();
                // Reduce fall distance by 0.1 per agility level
                return (float) Math.max(0.0, fallDistance - (agilityLevel * 0.1));
            }
        }
        return fallDistance;
    }

    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float modifyDamageReceived(float amount) {
        if ((Object) this instanceof ServerPlayer player) {
            if (player instanceof IPlayerSkillData skillDataPlayer) {
                int defenseLevel = skillDataPlayer.getSkillData().getDefenseLevel();
                // E.g., 0.5% damage reduction per level, max 80%
                float reduction = Math.min(0.80f, defenseLevel * 0.005f);
                return amount * (1.0f - reduction);
            }
        }
        return amount;
    }
}
