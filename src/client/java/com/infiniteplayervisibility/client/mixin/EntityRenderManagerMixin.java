package com.infiniteplayervisibility.client.mixin;

import com.infiniteplayervisibility.client.ClientEntityVisibility;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
abstract class EntityRenderManagerMixin {
	@Shadow
	public abstract <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T entity);

	@Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
	private void infinitePlayerVisibility$allowForcedEntitiesWithoutBypassingFrustum(
		Entity entity,
		Frustum frustum,
		double x,
		double y,
		double z,
		CallbackInfoReturnable<Boolean> cir
	) {
		if (!ClientEntityVisibility.shouldOverrideDistanceLimit(entity)) {
			return;
		}

		if (!ClientEntityVisibility.shouldRenderEntity(entity)) {
			cir.setReturnValue(false);
			return;
		}

		EntityRendererAccessor<Entity> renderer = (EntityRendererAccessor<Entity>)this.getRenderer(entity);
		if (!renderer.infinitePlayerVisibility$invokeCanBeCulled(entity)) {
			cir.setReturnValue(true);
			return;
		}

		AABB box = renderer.infinitePlayerVisibility$invokeGetBoundingBox(entity).inflate(0.5D);
		if (box.getSize() == 0.0D) {
			box = new AABB(
				entity.getX() - 2.0D,
				entity.getY() - 2.0D,
				entity.getZ() - 2.0D,
				entity.getX() + 2.0D,
				entity.getY() + 2.0D,
				entity.getZ() + 2.0D
			);
		}

		cir.setReturnValue(frustum.isVisible(box));
	}
}
