package com.example;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SkillUpdatePayload(
    int miningLevel, double miningXp,
    int woodcuttingLevel, double woodcuttingXp,
    int constitutionLevel, double constitutionXp,
    int excavationLevel, double excavationXp,
    int fishingLevel, double fishingXp,
    int combatLevel, double combatXp,
    int defenseLevel, double defenseXp,
    int agilityLevel, double agilityXp
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SkillUpdatePayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "skill_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SkillUpdatePayload> CODEC = CustomStreamCodec.create();

    private static class CustomStreamCodec {
        public static StreamCodec<RegistryFriendlyByteBuf, SkillUpdatePayload> create() {
            return StreamCodec.of(CustomStreamCodec::encode, CustomStreamCodec::decode);
        }

        private static void encode(RegistryFriendlyByteBuf buf, SkillUpdatePayload payload) {
            buf.writeVarInt(payload.miningLevel());
            buf.writeDouble(payload.miningXp());
            buf.writeVarInt(payload.woodcuttingLevel());
            buf.writeDouble(payload.woodcuttingXp());
            buf.writeVarInt(payload.constitutionLevel());
            buf.writeDouble(payload.constitutionXp());
            buf.writeVarInt(payload.excavationLevel());
            buf.writeDouble(payload.excavationXp());
            buf.writeVarInt(payload.fishingLevel());
            buf.writeDouble(payload.fishingXp());
            buf.writeVarInt(payload.combatLevel());
            buf.writeDouble(payload.combatXp());
            buf.writeVarInt(payload.defenseLevel());
            buf.writeDouble(payload.defenseXp());
            buf.writeVarInt(payload.agilityLevel());
            buf.writeDouble(payload.agilityXp());
        }

        private static SkillUpdatePayload decode(RegistryFriendlyByteBuf buf) {
            return new SkillUpdatePayload(
                buf.readVarInt(), buf.readDouble(),
                buf.readVarInt(), buf.readDouble(),
                buf.readVarInt(), buf.readDouble(),
                buf.readVarInt(), buf.readDouble(),
                buf.readVarInt(), buf.readDouble(),
                buf.readVarInt(), buf.readDouble(),
                buf.readVarInt(), buf.readDouble(),
                buf.readVarInt(), buf.readDouble()
            );
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
