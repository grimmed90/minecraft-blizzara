package com.aurarpg.mixin;

import com.aurarpg.IPlayerSkillData;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
    @Shadow private int timeUntilLured;
    @Shadow private int nibble;
    @Shadow private Entity hookedIn;
    @Shadow public abstract Player getPlayerOwner();

    @Inject(method = "tick", at = @At("TAIL"))
    private void accelerateFishing(CallbackInfo ci) {
        if (this.timeUntilLured > 0) {
            Player owner = this.getPlayerOwner();
            if (owner instanceof ServerPlayer serverPlayer && !serverPlayer.isCreative()) {
                if (serverPlayer instanceof IPlayerSkillData skillDataPlayer) {
                    int fishingLevel = skillDataPlayer.getSkillData().getFishingLevel();
                    // Accelerate lure time: reduce by an extra 1 tick for every 5 fishing levels
                    if (fishingLevel >= 5 && serverPlayer.getRandom().nextInt(5) < (fishingLevel / 5)) {
                        this.timeUntilLured--;
                    }
                }
            }
        }
    }

    @Inject(method = "retrieve", at = @At("HEAD"))
    private void onRetrieve(net.minecraft.world.item.ItemStack rod, CallbackInfoReturnable<Integer> cir) {
        Player owner = this.getPlayerOwner();
        if (owner instanceof ServerPlayer serverPlayer && !serverPlayer.isCreative()) {
            if (this.hookedIn != null || this.nibble > 0) {
                // Caught something (fish or entity)
                com.aurarpg.ExampleMod.addXpStatic(serverPlayer, "Fishing", 20.0);
                com.aurarpg.ExampleMod.updateLuckStatic(serverPlayer);
            }
        }
    }
}
