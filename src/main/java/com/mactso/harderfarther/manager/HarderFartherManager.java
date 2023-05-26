package com.mactso.harderfarther.manager;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.network.SyncDifficultyToClientsPacket;
import com.mactso.harderfarther.utility.Utility;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldProperties;

public class HarderFartherManager {

	public static float calcDistanceModifier(Vec3d eventVec, Vec3d spawnVec) {
		double distance = eventVec.distanceTo(spawnVec);
		distance = Math.max(0, distance - MyConfig.getBoostMinDistance());
		Float f = (float) Math.min(1.0f, distance / MyConfig.getBoostMaxDistance());
		return f;
	}
	
	public static float doApplyHeightFactor(float difficulty, int y) {

		if (y < MyConfig.getMinimumSafeAltitude()) {
			difficulty *= 1.06f;
		} else if (y > MyConfig.getMaximumSafeAltitude()) {
			difficulty *= 1.09f;
		}

		return difficulty;

	}
	
	
	
	public static float getDistanceDifficultyHere (ServerWorld serverLevel, Vec3d eventVec) {
		double xzf = serverLevel.getDimension().coordinateScale();
		if (xzf == 0.0) {
			xzf = 1.0d;
		}
		WorldProperties winfo = serverLevel.getLevelProperties();
		Vec3d spawnVec = new Vec3d(winfo.getSpawnX() / xzf, winfo.getSpawnY(), winfo.getSpawnZ() / xzf);
		float difficulty = HarderFartherManager.calcDistanceModifier(eventVec, spawnVec);
		return difficulty;
	}
	
	
	
	public static float getDifficultyHere(ServerWorld serverLevel, LivingEntity le) {
		
		Utility.debugMsg(2, "getdifficulty here top");
		BlockPos pos = le.getBlockPos();

		double xzf = serverLevel.getDimension().coordinateScale();
		if (xzf == 0.0) {
			xzf = 1.0d;
		}
		WorldProperties winfo = serverLevel.getLevelProperties();
		Vec3d spawnVec = new Vec3d(winfo.getSpawnX() / xzf, winfo.getSpawnY(), winfo.getSpawnZ() / xzf);
		Vec3d eventVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

		Utility.debugMsg(2, "getTimedifficulty here top");
		float timeDifficulty = 0;
		timeDifficulty = HarderTimeManager.getTimeDifficulty(serverLevel, le);

		Utility.debugMsg(2, "getGrimdifficulty here top");
		float gcDifficultyPct = 0;
		gcDifficultyPct = GrimCitadelManager.getGrimDifficulty(le);

		Utility.debugMsg(2, "getCalcDistanceModifier top");
		float hfDifficulty = HarderFartherManager.calcDistanceModifier(eventVec, spawnVec);
		
		float highDifficulty = Math.max(timeDifficulty, hfDifficulty);
		highDifficulty = Math.max(gcDifficultyPct, highDifficulty);

		if (le instanceof ServerPlayerEntity sp) {
//			System.out.println("HFM sending hf:"+hfDifficulty + " gc:" + gcDifficultyPct + " tm:" + timeDifficulty);
			Utility.debugMsg(2, "getdifficulty here network message");


			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeFloat(hfDifficulty);
			buf.writeFloat(gcDifficultyPct);
			buf.writeFloat(timeDifficulty);
			ServerPlayNetworking.send((ServerPlayerEntity) sp, SyncDifficultyToClientsPacket.GAME_PACKET_SYNC_DIFFICULTY_S2C, buf);

		}	

		Utility.debugMsg(2, "getdifficulty returning " + highDifficulty);
		return highDifficulty;
	}
	
}
