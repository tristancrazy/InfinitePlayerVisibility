package com.infiniteplayervisibility.client;

import com.infiniteplayervisibility.EntityVisibilityRules;
import com.infiniteplayervisibility.client.compat.VoxyCompat;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class ClientEntityVisibility {
	private static final LongSet RENDERABLE_ENTITY_POSITIONS = new LongOpenHashSet();
	private static ClientLevel cachedRenderableEntityWorld;
	private static long cachedRenderableEntityWorldTime = Long.MIN_VALUE;
	private static long cachedRenderableEntityCameraPos = Long.MIN_VALUE;

	private ClientEntityVisibility() {
	}

	public static boolean shouldOverrideDistanceLimit(Entity entity) {
		return EntityVisibilityRules.shouldForceRemoteTracking(entity);
	}

	public static boolean shouldForceClientTick(Entity entity) {
		return shouldOverrideDistanceLimit(entity) && !entity.isAlwaysTicking() && isWithinConfiguredVisibility(entity);
	}

	public static boolean shouldKeepClientTicking(Entity entity) {
		return shouldForceClientTick(entity) && !entity.isRemoved();
	}

	public static boolean shouldRenderEntity(Entity entity) {
		return shouldOverrideDistanceLimit(entity) && isWithinConfiguredVisibility(entity) && VoxyCompat.shouldRenderEntity(entity);
	}

	public static boolean hasRenderableEntityAt(ClientLevel world, BlockPos pos) {
		refreshRenderableEntityPositionCache(world);
		return RENDERABLE_ENTITY_POSITIONS.contains(pos.asLong());
	}

	public static void invalidateRenderableEntityPositionCache() {
		cachedRenderableEntityWorld = null;
		cachedRenderableEntityWorldTime = Long.MIN_VALUE;
		cachedRenderableEntityCameraPos = Long.MIN_VALUE;
		RENDERABLE_ENTITY_POSITIONS.clear();
	}

	private static boolean isWithinConfiguredVisibility(Entity entity) {
		int visibilityDistance = EntityVisibilityRules.getConfiguredTrackingDistanceBlocks(entity);
		if (visibilityDistance >= EntityVisibilityRules.INFINITE_TRACKING_DISTANCE_BLOCKS) {
			return true;
		}

		Minecraft client = Minecraft.getInstance();
		if (client.gameRenderer == null || client.gameRenderer.getMainCamera() == null) {
			return true;
		}

		Vec3 cameraPos = client.gameRenderer.getMainCamera().position();
		double maxDistance = visibilityDistance;
		return entity.distanceToSqr(cameraPos.x, cameraPos.y, cameraPos.z) <= maxDistance * maxDistance;
	}

	private static void refreshRenderableEntityPositionCache(ClientLevel world) {
		long worldTime = world.getGameTime();
		long cameraPos = getCameraBlockPos();
		if (world == cachedRenderableEntityWorld && worldTime == cachedRenderableEntityWorldTime && cameraPos == cachedRenderableEntityCameraPos) {
			return;
		}

		RENDERABLE_ENTITY_POSITIONS.clear();
		for (Entity entity : world.entitiesForRendering()) {
			if (shouldIndexRenderableEntity(entity)) {
				RENDERABLE_ENTITY_POSITIONS.add(entity.blockPosition().asLong());
			}
		}

		cachedRenderableEntityWorld = world;
		cachedRenderableEntityWorldTime = worldTime;
		cachedRenderableEntityCameraPos = cameraPos;
	}

	private static long getCameraBlockPos() {
		Minecraft client = Minecraft.getInstance();
		if (client.gameRenderer == null || client.gameRenderer.getMainCamera() == null) {
			return Long.MIN_VALUE;
		}

		return BlockPos.containing(client.gameRenderer.getMainCamera().position()).asLong();
	}

	private static boolean shouldIndexRenderableEntity(Entity entity) {
		return !entity.isRemoved() && shouldRenderEntity(entity);
	}
}
