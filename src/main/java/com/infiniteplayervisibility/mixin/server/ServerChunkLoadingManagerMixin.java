package com.infiniteplayervisibility.mixin.server;

import com.infiniteplayervisibility.EntityVisibilityRules;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
abstract class ServerChunkLoadingManagerMixin {
	@Shadow
	@Final
	private Int2ObjectMap<?> entityMap;

	@Shadow
	@Final
	ServerLevel level;

	@Inject(method = "tick()V", at = @At("TAIL"))
	private void infinitePlayerVisibility$refreshForcedTrackers(CallbackInfo ci) {
		if ((this.level.getGameTime() & 3L) != 0L) {
			return;
		}

		if (this.level.players().isEmpty()) {
			return;
		}

		for (Object trackerObject : this.entityMap.values()) {
			ServerChunkLoadingManagerEntityTrackerAccessor tracker = (ServerChunkLoadingManagerEntityTrackerAccessor)trackerObject;
			Entity entity = tracker.infinitePlayerVisibility$getEntity();
			if (!EntityVisibilityRules.shouldForceServerTracking(entity)) {
				continue;
			}

			for (ServerPlayer player : this.level.players()) {
				tracker.infinitePlayerVisibility$updateTrackedStatus(player);
			}
		}
	}
}
