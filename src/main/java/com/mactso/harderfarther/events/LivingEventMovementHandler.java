package com.mactso.harderfarther.events;

import com.mactso.harderfarther.client.GrimSongManager;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.item.ModItems;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.manager.HarderFartherManager;
import com.mactso.harderfarther.manager.HarderTimeManager;
import com.mactso.harderfarther.network.GrimClientSongPacket;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.sounds.ModSounds;
import com.mactso.harderfarther.utility.Boosts;
import com.mactso.harderfarther.utility.Glooms;
import com.mactso.harderfarther.utility.Utility;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.random.RandomGenerator;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;;

@Mod.EventBusSubscriber()
public class LivingEventMovementHandler {
	
	/**
	 * @param event
	 */
	@SubscribeEvent
	public void onLivingUpdate(LivingTickEvent event) {

		LivingEntity le = event.getEntity();
		RandomGenerator rand = le.getWorld().getRandom();

		if (le.world.isClient()) {
			if (le instanceof PlayerEntity cp) {
				GrimCitadelManager.playGCOptionalSoundCues(cp);
			}
			if (FogColorsEventHandler.getServerTimeDifficulty() == 0) {
				if ((le instanceof PlayerEntity cp) && (rand.nextInt(144000) == 42)) {
					GrimSongManager.startSong(ModSounds.NUM_LAKE_DESTINY);
				}
			}
			return;
		}

		ServerWorld serverLevel = (ServerWorld) le.getWorld();

		if (le instanceof ServerPlayerEntity sp) {
			Utility.debugMsg(2, "LivingEventMovementHandler");

			boolean hasLifeHeart = sp.getInventory().contains(ModItems.LIFE_HEART_STACK);

			if ((sp.getHealth() < sp.getMaxHealth()) && (hasLifeHeart)) {
				int dice = MyConfig.getGrimLifeheartPulseSeconds() * Utility.TICKS_PER_SECOND;
				int roll = rand.nextInt(dice);
				int duration = Utility.FOUR_SECONDS;
				if (roll == 42) { // once per 2 minutes
					int slot = sp.getInventory().getSlotWithStack(ModItems.LIFE_HEART_STACK);
					slot /= 9;
					int healingpower = 1;
					duration = Utility.FOUR_SECONDS * 3;
					if (slot != 1) { // first top row no sound
						float volume = 0.48f; // default loud
						healingpower = 3;
						duration = Utility.FOUR_SECONDS;

						if (slot == 3) // second row quiet
						{
							volume = 0.12f;
							healingpower = 2;
							duration = Utility.FOUR_SECONDS;
						}
						if (slot == 3) // third row just over hotbar
						{
							volume = 0.24f;
							healingpower = 2;
							duration = Utility.FOUR_SECONDS + Utility.FOUR_SECONDS;

						}
						serverLevel.playSound(null, sp.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_CHIME,
								SoundCategory.PLAYERS, volume, 0.86f);
					}
					Utility.updateEffect((LivingEntity) sp, healingpower, StatusEffects.REGENERATION,
							Utility.FOUR_SECONDS);
				}
			}

			long gameTime = serverLevel.getTime();

			float difficulty = HarderFartherManager.getDifficultyHere(serverLevel, le);

			if (difficulty > 0) {
				if (GrimCitadelManager.isGCNear(difficulty)) {
					Utility.slowFlyingMotion(le);
				}
				if (gameTime % 10 != le.getId() % 10)
					return;

				Utility.debugMsg(2, le, "Living Event " + EntityType.getId((event.getEntity().getType())).toString()
						+ " dif: " + difficulty);
				if ((le instanceof ServerPlayerEntity) && (rand.nextInt(300000) == 4242) && (difficulty > Utility.Pct09)) {
					Network.sendToClient(new GrimClientSongPacket(ModSounds.NUM_DUSTY_MEMORIES), sp);
				}

				if ((difficulty > Utility.Pct84)) {
					if (le.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
						le.removeStatusEffect(StatusEffects.SLOW_FALLING);
					}
				}

				if (GrimCitadelManager.getGrimDifficulty(le) > 0) {
					Glooms.doGlooms(serverLevel, gameTime, difficulty, le, Glooms.GRIM);
					if ((le instanceof ServerPlayerEntity) && (rand.nextInt(144000) == 4242)
							&& (difficulty > Utility.Pct09)) {
						Network.sendToClient(new GrimClientSongPacket(ModSounds.NUM_DUSTY_MEMORIES), sp);
					}
				}
				if (HarderTimeManager.getTimeDifficulty(serverLevel, le) > 0) {
					
					Glooms.doGlooms(serverLevel, gameTime, difficulty, le, Glooms.TIME);
					
				}
			}

		} else {
			// "enter world event" horked as of 1.19.  Move boosts here..
			if (event.getEntity() instanceof HostileEntity me) {
				Utility.debugMsg(2, "entering doBoostAbilities");
				String eDsc = EntityType.getId(me.getType()).toString();
				Boosts.doBoostAbilities(me, eDsc);
			}
		}

	}

}
