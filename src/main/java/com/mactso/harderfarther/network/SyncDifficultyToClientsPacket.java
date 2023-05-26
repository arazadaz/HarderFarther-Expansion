package com.mactso.harderfarther.network;

import java.util.function.Supplier;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.client.GrimSongManager;
import com.mactso.harderfarther.events.FogColorsEventHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SyncDifficultyToClientsPacket  {

	public static Identifier GAME_PACKET_SYNC_DIFFICULTY_S2C = new Identifier(Main.MODID, "gamepacketsyncdifficultys2c");

	public static void processPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		float hardDifficulty = buf.readFloat();
		float grimDifficulty = buf.readFloat();
		float timeDifficulty = buf.readFloat();

		client.execute(() -> {
			FogColorsEventHandler.setLocalDifficulty(hardDifficulty, grimDifficulty, timeDifficulty);
		});
	}
}
