package com.mactso.harderfarther.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mactso.harderfarther.Main;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Network
	{
		private static final String PROTOCOL_VERSION = "1.0";
		public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		    new Identifier(Main.MODID, "main"),
		    () -> PROTOCOL_VERSION,
		    PROTOCOL_VERSION::equals,
		    PROTOCOL_VERSION::equals
		);
		private static int id = 0;

		public static <MSG> void registerMessage(Class<MSG> msg,
				BiConsumer<MSG, PacketByteBuf> encoder,
				Function<PacketByteBuf, MSG> decoder,
				BiConsumer<MSG, Supplier<Context>> handler)
		{
			INSTANCE.registerMessage(id++, msg, encoder, decoder, handler);
		}

		@OnlyIn(Dist.CLIENT)
		public static <MSG> void sendToServer(MSG msg)
		{
			INSTANCE.sendToServer(msg);
		}

		public static <MSG> void sendToClient(MSG msg, ServerPlayerEntity player)
		{
			INSTANCE.sendTo(msg, player.networkHandler.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
		}

		public static <MSG> void sendToTarget(PacketDistributor.PacketTarget target, MSG msg)
		{
			INSTANCE.send(target, msg);
		}

	}
	

