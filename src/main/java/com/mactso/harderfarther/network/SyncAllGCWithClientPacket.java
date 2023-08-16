package com.mactso.harderfarther.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.events.FogColorsEventHandler;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class SyncAllGCWithClientPacket {

	public static ResourceLocation GAME_PACKET_SYNC_GRIM_CITADEL_S2C = new ResourceLocation(Main.MODID, "gamepacketsyncgrimcitadels2c");
	
	private static List<BlockPos> GCLocations = new ArrayList<BlockPos>(10);

	
	public static void processPacket(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender)
	{
		GCLocations.clear();
		int size = buf.readInt();

		for(int index = 0; index<size; index++) {
			GCLocations.add(buf.readBlockPos());

		}

		client.execute(() ->{
			GrimCitadelManager.realGCList = GCLocations;
		});

	}

}

