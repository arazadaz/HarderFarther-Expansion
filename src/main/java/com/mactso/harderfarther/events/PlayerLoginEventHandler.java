package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.network.SyncFogToClientsPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerLoginEventHandler {
	public static void register(){
		ServerPlayConnectionEvents.JOIN.register(
				(handler, sender, server) -> {

					if (handler.getPlayer().level().isClientSide) return;
					Player sp = handler.getPlayer();
					if (sp == null) return;
					if (!(sp instanceof ServerPlayer)) return;


					if (PrimaryConfig.isUseGrimCitadels()) {

						GrimCitadelManager.sendAllGCPosToClient((ServerPlayer) handler.getPlayer());

						FriendlyByteBuf buf = PacketByteBufs.create();
						buf.writeDouble(PrimaryConfig.getGrimFogRedPercent());
						buf.writeDouble(PrimaryConfig.getGrimFogGreenPercent());
						buf.writeDouble(PrimaryConfig.getGrimFogBluePercent());

						ServerPlayNetworking.send((ServerPlayer) handler.getPlayer(), SyncFogToClientsPacket.GAME_PACKET_SYNC_FOG_COLOR_S2C, buf);
					}

				});
    	
    }
}
