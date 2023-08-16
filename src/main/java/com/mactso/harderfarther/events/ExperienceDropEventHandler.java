package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.PrimaryConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;


// this method only *limits* xp drops that happen to fast.  it is part of the farm limiter.

public class ExperienceDropEventHandler {

	public static long tickTimer = 0; //This might have to be redone to be per player?, for fabric anyways.

	/*
	public void onMonsterDrops(LivingExperienceDropEvent event) {
		
		LivingEntity le = event.getEntity();
		if (le == null)   {
			return;
		}
		
		if (le.getWorld().isClient()) {
			return;
		}
		
		if (!(le instanceof MobEntity)) {
			return;
		}
		
		if (le instanceof AnimalEntity) {
			return;
		}
		
		ServerWorld serverLevel = (ServerWorld) le.world;
		
		if (closeToWorldSpawn(serverLevel, le))
			return;
		
		if (tickTimer > serverLevel.getTime()) {
			Utility.debugMsg(2, le, "Mob Died inside no bonus loot frame.");
			return;
		}
		tickTimer = serverLevel.getTime() + (long) 20; // no boosted XP for 1 seconds after a kill.

	}*/

	private boolean closeToWorldSpawn(ServerLevel serverLevel, LivingEntity le) {

		Vec3 spawnVec = new Vec3(serverLevel.getLevelData().getXSpawn(), serverLevel.getLevelData().getYSpawn(),
				serverLevel.getLevelData().getZSpawn());

		BlockPos pos = le.blockPosition();
		Vec3 eventVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());
		
		if (eventVec.distanceTo(spawnVec) < PrimaryConfig.getSafeDistance()*8)
			return true;

		return false;
	}
}
