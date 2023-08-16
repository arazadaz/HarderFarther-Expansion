package com.mactso.harderfarther.events;

import java.util.Iterator;
import java.util.List;

import com.mactso.harderfarther.api.ServerWorldTickCallback;
import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.network.SyncFogToClientsPacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class WorldTickHandler {

	// assumes this event only raised for server worlds. TODO verify.
	public static void onWorldTickRegister(){
		ServerTickEvents.END_WORLD_TICK.register(
				(world) -> {


					// this is always serverlevel
					if (world instanceof ServerLevel) {

						GrimCitadelManager.checkCleanUpCitadels(world);

						long gametime = world.getGameTime();

						List<ServerPlayer> allPlayers = world.getServer().getPlayerList().getPlayers();
						Iterator<ServerPlayer> apI = allPlayers.iterator();

						FriendlyByteBuf buf = PacketByteBufs.create();
						buf.writeDouble(PrimaryConfig.getGrimFogRedPercent());
						buf.writeDouble(PrimaryConfig.getGrimFogGreenPercent());
						buf.writeDouble(PrimaryConfig.getGrimFogBluePercent());

						while (apI.hasNext()) { // sends to all players online.
							ServerPlayer sp = apI.next();
							if (gametime % 100 == sp.getId() % 100) {
								ServerPlayNetworking.send((ServerPlayer) sp, SyncFogToClientsPacket.GAME_PACKET_SYNC_FOG_COLOR_S2C, buf);
							}
						}
					}
				});
	}

}
