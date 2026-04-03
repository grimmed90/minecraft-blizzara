package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.tags.ItemTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "modid";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final TagKey<Block> MINEABLE_PICKAXE = BlockTags.MINEABLE_WITH_PICKAXE;
    private static final TagKey<Block> MINEABLE_AXE = BlockTags.MINEABLE_WITH_AXE;

    // We can define custom tags or just use Identifiers for tags
    private static final TagKey<Block> FORGE_ORES_IRON = TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "ores/iron"));
    private static final TagKey<Block> FORGE_ORES_DIAMOND = TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "ores/diamond"));
    private static final TagKey<Block> FORGE_ORES_EMERALD = TagKey.create(net.minecraft.core.registries.Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "ores/emerald"));

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing RPG Mod");

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
            if (world.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) return;

            // Give XP
            double xp = getBlockXpValue(state, world, pos);
            if (state.is(MINEABLE_PICKAXE)) {
                addXp(player, "Mining", xp);

                // Double drop chance at level 10+
                if (getMiningLevel(player) >= 10 && world.getRandom().nextFloat() < 0.2f) {
                    dropDoubleLoot((ServerLevel) world, pos, state, serverPlayer, blockEntity);
                }
            } else if (state.is(MINEABLE_AXE)) {
                addXp(player, "Woodcutting", xp);
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerLevel world : server.getAllLevels()) {
                for (ServerPlayer player : world.players()) {
                    int miningLevel = getMiningLevel(player);
                    if (miningLevel >= 5) {
                        ItemStack mainHand = player.getMainHandItem();
                        if (mainHand.is(ItemTags.PICKAXES)) {
                            int hasteAmplifier = miningLevel >= 20 ? 1 : 0; // Haste I is 0, Haste II is 1
                            BuiltInRegistries.MOB_EFFECT.getOptional(Identifier.fromNamespaceAndPath("minecraft", "haste")).ifPresent(haste ->
                                player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(haste), 40, hasteAmplifier, false, false, true))
                            );
                        }
                    }
                }
            }
        });

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (entity instanceof ServerPlayer player) {
                // Give Constitution XP based on damage taken
                addXp(player, "Constitution", damageTaken);

                // Update Max Health
                updateMaxHealth(player);
            }
        });
    }

    private static final Identifier CONSTITUTION_HEALTH_MODIFIER = Identifier.fromNamespaceAndPath(MOD_ID, "constitution_health_modifier");

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

    private int getMiningLevel(Player player) {
        if (player instanceof IPlayerSkillData skillDataPlayer) {
            return skillDataPlayer.getSkillData().getMiningLevel();
        }
        return 0;
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
