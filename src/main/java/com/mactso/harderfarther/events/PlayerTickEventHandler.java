package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.HarderTimeManager;

public class PlayerTickEventHandler {
	
	@SubscribeEvent
	public void onTick(PlayerTickEvent event) {

		if ((event.phase != Phase.START))
			return;
		if (MyConfig.isMakeHarderOverTime()) {
			HarderTimeManager.doScarySpookyThings(event.player);
		}
			
	}
}
