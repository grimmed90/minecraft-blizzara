package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.ParticleTypes;

public class ExampleModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
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