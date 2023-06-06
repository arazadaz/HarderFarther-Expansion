package com.mactso.harderfarther.events;

import com.mactso.harderfarther.client.GrimSongManager;
import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.item.ModItems;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.api.DifficultyCalculator;
import com.mactso.harderfarther.manager.HarderTimeManager;
import com.mactso.harderfarther.network.GrimClientSongPacket;
import com.mactso.harderfarther.sounds.ModSounds;
import com.mactso.harderfarther.utility.Boosts;
import com.mactso.harderfarther.utility.Glooms;
import com.mactso.harderfarther.utility.Utility;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.random.RandomGenerator;


public class LivingEventMovementHandler {

	public static void onEntityTickRegister(){
		LivingEntityTickCallback.EVENT.register(
				(entity) -> {

					RandomGenerator rand = entity.getWorld().getRandom();

					if (entity.world.isClient()) {
						if (entity instanceof PlayerEntity cp) {
							GrimCitadelManager.playGCOptionalSoundCues(cp);
						}
						if (FogColorsEventHandler.getServerTimeDifficulty() == 0) {
							if ((entity instanceof PlayerEntity cp) && (rand.nextInt(144000) == 42)) {
								GrimSongManager.startSong(ModSounds.NUM_LAKE_DESTINY);
							}
						}
						return ActionResult.PASS;
					}

					ServerWorld serverLevel = (ServerWorld) entity.getWorld();

					if (entity instanceof ServerPlayerEntity sp) {
						Utility.debugMsg(2, "LivingEventMovementHandler");

						boolean hasLifeHeart = sp.getInventory().contains(ModItems.LIFE_HEART_STACK);

						if ((sp.getHealth() < sp.getMaxHealth()) && (hasLifeHeart)) {
							int dice = PrimaryConfig.getGrimLifeheartPulseSeconds() * Utility.TICKS_PER_SECOND;
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

						float difficulty = DifficultyCalculator.getDifficultyHere(serverLevel, entity);

						if (difficulty > 0) {
							if (GrimCitadelManager.isGCNear(difficulty)) {
								Utility.slowFlyingMotion(entity);
							}
							if (gameTime % 10 != entity.getId() % 10)
								return ActionResult.PASS;

							Utility.debugMsg(2, entity, "Living Event " + EntityType.getId((entity.getType())).toString()
									+ " dif: " + difficulty);
							if ((entity instanceof ServerPlayerEntity) && (rand.nextInt(300000) == 4242) && (difficulty > Utility.Pct09)) {
								PacketByteBuf buf = PacketByteBufs.create();
								buf.writeInt(ModSounds.NUM_DUSTY_MEMORIES);

								ServerPlayNetworking.send((ServerPlayerEntity) sp, GrimClientSongPacket.GAME_PACKET_SET_GRIM_CLIENT_SONG_S2C, buf);
							}

							if ((difficulty > Utility.Pct84)) {
								if (entity.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
									entity.removeStatusEffect(StatusEffects.SLOW_FALLING);
								}
							}

							if (GrimCitadelManager.getGrimDifficulty(entity) > 0) {
								Glooms.doGlooms(serverLevel, gameTime, difficulty, entity, Glooms.GRIM);
								if ((entity instanceof ServerPlayerEntity) && (rand.nextInt(144000) == 4242)
										&& (difficulty > Utility.Pct09)) {
									PacketByteBuf buf = PacketByteBufs.create();
									buf.writeInt(ModSounds.NUM_DUSTY_MEMORIES);

									ServerPlayNetworking.send((ServerPlayerEntity) sp, GrimClientSongPacket.GAME_PACKET_SET_GRIM_CLIENT_SONG_S2C, buf);
								}
							}
							if (HarderTimeManager.getTimeDifficulty(serverLevel, entity) > 0) {

								Glooms.doGlooms(serverLevel, gameTime, difficulty, entity, Glooms.TIME);

							}
						}

					} else {
						// "enter world event" horked as of 1.19.  Move boosts here..
						if (entity instanceof HostileEntity me) {
							Utility.debugMsg(2, "entering doBoostAbilities");
							String eDsc = EntityType.getId(me.getType()).toString();
							Boosts.doBoostAbilities(me, eDsc);
						}
					}
					return ActionResult.PASS;
				});

	}
}
