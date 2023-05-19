package com.mactso.harderfarther.network;


import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.client.GrimSongManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class GrimClientSongPacket {

	public static Identifier GAME_PACKET_SET_GRIM_CLIENT_SONG_S2C = new Identifier(Main.MODID, "gamepacketsetgrimclientsongs2c");

	private int song;

	public GrimClientSongPacket (int song )
	{
		this.song = song;
	}

	public static void processPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
		int song = buf.readInt();

		client.execute(() ->{
			GrimSongManager.startSong(song);
		});
	}


	public void send(PlayerEntity player)
	{
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(this.song);

		ServerPlayNetworking.send((ServerPlayerEntity) player, GrimClientSongPacket.GAME_PACKET_SET_GRIM_CLIENT_SONG_S2C, buf);
	}
}

