package com.infiniteplayervisibility.client.mixin;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderer.class)
interface EntityRendererAccessor<T extends Entity> {
	@Invoker("getBoundingBoxForCulling")
	AABB infinitePlayerVisibility$invokeGetBoundingBox(T entity);

	@Invoker("affectedByCulling")
	boolean infinitePlayerVisibility$invokeCanBeCulled(T entity);
}
