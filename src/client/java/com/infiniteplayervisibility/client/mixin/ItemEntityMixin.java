package com.infiniteplayervisibility.client.mixin;

import com.infiniteplayervisibility.client.ClientEntityVisibility;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
abstract class ItemEntityMixin {
	@Shadow
	private int age;

	@Shadow
	private int pickupDelay;

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void infinitePlayerVisibility$skipRemoteItemPhysics(CallbackInfo ci) {
		ItemEntity entity = (ItemEntity)(Object)this;
		if (!entity.level().isClientSide() || !ClientEntityVisibility.shouldForceClientTick(entity)) {
			return;
		}

		if (entity.getItem().isEmpty()) {
			entity.discard();
			ci.cancel();
			return;
		}

		entity.baseTick();
		if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
			this.pickupDelay--;
		}

		if (this.age != -32768) {
			this.age++;
		}

		ci.cancel();
	}
}
