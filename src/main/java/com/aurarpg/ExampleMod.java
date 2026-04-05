package com.aurarpg;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "aurarpg";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final TagKey<Block> MINEABLE_PICKAXE = TagKey.create(BuiltInRegistries.BLOCK.key(), Identifier.fromNamespaceAndPath("minecraft", "mineable/pickaxe"));
    private static final TagKey<Block> MINEABLE_AXE = TagKey.create(BuiltInRegistries.BLOCK.key(), Identifier.fromNamespaceAndPath("minecraft", "mineable/axe"));

    private static final TagKey<Block> FORGE_ORES_DIAMOND = TagKey.create(BuiltInRegistries.BLOCK.key(), Identifier.fromNamespaceAndPath("c", "ores/diamond"));
    private static final TagKey<Block> FORGE_ORES_EMERALD = TagKey.create(BuiltInRegistries.BLOCK.key(), Identifier.fromNamespaceAndPath("c", "ores/emerald"));
    private static final TagKey<Block> FORGE_ORES_IRON = TagKey.create(BuiltInRegistries.BLOCK.key(), Identifier.fromNamespaceAndPath("c", "ores/iron"));

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing ExampleMod...");

        PayloadTypeRegistry.serverboundPlay().register(OpenSkillsMenuPayload.ID, OpenSkillsMenuPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SkillUpdatePayload.ID, SkillUpdatePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(OpenSkillsMenuPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            if (player instanceof IPlayerSkillData skillDataPlayer) {
                SkillData data = skillDataPlayer.getSkillData();
                SkillUpdatePayload response = new SkillUpdatePayload(
                    data.getMiningLevel(), data.getMiningXp(),
                    data.getWoodcuttingLevel(), data.getWoodcuttingXp(),
                    data.getConstitutionLevel(), data.getConstitutionXp(),
                    data.getExcavationLevel(), data.getExcavationXp(),
                    data.getFishingLevel(), data.getFishingXp(),
                    data.getCombatLevel(), data.getCombatXp(),
                    data.getDefenseLevel(), data.getDefenseXp(),
                    data.getAgilityLevel(), data.getAgilityXp()
                );
                ServerPlayNetworking.send(player, response);
            }
        });

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world instanceof ServerLevel serverWorld && player instanceof ServerPlayer serverPlayer) {
                if (state.is(BlockTags.LOGS)) {
                    addXp(serverPlayer, "Woodcutting", 10.0);
                    breakLogsRecursive(serverWorld, pos, serverPlayer, 10);
                } else if (state.is(MINEABLE_PICKAXE) || state.is(FORGE_ORES_DIAMOND) || state.is(FORGE_ORES_EMERALD) || state.is(FORGE_ORES_IRON)) {
                    double xp = getBlockXpValue(state, world, pos);
                    if (xp > 0) {
                        addXp(serverPlayer, "Mining", xp);

                        if (getMiningLevel(serverPlayer) >= 10 && world.getRandom().nextFloat() < 0.1f) {
                            dropDoubleLoot(serverWorld, pos, state, serverPlayer, blockEntity);
                        }
                    }
                }

                if (state.is(BlockTags.DIRT) || state.is(BlockTags.SAND)) {
                    addXp(serverPlayer, "Excavation", 5.0);
                    dropExcavationLoot(serverWorld, pos, state, serverPlayer);
                }
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            int ticks = server.getTickCount();
            for (ServerLevel world : server.getAllLevels()) {
                for (ServerPlayer player : world.players()) {
                    if (player.isCreative()) continue;

                    // Give Constitution XP every 1200 ticks (1 minute)
                    if (ticks % 1200 == 0) {
                        addXp(player, "Constitution", 10.0);
                        updateMaxHealth(player);
                    }

                    // Give Agility XP while sprinting
                    if (player.isSprinting() && ticks % 20 == 0) { // 1 XP every second of sprinting
                        addXp(player, "Agility", 1.0);
                        updateMovementSpeed(player);
                    }

                    ItemStack mainHand = player.getMainHandItem();

                    int miningLevel = getMiningLevel(player);
                    if (miningLevel >= 5 && mainHand.is(ItemTags.PICKAXES)) {
                        int hasteAmplifier = miningLevel >= 20 ? 1 : 0; // Haste I is 0, Haste II is 1
                        BuiltInRegistries.MOB_EFFECT.getOptional(Identifier.fromNamespaceAndPath("minecraft", "haste")).ifPresent(haste ->
                            player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(haste), 40, hasteAmplifier, false, false, true))
                        );
                    }

                    int woodcuttingLevel = getLevel(player, "Woodcutting");
                    if (woodcuttingLevel >= 10 && mainHand.is(ItemTags.AXES)) {
                        int hasteAmplifier = woodcuttingLevel >= 20 ? 1 : 0;
                        BuiltInRegistries.MOB_EFFECT.getOptional(Identifier.fromNamespaceAndPath("minecraft", "haste")).ifPresent(haste ->
                            player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(haste), 40, hasteAmplifier, false, false, true))
                        );
                    }
                }
            }
        });

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (entity instanceof ServerPlayer player) {
                // Give Constitution XP based on damage taken
                addXp(player, "Constitution", damageTaken);

                // Give Defense XP based on damage taken
                addXp(player, "Defense", damageTaken);

                // Update Max Health (Constitution)
                updateMaxHealth(player);

                // Update Knockback Resistance
                updateKnockbackResistance(player);
            }

            if (source.getEntity() instanceof ServerPlayer attacker && !attacker.isCreative()) {
                addXp(attacker, "Combat", damageTaken);
                updateAttackDamage(attacker);
            }
        });
    }

    private static final Identifier CONSTITUTION_HEALTH_MODIFIER = Identifier.fromNamespaceAndPath(MOD_ID, "constitution_health_modifier");
    private static final Identifier COMBAT_DAMAGE_MODIFIER = Identifier.fromNamespaceAndPath(MOD_ID, "combat_damage_modifier");
    private static final Identifier DEFENSE_KNOCKBACK_MODIFIER = Identifier.fromNamespaceAndPath(MOD_ID, "defense_knockback_modifier");
    private static final Identifier AGILITY_SPEED_MODIFIER = Identifier.fromNamespaceAndPath(MOD_ID, "agility_speed_modifier");
    private static final Identifier FISHING_LUCK_MODIFIER = Identifier.fromNamespaceAndPath(MOD_ID, "fishing_luck_modifier");

    public static void addXpStatic(Player player, String skill, double xp) {
        if (player instanceof IPlayerSkillData skillDataPlayer) {
            boolean leveledUp = skillDataPlayer.getSkillData().addXP(skill, xp);
            if (leveledUp && player instanceof ServerPlayer serverPlayer) {
                ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                        net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                        net.minecraft.sounds.SoundSource.PLAYERS,
                        1.0F, 1.0F);
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        20, 0.5, 0.5, 0.5, 0.1);
            }
        }
    }

    public static void updateMovementSpeedStatic(ServerPlayer player) {
        int agilityLevel = 0;
        if (player instanceof IPlayerSkillData skillDataPlayer) {
            agilityLevel = skillDataPlayer.getSkillData().getAgilityLevel();
        }
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(AGILITY_SPEED_MODIFIER);

            double speedBonus = agilityLevel * 0.002; // +0.002 speed per level
            if (speedBonus > 0) {
                speedAttr.addPermanentModifier(new AttributeModifier(AGILITY_SPEED_MODIFIER, speedBonus, AttributeModifier.Operation.ADD_VALUE));
            }
        }
    }

    private void updateMovementSpeed(ServerPlayer player) {
        updateMovementSpeedStatic(player);
    }

    public static void updateLuckStatic(ServerPlayer player) {
        int fishingLevel = 0;
        if (player instanceof IPlayerSkillData skillDataPlayer) {
            fishingLevel = skillDataPlayer.getSkillData().getFishingLevel();
        }
        AttributeInstance luckAttr = player.getAttribute(Attributes.LUCK);
        if (luckAttr != null) {
            luckAttr.removeModifier(FISHING_LUCK_MODIFIER);

            double luckBonus = fishingLevel * 0.1; // +0.1 luck per level
            if (luckBonus > 0) {
                luckAttr.addPermanentModifier(new AttributeModifier(FISHING_LUCK_MODIFIER, luckBonus, AttributeModifier.Operation.ADD_VALUE));
            }
        }
    }

    private void updateKnockbackResistance(ServerPlayer player) {
        int defenseLevel = getLevel(player, "Defense");
        AttributeInstance kbAttr = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (kbAttr != null) {
            kbAttr.removeModifier(DEFENSE_KNOCKBACK_MODIFIER);

            double kbBonus = Math.min(1.0, defenseLevel * 0.02); // 2% kb resist per level, max 100%
            if (kbBonus > 0) {
                kbAttr.addPermanentModifier(new AttributeModifier(DEFENSE_KNOCKBACK_MODIFIER, kbBonus, AttributeModifier.Operation.ADD_VALUE));
            }
        }
    }

    private void updateAttackDamage(ServerPlayer player) {
        int combatLevel = getLevel(player, "Combat");
        AttributeInstance attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) {
            attackAttr.removeModifier(COMBAT_DAMAGE_MODIFIER);

            double damageBonus = combatLevel * 0.1; // +0.1 damage per level
            if (damageBonus > 0) {
                attackAttr.addPermanentModifier(new AttributeModifier(COMBAT_DAMAGE_MODIFIER, damageBonus, AttributeModifier.Operation.ADD_VALUE));
            }
        }
    }

    private void updateMaxHealth(ServerPlayer player) {
        if (player instanceof IPlayerSkillData skillDataPlayer) {
            int constitutionLevel = skillDataPlayer.getSkillData().getConstitutionLevel();
            AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                maxHealthAttr.removeModifier(CONSTITUTION_HEALTH_MODIFIER);

                int healthBonus = (constitutionLevel / 5) * 2; // +2.0 health (1 heart) every 5 levels
                if (healthBonus > 0) {
                    maxHealthAttr.addPermanentModifier(new AttributeModifier(CONSTITUTION_HEALTH_MODIFIER, healthBonus, AttributeModifier.Operation.ADD_VALUE));
                }
            }
        }
    }

    private double getBlockXpValue(BlockState state, Level world, BlockPos pos) {
        if (state.is(FORGE_ORES_DIAMOND) || state.is(FORGE_ORES_EMERALD)) {
            return 50.0;
        } else if (state.is(FORGE_ORES_IRON) || state.is(BlockTags.LOGS)) {
            return 5.0;
        }

        float hardness = state.getDestroySpeed(world, pos);
        if (hardness < 0) return 0.0; // Unbreakable
        return Math.max(1.0, hardness * 2.0);
    }

    private boolean addXp(Player player, String skill, double xp) {
        if (player instanceof IPlayerSkillData skillDataPlayer) {
            boolean leveledUp = skillDataPlayer.getSkillData().addXP(skill, xp);
            if (leveledUp && player instanceof ServerPlayer serverPlayer) {
                ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                        net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                        net.minecraft.sounds.SoundSource.PLAYERS,
                        1.0F, 1.0F);
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        20, 0.5, 0.5, 0.5, 0.1);
            }
            return leveledUp;
        }
        return false;
    }

    private int getLevel(Player player, String skill) {
        if (player instanceof IPlayerSkillData skillDataPlayer) {
            switch(skill) {
                case "Mining": return skillDataPlayer.getSkillData().getMiningLevel();
                case "Woodcutting": return skillDataPlayer.getSkillData().getWoodcuttingLevel();
                case "Excavation": return skillDataPlayer.getSkillData().getExcavationLevel();
                case "Combat": return skillDataPlayer.getSkillData().getCombatLevel();
                case "Defense": return skillDataPlayer.getSkillData().getDefenseLevel();
                case "Agility": return skillDataPlayer.getSkillData().getAgilityLevel();
                case "Fishing": return skillDataPlayer.getSkillData().getFishingLevel();
                case "Constitution": return skillDataPlayer.getSkillData().getConstitutionLevel();
            }
        }
        return 0;
    }

    private int getMiningLevel(Player player) {
        return getLevel(player, "Mining");
    }

    private void breakLogsRecursive(ServerLevel world, BlockPos pos, ServerPlayer player, int maxBlocks) {
        if (maxBlocks <= 0) return;
        int woodcuttingLevel = getLevel(player, "Woodcutting");
        if (woodcuttingLevel < 10) return;
        BlockPos up = pos.above();
        BlockState upState = world.getBlockState(up);
        if (upState.is(BlockTags.LOGS)) {
            world.destroyBlock(up, true, player);
            breakLogsRecursive(world, up, player, maxBlocks - 1);
        }
    }

    private void dropExcavationLoot(ServerLevel world, BlockPos pos, BlockState state, ServerPlayer player) {
        int level = getLevel(player, "Excavation");
        if (level > 0 && (state.is(BlockTags.DIRT) || state.is(BlockTags.SAND))) {
            float chance = level * 0.005f; // Max 50% at level 100
            if (world.getRandom().nextFloat() < chance) {
                net.minecraft.world.item.Item dropItem = world.getRandom().nextBoolean() ? net.minecraft.world.item.Items.FLINT : net.minecraft.world.item.Items.GOLD_NUGGET;
                Block.popResource(world, pos, new ItemStack(dropItem));
            }
        }
    }

    private void dropDoubleLoot(ServerLevel world, BlockPos pos, BlockState state, ServerPlayer player, BlockEntity blockEntity) {
        LootParams.Builder builder = new LootParams.Builder(world)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, player.getMainHandItem())
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, player);

        for (ItemStack drop : state.getDrops(builder)) {
            Block.popResource(world, pos, drop);
        }
    }
}
