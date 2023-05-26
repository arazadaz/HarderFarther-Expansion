package com.mactso.harderfarther.events;

import com.mactso.harderfarther.manager.GrimCitadelManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class PlayerInteractionEventHandler {

	// this wasn't being fired at all on water placement.  Not sure why not.
	
	/*@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent event) {
		PlayerEntity player = event.getEntity();
//		if (player.isCreative())
//			return;
		World level = player.world;
		if (level.isClient)
			return;

		
		if (GrimCitadelManager.isInGrimProtectedArea(event.getPos())) {
			event.setCanceled(true);
		}
	}*/


}
