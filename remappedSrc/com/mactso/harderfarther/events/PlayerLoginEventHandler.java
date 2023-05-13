package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.network.SyncFogToClientsPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerLoginEventHandler {
    @SubscribeEvent
    public void onLogin(PlayerLoggedInEvent event)
    {
    	if (event.getEntity().level.isClientSide) return;
    	PlayerEntity sp = event.getEntity();
    	if ( sp == null ) return;
    	if (!(sp instanceof ServerPlayerEntity)) return;
    	if (MyConfig.isUseGrimCitadels()) {
    			GrimCitadelManager.sendAllGCPosToClient((ServerPlayerEntity) sp );
    			SyncFogToClientsPacket msg = new SyncFogToClientsPacket(
    					MyConfig.getGrimFogRedPercent(),
    					MyConfig.getGrimFogGreenPercent(),
    					MyConfig.getGrimFogBluePercent());
   				Network.sendToClient(msg, (ServerPlayerEntity)sp);
    	}
    	
    }
}
