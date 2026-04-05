package com.aurarpg;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenSkillsMenuPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenSkillsMenuPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "open_skills_menu"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSkillsMenuPayload> CODEC = StreamCodec.unit(new OpenSkillsMenuPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
