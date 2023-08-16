package com.mactso.harderfarther.events;

import com.mactso.harderfarther.api.FogColorCallback;
import com.mactso.harderfarther.api.FogRenderCallback;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.world.InteractionResult;

public class FogColorsEventHandler {
	
	private static float sliderColorPercent = 1.0f;
	private static float sliderFogThickness = 1.0f;
	private static float sliderStartFogDistance = 1.0f;
	
	private static double RedFromServer = .85f;
	private static double GreenFromserver = 0.2f;
	private static double BlueFromServer = 0.3f;

	private static float clientLocalHardDifficulty = 0;
	private static float clientLocalGrimDifficulty = 0;

	private static float clientLocalTimeDifficulty = 0;
	private static float clientLocalHighDifficulty = 0;

	public static float getServerGrimDifficulty() {
		return clientLocalGrimDifficulty;
	}
	public static float getServerHardDifficulty() {
		return clientLocalHardDifficulty;
	}

	public static float getServerHighDifficulty() {
		return clientLocalHighDifficulty;
	}
	
	public static float getServerTimeDifficulty() {
		return clientLocalTimeDifficulty;
	}

	// r,g,b should always be 0 to 1.0f
	public static void setFogRGB(double r, double g, double b) {

		RedFromServer = r;
		GreenFromserver = g;
		BlueFromServer = b;
		
		if ((clientLocalGrimDifficulty == 0) && (clientLocalTimeDifficulty > 0)) {
			RedFromServer = r * 0.77;
			GreenFromserver = Math.min(1, g*1.1);
		}

	}

	public static void setLocalDifficulty(float hardDifficulty, float grimDifficulty, float timeDifficulty) {

		clientLocalHardDifficulty = hardDifficulty;
		clientLocalHighDifficulty = hardDifficulty;

		clientLocalGrimDifficulty = grimDifficulty;
		clientLocalHighDifficulty = Math.max(hardDifficulty, grimDifficulty);

		clientLocalTimeDifficulty = timeDifficulty;
		clientLocalHighDifficulty = Math.max(clientLocalHighDifficulty, timeDifficulty);

	}

	private static long colorTick = 0;

	private static long fogTick = 0;

	private int antiSpam = 0;

	private static void adjustFogColor(float[] fog, float slider) {

		double redSlider = Math.max(RedFromServer, slider);
		double greenSlider = Math.max(GreenFromserver, slider);
		double blueSlider = Math.max(BlueFromServer, slider);
//		if (++antiSpam%100 == 0)
//		System.out.println("fog color slider:" + slider);
		if (slider != 0) {
			fog[0]*=redSlider;
			fog[1]*=greenSlider;
			fog[2]*=blueSlider;
		}

		//Do nothing if slider is 0
	}

	private static void adjustFogDistance(float closeFogPercent, float farFogPercent) {

		if ((closeFogPercent < 1) || (farFogPercent < 1)) {
//			if (antiSpam%100 == 0)
//			System.out.println("fogclose%:" + closeFogPercent + " fogfar%:" + farFogPercent  );

			float f1 = RenderSystem.getShaderFogStart();
			float f2 = RenderSystem.getShaderFogEnd();

			f1 = (f1 * closeFogPercent) * farFogPercent;
			f2 *= farFogPercent;

			
			RenderSystem.setShaderFogStart(f1);
			RenderSystem.setShaderFogEnd(f2);
		}

	}

	private static float doSlideToPercent(float slider, float target) {
		final double slideAmount = 0.005f;
		if (slider > target+0.005f) {
			slider -= slideAmount;
		} else if (slider < target-0.005f) {
			slider += slideAmount;
		} else {
			slider = target;
		}
		return slider;
	}

	// clientside gui event
	public static void onFogColorRegister(){
		FogColorCallback.EVENT.register(
				(fog) -> {

					Minecraft m = Minecraft.getInstance();
					LocalPlayer cp = m.player;
					long gametick = cp.level.getGameTime();
					if ((colorTick != gametick)) {
						colorTick = gametick;
						float percent = Math.max(clientLocalGrimDifficulty, clientLocalTimeDifficulty);
						if ((percent > 0) && (percent < 0.1f)) {
							percent = 0.1f;
						}
						percent = Math.max(percent, 0.00f);
						percent = Math.min(percent, 1.0f);
						sliderColorPercent = doSlideToPercent(sliderColorPercent, 1 - percent);
					}

					adjustFogColor(fog, sliderColorPercent);
				});
	}

	// Density of Fog- not Color
	public static void onFogRenderRegister(){
		FogRenderCallback.EVENT.register(
				(camera, fogType, viewDistance, thickFog, tickDelta) -> {
//					FogMode sky = FogMode.FOG_SKY;
					if (fogType == FogMode.FOG_TERRAIN) {
						Minecraft m = Minecraft.getInstance();
						LocalPlayer cp = m.player;
						long gametick = cp.level.getGameTime();
						if ((FogColorsEventHandler.fogTick != gametick)) {
							FogColorsEventHandler.fogTick = gametick;

							float percent = 1.0f;
							if (clientLocalGrimDifficulty >= clientLocalTimeDifficulty) {
								percent = clientLocalGrimDifficulty;
							} else {
								percent = clientLocalGrimDifficulty;
							}

							if ((percent > 0.0f) && (percent < 0.05f)) {
								percent = 0.05f;
							}

							if (percent > 0.75) {
								percent -= (percent - 0.70) * 2.5;
							}
							percent = Math.max(0, percent);
							percent = Math.min(percent, 1.0f);

							sliderStartFogDistance = doSlideToPercent(sliderStartFogDistance, 1 - percent);
							sliderFogThickness = doSlideToPercent(sliderFogThickness, 1 - percent);
						}


						adjustFogDistance(sliderStartFogDistance, sliderFogThickness);

					}
					return InteractionResult.PASS;
				});

	}

}
