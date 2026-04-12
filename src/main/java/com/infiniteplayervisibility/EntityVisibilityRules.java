package com.infiniteplayervisibility;

import com.infiniteplayervisibility.config.InfinitePlayerVisibilityConfig;
import com.infiniteplayervisibility.config.InfinitePlayerVisibilityConfigManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public final class EntityVisibilityRules {
	public static final int INFINITE_TRACKING_DISTANCE_BLOCKS = 30000000;

	private EntityVisibilityRules() {
	}

	public static boolean shouldForceRemoteTracking(Entity entity) {
		if (entity.isRemoved()) {
			return false;
		}

		InfinitePlayerVisibilityConfig config = InfinitePlayerVisibilityConfigManager.getConfig();
		return entity.isAlwaysTicking() ? config.renderRemotePlayers() : config.renderRemoteEntities();
	}

	public static int getConfiguredTrackingDistanceBlocks(Entity entity) {
		return shouldForceRemoteTracking(entity) ? InfinitePlayerVisibilityConfigManager.getConfig().visibilityDistanceBlocks() : 0;
	}

	public static boolean shouldForceServerTracking(Entity entity) {
		if (!shouldForceRemoteTracking(entity)) {
			return false;
		}

		if (entity.isAlwaysTicking()) {
			return true;
		}

		return entity.level() instanceof ServerLevel serverWorld && serverWorld.isPositionEntityTicking(entity.blockPosition());
	}
}
