package com.infiniteplayervisibility.mixin.server;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
interface ServerChunkLoadingManagerEntityTrackerAccessor {
	@Accessor("entity")
	Entity infinitePlayerVisibility$getEntity();

	@Invoker("updatePlayer")
	void infinitePlayerVisibility$updateTrackedStatus(ServerPlayer player);
}
