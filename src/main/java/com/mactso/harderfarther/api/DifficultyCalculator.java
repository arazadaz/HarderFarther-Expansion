package com.mactso.harderfarther.api;

import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.manager.HarderTimeManager;
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

import java.util.List;

public class DifficultyCalculator {

	public static float calcDistanceModifier(Vec3d eventVec, Vec3d nearestOutpostVec) {
		double distance = eventVec.distanceTo(nearestOutpostVec);
		distance = Math.max(0, distance - PrimaryConfig.getBoostMinDistance());
		Float f = (float) Math.min(1.0f, distance / PrimaryConfig.getBoostMaxDistance());
		return f;
	}
	
	public static float doApplyHeightFactor(float difficulty, int y) {

		if (y < PrimaryConfig.getMinimumSafeAltitude()) {
			difficulty *= 1.06f;
		} else if (y > PrimaryConfig.getMaximumSafeAltitude()) {
			difficulty *= 1.09f;
		}

		return difficulty;

	}
	
	
	
	public static float getDistanceDifficultyHere (ServerWorld serverWorld, Vec3d eventVec) {
		double xzf = serverWorld.getDimension().coordinateScale();
		if (xzf == 0.0) {
			xzf = 1.0d;
		}
		WorldProperties winfo = serverWorld.getLevelProperties();
		Vec3d spawnVec = new Vec3d(winfo.getSpawnX() / xzf, winfo.getSpawnY(), winfo.getSpawnZ() / xzf);

		//Add spawn to outpost list if enabled & get nearest outpost
		Vec3d[] outposts = PrimaryConfig.getOutpostPositions();
		if(PrimaryConfig.isSpawnAnOutpost()){
			outposts[0] = spawnVec;
		}

		Vec3d nearestOutpost = getNearestOutpost(outposts, eventVec);

		float difficulty = DifficultyCalculator.calcDistanceModifier(eventVec, nearestOutpost);
		return difficulty;
	}
	
	
	
	public static float getDifficultyHere(ServerWorld serverWorld, LivingEntity le) {
		
		Utility.debugMsg(2, "getdifficulty here top");
		BlockPos pos = le.getBlockPos();

		double xzf = serverWorld.getDimension().coordinateScale();
		if (xzf == 0.0) {
			xzf = 1.0d;
		}
		WorldProperties winfo = serverWorld.getLevelProperties();
		Vec3d spawnVec = new Vec3d(winfo.getSpawnX() / xzf, winfo.getSpawnY(), winfo.getSpawnZ() / xzf);
		Vec3d eventVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());

		//Add spawn to outpost list if enabled & get nearest outpost
		Vec3d[] outposts = PrimaryConfig.getOutpostPositions();
		if(PrimaryConfig.isSpawnAnOutpost()){
			outposts[0] = spawnVec;
		}

		Vec3d nearestOutpost = getNearestOutpost(outposts, eventVec);

		Utility.debugMsg(2, "getTimedifficulty here top");
		float timeDifficulty = 0;
		timeDifficulty = HarderTimeManager.getTimeDifficulty(serverWorld, le);

		Utility.debugMsg(2, "getGrimdifficulty here top");
		float gcDifficultyPct = 0;
		gcDifficultyPct = GrimCitadelManager.getGrimDifficulty(le);

		Utility.debugMsg(2, "getCalcDistanceModifier top");
		float hfDifficulty = DifficultyCalculator.calcDistanceModifier(eventVec, nearestOutpost);
		
		float highDifficulty[] = new float[]{Math.max(timeDifficulty, hfDifficulty)};
		highDifficulty[0] = Math.max(gcDifficultyPct, highDifficulty[0]);

		if (le instanceof ServerPlayerEntity sp) {
//			System.out.println("HFM sending hf:"+hfDifficulty + " gc:" + gcDifficultyPct + " tm:" + timeDifficulty);
			Utility.debugMsg(2, "getdifficulty here network message");


			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeFloat(hfDifficulty);
			buf.writeFloat(gcDifficultyPct);
			buf.writeFloat(timeDifficulty);
			ServerPlayNetworking.send((ServerPlayerEntity) sp, SyncDifficultyToClientsPacket.GAME_PACKET_SYNC_DIFFICULTY_S2C, buf);

		}

		//Allow other mods to modify final difficulty. I personally will be using this to force the nether to always have a constant difficulty in a helper mod for my modpack.
		DifficultyOverrideCallback.EVENT.invoker().interact(highDifficulty, serverWorld, outposts, PrimaryConfig.getBoostMinDistance(), PrimaryConfig.getBoostMaxDistance());

		Utility.debugMsg(2, "getdifficulty returning " + highDifficulty);
		return highDifficulty[0];
	}



	public static Vec3d getNearestOutpost(Vec3d[] outposts, Vec3d eventVec){

		int iterator=0;
		if(!PrimaryConfig.isSpawnAnOutpost()){
			iterator++;
		}

		int nearestOutpostDistance = Integer.MAX_VALUE;
		Vec3d nearestOutpost = outposts[iterator];
		for(;iterator<outposts.length; iterator++){
			if(outposts[iterator].distanceTo(eventVec)<nearestOutpostDistance){
				nearestOutpost = outposts[iterator];
			}
		}


		return nearestOutpost;
	}
	
}
