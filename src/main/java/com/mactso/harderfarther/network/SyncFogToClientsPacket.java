package com.mactso.harderfarther.network;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.events.FogColorsEventHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SyncFogToClientsPacket  {

	public static Identifier GAME_PACKET_SYNC_FOG_COLOR_S2C = new Identifier(Main.MODID, "gamepacketsyncfogcolors2c");
		private double red;
		private double green;
		private double blue;


		public static void processPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
		{
			double red = buf.readDouble();
			double green = buf.readDouble();
			double blue = buf.readDouble();

			client.execute(() ->{
				FogColorsEventHandler.setFogRGB(red, green, blue);
			});
		}

}
