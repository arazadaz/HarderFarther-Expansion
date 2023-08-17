package com.mactso.harderfarther.events;

import com.mactso.harderfarther.api.LivingEntityTickCallback;
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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;


public class LivingEventMovementHandler {

	public static void onEntityTickRegister(){
		LivingEntityTickCallback.EVENT.register(
				(entity) -> {

					RandomSource rand = entity.level().getRandom();

					if (entity.level().isClientSide()) {
						if (entity instanceof Player cp) {
							GrimCitadelManager.playGCOptionalSoundCues(cp);
						}
						if (FogColorsEventHandler.getServerTimeDifficulty() == 0) {
							if ((entity instanceof Player cp) && (rand.nextInt(144000) == 42)) {
								GrimSongManager.startSong(ModSounds.NUM_LAKE_DESTINY);
							}
						}
						return InteractionResult.PASS;
					}

					ServerLevel serverLevel = (ServerLevel) entity.level();

					if (entity instanceof ServerPlayer sp) {
						Utility.debugMsg(2, "LivingEventMovementHandler");

						boolean hasLifeHeart = sp.getInventory().contains(ModItems.LIFE_HEART_STACK);

						if ((sp.getHealth() < sp.getMaxHealth()) && (hasLifeHeart)) {
							int dice = PrimaryConfig.getGrimLifeheartPulseSeconds() * Utility.TICKS_PER_SECOND;
							int roll = rand.nextInt(dice);
							int duration = Utility.FOUR_SECONDS;
							if (roll == 42) { // once per 2 minutes
								int slot = sp.getInventory().findSlotMatchingItem(ModItems.LIFE_HEART_STACK);
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
									serverLevel.playSound(null, sp.blockPosition(), SoundEvents.NOTE_BLOCK_CHIME.value(),
											SoundSource.PLAYERS, volume, 0.86f);
								}
								Utility.updateEffect((LivingEntity) sp, healingpower, MobEffects.REGENERATION,
										Utility.FOUR_SECONDS);
							}
						}

						long gameTime = serverLevel.getGameTime();

						float difficulty = DifficultyCalculator.getDifficultyHere(serverLevel, entity);

						if (difficulty > 0) {
							if (GrimCitadelManager.isGCNear(difficulty)) {
								Utility.slowFlyingMotion(entity);
							}
							if (gameTime % 10 != entity.getId() % 10)
								return InteractionResult.PASS;

							Utility.debugMsg(2, entity, "Living Event " + EntityType.getKey((entity.getType())).toString()
									+ " dif: " + difficulty);
							if ((entity instanceof ServerPlayer) && (rand.nextInt(300000) == 4242) && (difficulty > Utility.Pct09)) {
								FriendlyByteBuf buf = PacketByteBufs.create();
								buf.writeInt(ModSounds.NUM_DUSTY_MEMORIES);

								ServerPlayNetworking.send((ServerPlayer) sp, GrimClientSongPacket.GAME_PACKET_SET_GRIM_CLIENT_SONG_S2C, buf);
							}

							if ((difficulty > Utility.Pct84)) {
								if (entity.hasEffect(MobEffects.SLOW_FALLING)) {
									entity.removeEffect(MobEffects.SLOW_FALLING);
								}
							}

							if (GrimCitadelManager.getGrimDifficulty(entity) > 0) {
								Glooms.doGlooms(serverLevel, gameTime, difficulty, entity, Glooms.GRIM);
								if ((entity instanceof ServerPlayer) && (rand.nextInt(144000) == 4242)
										&& (difficulty > Utility.Pct09)) {
									FriendlyByteBuf buf = PacketByteBufs.create();
									buf.writeInt(ModSounds.NUM_DUSTY_MEMORIES);

									ServerPlayNetworking.send((ServerPlayer) sp, GrimClientSongPacket.GAME_PACKET_SET_GRIM_CLIENT_SONG_S2C, buf);
								}
							}
							if (HarderTimeManager.getTimeDifficulty(serverLevel, entity) > 0) {

								Glooms.doGlooms(serverLevel, gameTime, difficulty, entity, Glooms.TIME);

							}
						}

					} else {
						// "enter world event" horked as of 1.19.  Move boosts here..
						if (entity instanceof Monster me) {
							Utility.debugMsg(2, "entering doBoostAbilities");
							String eDsc = EntityType.getKey(me.getType()).toString();
							Boosts.doBoostAbilities(me, eDsc);
						}
					}
					return InteractionResult.PASS;
				});

	}
}
