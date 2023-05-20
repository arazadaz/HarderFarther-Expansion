package com.mactso.harderfarther.events;

import java.util.ArrayList;
import java.util.Collection;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.manager.HarderFartherManager;
import com.mactso.harderfarther.manager.LootManager;
import com.mactso.harderfarther.utility.Utility;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;

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



	public static void monsterDropEventRegister() {
		LivingEntityDropCallback.EVENT.register(
				(damageSource, entity)  -> {

					if (!isDropsSpecialLoot(entity, damageSource))
						return ActionResult.PASS;

					ServerWorld serverLevel = (ServerWorld) entity.world;

					RandomGenerator rand = serverLevel.getRandom();
					BlockPos pos = new BlockPos(entity.getX(), entity.getY(), entity.getZ());

					// in this section prevent ALL drops if players are killing mobs too quickly.

					/*boolean cancel = doLimitDropSpeed(serverLevel, entity, pos);
					if (cancel) {
						return ActionResult.FAIL;
					}*/

					// In this section, give bonus loot

					Collection<ItemStack> eventItems = new ArrayList<ItemStack>(10);

					LootManager.doXPBottleDrop(entity, eventItems, rand);

					float boostDifficulty = HarderFartherManager.getDifficultyHere(serverLevel,entity);
					if (boostDifficulty == 0)
						return ActionResult.PASS;
					if (boostDifficulty > MyConfig.getGrimCitadelMaxBoostPercent()) {
						if (boostDifficulty == GrimCitadelManager.getGrimDifficulty(entity)) {
							boostDifficulty = MyConfig.getGrimCitadelMaxBoostPercent();
						}
					}

					float odds = 100 + (333 * boostDifficulty);
					float health = entity.getMaxHealth(); // todo debugging
					int d1000 = (int) (Math.ceil(rand.nextDouble() * 1000));

					if (d1000 > odds) {
						Utility.debugMsg(1, pos, "No Loot Upgrade: Roll " + d1000 + " odds " + odds);
						return ActionResult.PASS;
					}

					d1000 = (int) (Math.ceil(entity.world.getRandom().nextDouble() * 1000));
					if (d1000 < 640) {
						d1000 += odds / 10;
					}

					MobEntity me = (MobEntity) entity;
					ItemStack itemStackToDrop = LootManager.doGetLootStack(entity, me, boostDifficulty, d1000);


					eventItems.add(itemStackToDrop);

					for(ItemStack item:eventItems){
						entity.dropStack(item);
					}


					Utility.debugMsg(1, pos, entity.getName().getString() + " died and dropped loot: "
							+ itemStackToDrop.getItem().toString());
					return ActionResult.PASS;

			});
	}




	
	
	private static boolean isDropsSpecialLoot(LivingEntity eventEntity, DamageSource dS) {

		if (!(MyConfig.isMakeMonstersHarderFarther()))
			return false;

		if (!(MyConfig.isUseLootDrops()))
			return false;

		if (eventEntity == null) {
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
