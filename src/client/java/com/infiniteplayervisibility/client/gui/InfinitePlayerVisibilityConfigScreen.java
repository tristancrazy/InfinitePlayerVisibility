package com.infiniteplayervisibility.client.gui;

import com.infiniteplayervisibility.config.InfinitePlayerVisibilityConfig;
import com.infiniteplayervisibility.config.InfinitePlayerVisibilityConfigManager;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class InfinitePlayerVisibilityConfigScreen extends Screen {
	private static final Component TITLE = Component.translatable("screen.infinite_player_visibility.title");
	private static final Component SUBTITLE = Component.translatable("screen.infinite_player_visibility.subtitle");
	private static final int OPTIONS_WIDTH = 240;
	private static final int BUTTON_WIDTH = 115;

	private final Screen parent;
	private final InfinitePlayerVisibilityConfig workingCopy;

	public InfinitePlayerVisibilityConfigScreen(Screen parent) {
		super(TITLE);
		this.parent = parent;
		this.workingCopy = InfinitePlayerVisibilityConfigManager.getConfig().copy();
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int leftX = centerX - OPTIONS_WIDTH / 2;
		int y = 68;

		this.addRenderableWidget(
			CycleButton.onOffBuilder(this.workingCopy.renderRemotePlayers())
				.create(leftX, y, OPTIONS_WIDTH, 20, Component.translatable("option.infinite_player_visibility.remote_players"), (button, value) -> {
					this.workingCopy.setRenderRemotePlayers(value);
				})
		);

		y += 24;
		this.addRenderableWidget(
			CycleButton.onOffBuilder(this.workingCopy.renderRemoteEntities())
				.create(leftX, y, OPTIONS_WIDTH, 20, Component.translatable("option.infinite_player_visibility.remote_entities"), (button, value) -> {
					this.workingCopy.setRenderRemoteEntities(value);
				})
		);

		y += 28;
		this.addRenderableWidget(new VisibilityDistanceSlider(leftX, y, OPTIONS_WIDTH, 20, this.workingCopy.visibilityDistanceBlocks(), this.workingCopy));

		int bottomY = this.height - 28;
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.saveAndClose()).bounds(centerX - BUTTON_WIDTH - 5, bottomY, BUTTON_WIDTH, 20).build());
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.onClose()).bounds(centerX + 5, bottomY, BUTTON_WIDTH, 20).build());
	}

	@Override
	public void onClose() {
		if (this.minecraft != null) {
			this.minecraft.setScreen(this.parent);
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
		this.extractMenuBackground(context);
		super.extractRenderState(context, mouseX, mouseY, deltaTicks);
		context.centeredText(this.font, this.title, this.width / 2, 18, 16777215);
		context.centeredText(this.font, SUBTITLE, this.width / 2, 34, 11184810);
	}

	private void saveAndClose() {
		InfinitePlayerVisibilityConfigManager.setConfig(this.workingCopy);
		this.onClose();
	}

	private static double blocksToSliderValue(int blocks) {
		if (blocks >= InfinitePlayerVisibilityConfig.MAX_VISIBILITY_DISTANCE_BLOCKS) {
			return 1.0D;
		}

		double minLog = Math.log(InfinitePlayerVisibilityConfig.MIN_VISIBILITY_DISTANCE_BLOCKS);
		double maxLog = Math.log(InfinitePlayerVisibilityConfig.MAX_VISIBILITY_DISTANCE_BLOCKS);
		double clamped = Math.log(InfinitePlayerVisibilityConfig.clampVisibilityDistanceBlocks(blocks));
		return (clamped - minLog) / (maxLog - minLog);
	}

	private static int sliderValueToBlocks(double value) {
		if (value >= 0.999D) {
			return InfinitePlayerVisibilityConfig.MAX_VISIBILITY_DISTANCE_BLOCKS;
		}

		double minLog = Math.log(InfinitePlayerVisibilityConfig.MIN_VISIBILITY_DISTANCE_BLOCKS);
		double maxLog = Math.log(InfinitePlayerVisibilityConfig.MAX_VISIBILITY_DISTANCE_BLOCKS);
		double interpolated = Math.exp(minLog + value * (maxLog - minLog));
		return InfinitePlayerVisibilityConfig.clampVisibilityDistanceBlocks((int)Math.round(interpolated));
	}

	private static Component formatDistanceText(int blocks) {
		if (blocks >= InfinitePlayerVisibilityConfig.MAX_VISIBILITY_DISTANCE_BLOCKS) {
			return Component.translatable("option.infinite_player_visibility.visibility_distance.infinite");
		}

		int chunks = Math.max(1, (int)Math.ceil(blocks / 16.0D));
		return Component.translatable(
			"option.infinite_player_visibility.visibility_distance.value",
			String.format(Locale.ROOT, "%,d", chunks),
			String.format(Locale.ROOT, "%,d", blocks)
		);
	}

	private static final class VisibilityDistanceSlider extends AbstractSliderButton {
		private final InfinitePlayerVisibilityConfig config;
		private int blocks;

		private VisibilityDistanceSlider(int x, int y, int width, int height, int initialBlocks, InfinitePlayerVisibilityConfig config) {
			super(x, y, width, height, CommonComponents.EMPTY, blocksToSliderValue(initialBlocks));
			this.config = config;
			this.blocks = InfinitePlayerVisibilityConfig.clampVisibilityDistanceBlocks(initialBlocks);
			this.updateMessage();
		}

		@Override
		protected void updateMessage() {
			this.setMessage(
				Component.translatable("option.infinite_player_visibility.visibility_distance", formatDistanceText(this.blocks))
			);
		}

		@Override
		protected void applyValue() {
			this.blocks = sliderValueToBlocks(this.value);
			this.config.setVisibilityDistanceBlocks(this.blocks);
		}
	}
}
