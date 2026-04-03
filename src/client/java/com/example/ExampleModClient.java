package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ExampleModClient implements ClientModInitializer {
	private static KeyMapping openSkillsKeyBinding;

	@Override
	public void onInitializeClient() {
		openSkillsKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.modid.open_skills",
				GLFW.GLFW_KEY_K,
				KeyMapping.Category.register(Identifier.fromNamespaceAndPath("modid", "general"))
		));

		ClientPlayNetworking.registerGlobalReceiver(SkillUpdatePayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				Minecraft.getInstance().setScreen(new SkillsScreen(payload));
			});
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openSkillsKeyBinding.consumeClick()) {
				ClientPlayNetworking.send(new OpenSkillsMenuPayload());
			}

			if (client.level != null && !client.isPaused()) {
				for (AbstractClientPlayer player : client.level.players()) {
					if (player instanceof IPlayerSkillData skillDataPlayer) {
						int totalLevel = skillDataPlayer.getSkillData().getTotalLevel();
						double radius = 1.0 + (totalLevel * 0.1);

						for (int i = 0; i < 3; i++) {
							double angle = client.level.getRandom().nextDouble() * 2 * Math.PI;
							double xOffset = radius * Math.cos(angle);
							double zOffset = radius * Math.sin(angle);

							client.level.addParticle(
								ParticleTypes.CLOUD,
								player.getX() + xOffset,
								player.getY() + 0.1,
								player.getZ() + zOffset,
								0.0, 0.0, 0.0
							);
						}
					}
				}
			}
		});
	}
}
