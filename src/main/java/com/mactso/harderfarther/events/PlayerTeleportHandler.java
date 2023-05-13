package com.mactso.harderfarther.events;

import com.mactso.harderfarther.manager.GrimCitadelManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerTeleportHandler {

	@SubscribeEvent
	public void onLivingUpdate(EntityTeleportEvent event) {
		if (event.getEntity() instanceof PlayerEntity p) {
			if (p.isCreative()) {
				return;
			}
			if (GrimCitadelManager.isInsideGrimCitadelRadius(new BlockPos(event.getTarget()))) {
				event.setCanceled(true);
			}
			
		}
	}

}
