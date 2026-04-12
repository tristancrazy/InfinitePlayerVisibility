package com.infiniteplayervisibility.mixin.server;

import com.infiniteplayervisibility.EntityVisibilityRules;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
abstract class ServerChunkLoadingManagerEntityTrackerMixin {
	@Shadow
	@Final
	private Entity entity;

	@Redirect(
		method = "updatePlayer(Lnet/minecraft/server/level/ServerPlayer;)V",
		at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I")
	)
	private int infinitePlayerVisibility$removeDistanceCap(int trackedDistance, int watchedDistance) {
		return EntityVisibilityRules.shouldForceServerTracking(this.entity)
			? EntityVisibilityRules.getConfiguredTrackingDistanceBlocks(this.entity)
			: Math.min(trackedDistance, watchedDistance);
	}

	@Redirect(
		method = "updatePlayer(Lnet/minecraft/server/level/ServerPlayer;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ChunkMap;isChunkTracked(Lnet/minecraft/server/level/ServerPlayer;II)Z"
		)
	)
	private boolean infinitePlayerVisibility$ignoreChunkVisibility(
		ChunkMap manager,
		net.minecraft.server.level.ServerPlayer player,
		int chunkX,
		int chunkZ
	) {
		return EntityVisibilityRules.shouldForceServerTracking(this.entity) || manager.isChunkTracked(player, chunkX, chunkZ);
	}
}
