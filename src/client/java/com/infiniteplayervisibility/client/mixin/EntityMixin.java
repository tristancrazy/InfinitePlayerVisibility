package com.infiniteplayervisibility.client.mixin;

import com.infiniteplayervisibility.client.ClientEntityVisibility;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
abstract class EntityMixin {
	@Inject(method = "shouldRenderAtSqrDistance(D)Z", at = @At("HEAD"), cancellable = true)
	private void infinitePlayerVisibility$allowRemoteEntityRendering(double distance, CallbackInfoReturnable<Boolean> cir) {
		Entity entity = (Entity)(Object)this;
		if (ClientEntityVisibility.shouldOverrideDistanceLimit(entity)) {
			cir.setReturnValue(ClientEntityVisibility.shouldRenderEntity(entity));
		}
	}
}
