package com.mactso.harderfarther.events;

import java.util.Iterator;
import java.util.List;

import com.mactso.harderfarther.api.ServerWorldTickCallback;
import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.network.SyncFogToClientsPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

public class WorldTickHandler {

	// assumes this event only raised for server worlds. TODO verify.
	public static void onWorldTickRegister(){
		ServerWorldTickCallback.EVENT.register(
				(world) -> {


					// this is always serverlevel
					if (world instanceof ServerWorld) {

						GrimCitadelManager.checkCleanUpCitadels(world);

						long gametime = world.getTime();

						List<ServerPlayerEntity> allPlayers = world.getServer().getPlayerManager().getPlayerList();
						Iterator<ServerPlayerEntity> apI = allPlayers.iterator();

						PacketByteBuf buf = PacketByteBufs.create();
						buf.writeDouble(PrimaryConfig.getGrimFogRedPercent());
						buf.writeDouble(PrimaryConfig.getGrimFogGreenPercent());
						buf.writeDouble(PrimaryConfig.getGrimFogBluePercent());

						while (apI.hasNext()) { // sends to all players online.
							ServerPlayerEntity sp = apI.next();
							if (gametime % 100 == sp.getId() % 100) {
								ServerPlayNetworking.send((ServerPlayerEntity) sp, SyncFogToClientsPacket.GAME_PACKET_SYNC_FOG_COLOR_S2C, buf);
							}
						}
					}
					return ActionResult.PASS;
				});
	}

}
