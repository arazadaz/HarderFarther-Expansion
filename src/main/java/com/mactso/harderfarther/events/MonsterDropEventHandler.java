package com.mactso.harderfarther.events;

import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import com.mactso.harderfarther.api.LivingEntityDropCallback;
import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.api.DifficultyCalculator;
import com.mactso.harderfarther.manager.LootManager;
import com.mactso.harderfarther.utility.Utility;

public class MonsterDropEventHandler {

	public static long lastMobDeathTime = 0;

	public static float minCommonDistancePct = 0.01f; // (1% of max distance before common loot)
	public static float minUncommonDistancePct = 0.1f; // (10% of max distance before uncommon loot)
	public static float minRareDistancePct = 0.95f; // (95% of max distance before rare loot)




	//Disabled on initial port. I'm not sure if I should implement this or include it within a different "anti-cheese" mod.
	/*private static boolean doLimitDropSpeed(ServerWorld serverLevel, Entity eventEntity, BlockPos pos) {
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
	}*/



	public static void onMonsterDropEventRegister() {
		LivingEntityDropCallback.EVENT.register(
				(damageSource, entity)  -> {

					if (!isDropsSpecialLoot(entity, damageSource))
						return InteractionResult.PASS;

					ServerLevel serverLevel = (ServerLevel) entity.level();

					RandomSource rand = serverLevel.getRandom();
					BlockPos pos = BlockPos.containing(entity.getX(), entity.getY(), entity.getZ());

					// in this section prevent ALL drops if players are killing mobs too quickly.

					/*boolean cancel = doLimitDropSpeed(serverLevel, entity, pos);
					if (cancel) {
						return ActionResult.FAIL;
					}*/

					// In this section, give bonus loot

					Collection<ItemStack> eventItems = new ArrayList<ItemStack>(10);

					LootManager.doXPBottleDrop(entity, eventItems, rand);

					float boostDifficulty = DifficultyCalculator.getDifficultyHere(serverLevel,entity);
					if (boostDifficulty == 0)
						return InteractionResult.PASS;
					if (boostDifficulty > PrimaryConfig.getGrimCitadelMaxBoostPercent()) {
						if (boostDifficulty == GrimCitadelManager.getGrimDifficulty(entity)) {
							boostDifficulty = PrimaryConfig.getGrimCitadelMaxBoostPercent();
						}
					}

					float odds = 100 + (333 * boostDifficulty);
					float health = entity.getMaxHealth(); // todo debugging
					int d1000 = (int) (Math.ceil(rand.nextDouble() * 1000));

					if (d1000 > odds) {
						Utility.debugMsg(1, pos, "No Loot Upgrade: Roll " + d1000 + " odds " + odds);
						return InteractionResult.PASS;
					}

					d1000 = (int) (Math.ceil(entity.level().getRandom().nextDouble() * 1000));
					if (d1000 < 640) {
						d1000 += odds / 10;
					}

					Mob me = (Mob) entity;
					ItemStack itemStackToDrop = LootManager.doGetLootStack(entity, me, boostDifficulty, d1000);


					eventItems.add(itemStackToDrop);

					for(ItemStack item:eventItems){
						entity.spawnAtLocation(item);
					}


					Utility.debugMsg(1, pos, entity.getName().getString() + " died and dropped loot: "
							+ itemStackToDrop.getItem().toString());
					return InteractionResult.PASS;

			});
	}




	
	
	private static boolean isDropsSpecialLoot(LivingEntity eventEntity, DamageSource dS) {

		if (!(PrimaryConfig.isMakeMonstersHarderFarther()))
			return false;

		if (!(PrimaryConfig.isUseLootDrops()))
			return false;

		if (eventEntity == null) {
			return false;
		}

		if (!(eventEntity.level() instanceof ServerLevel)) {
			return false;
		}

		// Has to have been killed by a player to drop bonus loot.
		if ((dS != null) && (dS.getEntity() == null)) {
			return false;
		}

		if (!(dS.getEntity() instanceof ServerPlayer)) {
			return false;
		}

		if (!(eventEntity instanceof Enemy)) { 
			return false;
		}

		if (eventEntity instanceof Slime) {
			Slime se = (Slime) eventEntity;

			if (se.getSize() < 4) {
				return false;
			}
		}

		if (!(eventEntity instanceof Enemy)) {

			if (eventEntity instanceof AbstractFish) {
				return false;
			}

			if (eventEntity instanceof WaterAnimal) {
				return false;
			}

			if (eventEntity instanceof Animal) {
				return false;
			}
			
			return false;
		}

		return true;
	}

}
