package com.mactso.harderfarther.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.mactso.harderfarther.manager.GrimCitadelManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.network.NetworkEvent;

public class SyncAllGCWithClientPacket {
	
	private List<BlockPos> GCLocations;
	
	public SyncAllGCWithClientPacket ( List<BlockPos> gl)
	{
		this.GCLocations = gl;
	}
	
	public static void processPacket(SyncAllGCWithClientPacket message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork( () -> 
			{
				GrimCitadelManager.realGCList = message.GCLocations;
			}
		);
		ctx.get().setPacketHandled(true);
	}

	public static SyncAllGCWithClientPacket readPacketData(PacketByteBuf buf)
	{
		int numGCLocations = buf.readVarInt();
		List<BlockPos> readGCLocations = new ArrayList<>(numGCLocations);			
		for(int i=0; i<numGCLocations;i++) {
			readGCLocations.add(buf.readBlockPos());
		}
		return new SyncAllGCWithClientPacket(readGCLocations);
	}

	public static void writePacketData(SyncAllGCWithClientPacket msg, PacketByteBuf buf)
	{
		msg.encode(buf);
	}
	
	public void encode(PacketByteBuf buf)
	{
			
		buf.writeVarInt(GCLocations.size());
		for ( BlockPos b : GCLocations) {
			buf.writeBlockPos(b);
		}

	}
}

