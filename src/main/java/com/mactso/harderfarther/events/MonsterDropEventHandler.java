package com.mactso.harderfarther.events;

import java.util.Collection;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.manager.HarderFartherManager;
import com.mactso.harderfarther.manager.LootManager;
import com.mactso.harderfarther.timer.CapabilityChunkLastMobDeathTime;
import com.mactso.harderfarther.timer.IChunkLastMobDeathTime;
import com.mactso.harderfarther.utility.Utility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

public class MonsterDropEventHandler {

	public static long lastMobDeathTime = 0;

	public static float minCommonDistancePct = 0.01f; // (1% of max distance before common loot)
	public static float minUncommonDistancePct = 0.1f; // (10% of max distance before uncommon loot)
	public static float minRareDistancePct = 0.95f; // (95% of max distance before rare loot)



	
	
	private boolean doLimitDropSpeed(ServerWorld serverLevel, Entity eventEntity, BlockPos pos) {
		long worldTime = serverLevel.getTime();
		Chunk ichunk = serverLevel.getChunk(pos);
		IChunkLastMobDeathTime cap;
		boolean cancel = false;

		if (ichunk instanceof WorldChunk chunk) {
			cap = chunk.getCapability(CapabilityChunkLastMobDeathTime.LASTMOBDEATHTIME).orElse(null);
			lastMobDeathTime = 0;
			if (cap != null) {
				lastMobDeathTime = cap.getLastKillTime();
				long nextLootTime = lastMobDeathTime + MyConfig.getMobFarmingLimitingTimer();
				if (worldTime < nextLootTime) {
					Utility.debugMsg(2, pos,
							"Mobs Dying Too Quickly at: " + (int) eventEntity.getX() + ", " + (int) eventEntity.getY()
									+ ", " + (int) eventEntity.getZ() + ", " + " loot and xp denied.  Current Time:"
									+ worldTime + " nextLoot Time: " + nextLootTime + ".");
					cancel = true;
				} else {
					Utility.debugMsg(1, pos,
							"Mobs Dropping Loot at : " + (int) eventEntity.getX() + ", " + (int) eventEntity.getY()
									+ ", " + (int) eventEntity.getZ() + " Current Time:" + worldTime
									+ " nextLoot Time: " + nextLootTime + ".");
				}
				cap.setLastKillTime(worldTime);
			}
		}
		return cancel;
	}

	
	@SubscribeEvent  // serverside only.
	public boolean onMonsterDropsEvent(LivingDropsEvent event) {

		LivingEntity le = event.getEntity();
		DamageSource dS = event.getSource();

		if (!isDropsSpecialLoot(event, le, dS))
			return false;

		ServerWorld serverLevel = (ServerWorld) le.world;

		RandomGenerator rand = serverLevel.getRandom();
		BlockPos pos = new BlockPos(le.getX(), le.getY(), le.getZ());

		// in this section prevent ALL drops if players are killing mobs too quickly.

		boolean cancel = doLimitDropSpeed(serverLevel, le, pos);
		if (cancel) {
			event.setCanceled(true);
			return false;
		}

		// In this section, give bonus loot

		Collection<ItemEntity> eventItems = event.getDrops();

		LootManager.doXPBottleDrop(le, eventItems, rand);

		float boostDifficulty = HarderFartherManager.getDifficultyHere(serverLevel,le);
		if (boostDifficulty == 0)
			return false;
		if (boostDifficulty > MyConfig.getGrimCitadelMaxBoostPercent()) {
			if (boostDifficulty == GrimCitadelManager.getGrimDifficulty(le)) {
				boostDifficulty = MyConfig.getGrimCitadelMaxBoostPercent();
			}
		}		
		
		float odds = 100 + (333 * boostDifficulty);
		float health = le.getMaxHealth(); // todo debugging
		int d1000 = (int) (Math.ceil(rand.nextDouble() * 1000));

		if (d1000 > odds) {
			Utility.debugMsg(1, pos, "No Loot Upgrade: Roll " + d1000 + " odds " + odds);
			return false;
		}

		d1000 = (int) (Math.ceil(le.world.getRandom().nextDouble() * 1000));
		if (d1000 < 640) {
			d1000 += odds / 10;
		}

		MobEntity me = (MobEntity) event.getEntity();
		ItemStack itemStackToDrop = LootManager.doGetLootStack(le, me, boostDifficulty, d1000);

		ItemEntity myItemEntity = new ItemEntity(le.world, le.getX(), le.getY(),
				le.getZ(), itemStackToDrop);

		eventItems.add(myItemEntity);

		
		Utility.debugMsg(1, pos, le.getName().getString() + " died and dropped loot: "
				+ itemStackToDrop.getItem().toString());
		return true;
	}




	
	
	private boolean isDropsSpecialLoot(LivingDropsEvent event, LivingEntity eventEntity, DamageSource dS) {

		if (!(MyConfig.isMakeMonstersHarderFarther()))
			return false;

		if (!(MyConfig.isUseLootDrops()))
			return false;

		if (event.getEntity() == null) {
			return false;
		}

		if (!(eventEntity.world instanceof ServerWorld)) {
			return false;
		}

		// Has to have been killed by a player to drop bonus loot.
		if ((dS != null) && (dS.getAttacker() == null)) {
			return false;
		}

		if (!(dS.getAttacker() instanceof ServerPlayerEntity)) {
			return false;
		}

		if (!(eventEntity instanceof Monster)) { 
			return false;
		}

		if (eventEntity instanceof SlimeEntity) {
			SlimeEntity se = (SlimeEntity) eventEntity;

			if (se.getSize() < 4) {
				return false;
			}
		}

		if (!(eventEntity instanceof Monster)) {

			if (eventEntity instanceof FishEntity) {
				return false;
			}

			if (eventEntity instanceof WaterCreatureEntity) {
				return false;
			}

			if (eventEntity instanceof AnimalEntity) {
				return false;
			}
			
			return false;
		}

		return true;
	}

}
