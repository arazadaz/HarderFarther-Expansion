package com.mactso.harderfarther.network;


import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.client.GrimSongManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class GrimClientSongPacket {

	public static ResourceLocation GAME_PACKET_SET_GRIM_CLIENT_SONG_S2C = new ResourceLocation(Main.MODID, "gamepacketsetgrimclientsongs2c");

	private int song;

	public GrimClientSongPacket (int song )
	{
		this.song = song;
	}

	public static void processPacket(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender){
		int song = buf.readInt();

		client.execute(() ->{
			GrimSongManager.startSong(song);
		});
	}


	public void send(Player player)
	{
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeInt(this.song);

		ServerPlayNetworking.send((ServerPlayer) player, GrimClientSongPacket.GAME_PACKET_SET_GRIM_CLIENT_SONG_S2C, buf);
	}
}

