package com.infiniteplayervisibility.client.mixin;

import com.infiniteplayervisibility.client.ClientEntityVisibility;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
abstract class ClientWorldMixin {
	@Shadow
	@Final
	private EntityTickList tickingEntities;

	@Shadow
	@Nullable
	public abstract Entity getEntity(int id);

	@Inject(method = "addEntity", at = @At("TAIL"))
	private void infinitePlayerVisibility$startForcedEntityTicking(Entity entity, CallbackInfo ci) {
		if (ClientEntityVisibility.shouldOverrideDistanceLimit(entity)) {
			ClientEntityVisibility.invalidateRenderableEntityPositionCache();
		}

		if (ClientEntityVisibility.shouldForceClientTick(entity) && !this.tickingEntities.contains(entity)) {
			this.tickingEntities.add(entity);
		}
	}

	@Inject(method = "removeEntity", at = @At("HEAD"))
	private void infinitePlayerVisibility$stopForcedEntityTicking(int entityId, Entity.RemovalReason removalReason, CallbackInfo ci) {
		Entity entity = this.getEntity(entityId);
		if (entity == null) {
			return;
		}

		if (ClientEntityVisibility.shouldOverrideDistanceLimit(entity)) {
			ClientEntityVisibility.invalidateRenderableEntityPositionCache();
		}

		if (ClientEntityVisibility.shouldForceClientTick(entity) && this.tickingEntities.contains(entity)) {
			this.tickingEntities.remove(entity);
		}
	}
}
