package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.network.SyncFogToClientsPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerLoginEventHandler {
	public static void register(){
		ServerPlayConnectionEvents.JOIN.register(
				(handler, sender, server) -> {

					if (handler.getPlayer().world.isClient) return;
					PlayerEntity sp = handler.getPlayer();
					if (sp == null) return;
					if (!(sp instanceof ServerPlayerEntity)) return;


					if (MyConfig.isUseGrimCitadels()) {

						GrimCitadelManager.sendAllGCPosToClient((ServerPlayerEntity) handler.getPlayer());

						PacketByteBuf buf = PacketByteBufs.create();
						buf.writeDouble(PrimaryConfig.getGrimFogRedPercent());
						buf.writeDouble(PrimaryConfig.getGrimFogGreenPercent());
						buf.writeDouble(PrimaryConfig.getGrimFogBluePercent());

						ServerPlayNetworking.send((ServerPlayerEntity) handler.getPlayer(), SyncFogToClientsPacket.GAME_PACKET_SYNC_FOG_COLOR_S2C, buf);
					}

				});
    	
    }
}
