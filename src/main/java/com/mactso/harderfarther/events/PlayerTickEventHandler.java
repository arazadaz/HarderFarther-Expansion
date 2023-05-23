package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.HarderTimeManager;
import net.minecraft.util.ActionResult;

public class PlayerTickEventHandler {

	public static void onPlayerTickRegister() {
		PlayerTickCallback.EVENT.register(
				(player) -> {

					if (MyConfig.isMakeHarderOverTime()) {
						HarderTimeManager.doScarySpookyThings(player);
					}

					return ActionResult.PASS;
				});
	}
}
