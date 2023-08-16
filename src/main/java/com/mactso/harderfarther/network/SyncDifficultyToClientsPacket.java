package com.mactso.harderfarther.network;

import java.util.function.Supplier;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.client.GrimSongManager;
import com.mactso.harderfarther.events.FogColorsEventHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class SyncDifficultyToClientsPacket  {

	public static ResourceLocation GAME_PACKET_SYNC_DIFFICULTY_S2C = new ResourceLocation(Main.MODID, "gamepacketsyncdifficultys2c");

	public static void processPacket(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		float hardDifficulty = buf.readFloat();
		float grimDifficulty = buf.readFloat();
		float timeDifficulty = buf.readFloat();

		client.execute(() -> {
			FogColorsEventHandler.setLocalDifficulty(hardDifficulty, grimDifficulty, timeDifficulty);
		});
	}
}
