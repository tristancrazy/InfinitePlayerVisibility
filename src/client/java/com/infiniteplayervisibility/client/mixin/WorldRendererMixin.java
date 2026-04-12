package com.infiniteplayervisibility.client.mixin;

import com.infiniteplayervisibility.client.ClientEntityVisibility;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
abstract class WorldRendererMixin {
	@Shadow
	@Final
	private ClientLevel level;

	@Inject(method = "isSectionCompiledAndVisible", at = @At("RETURN"), cancellable = true)
	private void infinitePlayerVisibility$allowRenderingWithoutChunk(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValueZ()) {
			return;
		}

		if (ClientEntityVisibility.hasRenderableEntityAt(this.level, pos)) {
			cir.setReturnValue(true);
		}
	}
}
