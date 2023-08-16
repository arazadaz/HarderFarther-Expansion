package com.mactso.harderfarther.network;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.events.FogColorsEventHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class SyncFogToClientsPacket  {

	public static ResourceLocation GAME_PACKET_SYNC_FOG_COLOR_S2C = new ResourceLocation(Main.MODID, "gamepacketsyncfogcolors2c");
		private double red;
		private double green;
		private double blue;


		public static void processPacket(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender)
		{
			double red = buf.readDouble();
			double green = buf.readDouble();
			double blue = buf.readDouble();

			client.execute(() ->{
				FogColorsEventHandler.setFogRGB(red, green, blue);
			});
		}

}
