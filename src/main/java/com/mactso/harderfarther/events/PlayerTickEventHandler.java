package com.mactso.harderfarther.events;

import com.mactso.harderfarther.api.PlayerTickCallback;
import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.manager.HarderTimeManager;
import net.minecraft.world.InteractionResult;

public class PlayerTickEventHandler {

	public static void onPlayerTickRegister() {
		PlayerTickCallback.EVENT.register(
				(player) -> {

					if (PrimaryConfig.isMakeHarderOverTime()) {
						HarderTimeManager.doScarySpookyThings(player);
					}

					return InteractionResult.PASS;
				});
	}
}
