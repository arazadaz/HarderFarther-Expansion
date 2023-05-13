package com.mactso.harderfarther.utility;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FernBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.item.ModItems;
import com.mactso.harderfarther.manager.GrimCitadelManager;

public class Glooms {

	static List<Block> gloomHungerBlocks = Arrays.asList(Blocks.WATER, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.SAND,
			Blocks.NETHERRACK);

	public final static int HARD = 0;
	public final static int GRIM = 1;
	public final static int TIME = 2;

	private final static int AMP_1 = 0;
	static long pigTimer = 0;
	static long fishTimer = 0;

	static long villagerTimer = 0;
	static long phantomTimer = 0;
	static long invisTimer = 0;
	static long skeletonTimer = 0;
	static long spiderTimer = 0;
	static long spiderWebTimer = 0;
	static long zoglinTimer = 0;
	static long zombifiedPiglinTimer = 0;
	static long zombieTimer = 0;
	static long witherSkeletonTimer = 0;
	static long creeperTimer = 0;

	public static void doGloomPigs(PigEntity pig, BlockPos pos, long gameTime, ServerWorld serverLevel) {
		if (MyConfig.isGrimEffectPigs()) {
			
			if (pigTimer < gameTime) {
				pigTimer = gameTime + 1800;
				float pitch = 0.8f;
				int roll = serverLevel.getRandom().nextInt(100);
				if (roll < 10) {
					Utility.updateEffect(pig, 3, StatusEffects.WITHER, 120);
					serverLevel.playSound(null, pos, SoundEvents.ENTITY_PIGLIN_ADMIRING_ITEM, SoundCategory.AMBIENT, 2.20f,
							pitch);
					Utility.populateEntityType(EntityType.PIGLIN, serverLevel, pos, 1, 0, pig.isBaby());
				} else if (roll < 80) {
					Utility.updateEffect(pig, 3, StatusEffects.WITHER, 120);
					serverLevel.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.AMBIENT, 2.20f,
							pitch);
					Utility.populateEntityType(EntityType.HOGLIN, serverLevel, pos, 1, 0, pig.isBaby());
				} else {
					serverLevel.playSound(null, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.AMBIENT, 4.20f,
							pitch);
					Utility.populateEntityType(EntityType.ZOMBIFIED_PIGLIN, serverLevel, pos, 1, 0, true, pig.isBaby()); 
																															
				}
			}
		}
	}

	public static void doGloomDeadBranches(LivingEntity le, BlockPos pos, World level) {
		Utility.debugMsg(2, pos, "doSpreadDeadBranches");
		if (MyConfig.isGrimEffectTrees()) {
			if (level.getLightLevel(LightType.SKY, pos) > 10) {
				BlockPos deadBranchPos = level.getTopPosition(Type.MOTION_BLOCKING, pos);
				Block b = level.getBlockState(deadBranchPos.down()).getBlock();
				if (b instanceof LeavesBlock || b == Blocks.NETHER_WART_BLOCK) {
					if (b == ModBlocks.DEAD_BRANCHES || b == Blocks.NETHER_WART_BLOCK) {
						for (int i = 0; i <= 3; i++) {
							deadBranchPos = doSpreadOneDeadBranch(level, deadBranchPos);
						}
					} else {
						if (level.getRandom().nextInt(100) == 42) {
							level.setBlockState(deadBranchPos, Blocks.NETHER_WART_BLOCK.getDefaultState(), 3);
						} else {
							level.setBlockState(deadBranchPos, ModBlocks.DEAD_BRANCHES.getDefaultState(), 3);
						}
					}
				}
			}
		}
	}

	private static BlockPos doSpreadOneDeadBranch(World level, BlockPos pos) {
		Block b;
		BlockPos workPos = pos;
		int i = level.getRandom().nextInt(7);
		switch (i) {
		case 0:
			workPos = pos.north();
			break;
		case 1:
			workPos = pos.south();
			break;
		case 2:
			workPos = pos.east();
			break;
		case 3:
			workPos = pos.west();
			break;
		default:
			int r = 1 + level.getRandom().nextInt(2);
			workPos = pos.down(r);
		}
		b = level.getBlockState(workPos).getBlock();
		if ((b instanceof LeavesBlock) && (b != ModBlocks.DEAD_BRANCHES)) {
			level.setBlockState(workPos, ModBlocks.DEAD_BRANCHES.getDefaultState(), 3);
		}
		return workPos;
	}

	public static void doGloomWaterAnimals(WaterCreatureEntity we, BlockPos pos, long gameTime, ServerWorld serverLevel) {
		// May later break this into different kinds of water animals or fish.
		doGloomWaterAnimal(we, pos, gameTime, serverLevel);
	}

	private static void doGloomWaterAnimal(WaterCreatureEntity we, BlockPos pos, long gameTime, ServerWorld serverLevel) {

		if (fishTimer < gameTime) {
			if (!isDeepWaterUnderSky(we))
				return;

			fishTimer = gameTime + 600;
			List<GuardianEntity> listG = serverLevel.getNonSpectatingEntities(GuardianEntity.class,
					new Box(pos.north(32).west(32).up(8), pos.south(32).east(32).down(8)));
			if (listG.size() > 5)
				return;
			float pitch = 0.7f;
			Utility.populateEntityType(EntityType.GUARDIAN, serverLevel, pos, 1, 0);
		}
	}

	private static boolean isDeepWaterUnderSky(WaterCreatureEntity we) {
		BlockPos pos = we.getBlockPos();

		if (!we.getWorld().isSkyVisibleAllowingSea(pos))
			return false;
		Block bAbove = we.world.getBlockState(pos.up(12)).getBlock();
		Block bBelow = we.world.getBlockState(pos.down(12)).getBlock();
		if ((bAbove == Blocks.WATER) || (bBelow == Blocks.WATER)) {
			return true;
		}

		return false;
	}

	public static void doGlooms(ServerWorld serverLevel, long gameTime, float difficulty, LivingEntity le,
			int gloomType) {
		BlockPos pos = le.getBlockPos();

		if (le instanceof ServerPlayerEntity sp) {
			int amplitude = getEffectAmplitudeByDifficulty(difficulty);
			doGloomPlayer(sp, pos, serverLevel, difficulty, gloomType, amplitude);
		} else if (le instanceof VillagerEntity ve) {
			doGloomVillagers(ve, pos, gameTime, serverLevel);
		} else if (le instanceof WaterCreatureEntity we) {
			Glooms.doGloomWaterAnimals(we, pos, gameTime, serverLevel);
		} else if (le instanceof AnimalEntity ae) {
			doGloomAnimals(ae, pos, gameTime, serverLevel);
		} else if (le instanceof Monster) {
			doGloomMonsters(le, pos, gameTime, serverLevel);
		}

		Glooms.doGloomDeadBranches(le, pos, serverLevel);
	}

	private static int getEffectAmplitudeByDifficulty(float difficulty) {
		if (difficulty > Utility.Pct95)
			return 0;
		if (difficulty > Utility.Pct84)
			return 1;
		return 0;
	}

	public static void doGloomAnimals(AnimalEntity ae, BlockPos pos, long gameTime, ServerWorld level) {

		if (!(MyConfig.isGrimEffectAnimals()))
			return;

		if (level.getRandom().nextInt(400) < 9) {
			if (ae.getHealth() > 3) {
				Utility.updateEffect((LivingEntity) ae, 0, StatusEffects.POISON, 10);
				BlockState bs = level.getBlockState(ae.getBlockPos().down());
				if ((!isStoneOrWall(bs)) && (!GrimCitadelManager.getFloorBlocks().contains(bs.getBlock()))) {
					level.setBlockState(ae.getBlockPos().down(), Blocks.GRAVEL.getDefaultState(), 3);
				}
			}
			doGloomGroundTransform(ae, level);
		}
		if (level.getRandom().nextInt(9000) == 51) {
			BlockPos firePos = level.getTopPosition(Type.MOTION_BLOCKING_NO_LEAVES, pos.north(2));
			level.setBlockState(firePos, Blocks.FIRE.getDefaultState(), 3);
		}
		if (ae instanceof PigEntity) {
			Glooms.doGloomPigs((PigEntity) ae, pos, gameTime, level);
		}
	}

	
	
	public static boolean isStoneOrWall (BlockState bs) {
		if (bs.getBlock() == Blocks.COBBLESTONE) return false;
		if (bs.getBlock() == Blocks.COBBLESTONE_SLAB) return false;
		if (bs.getBlock() == Blocks.COBBLESTONE_WALL) return false;
		if (bs.getBlock() == Blocks.COBBLESTONE_STAIRS) return false;
		if (bs.getMaterial() == Material.STONE) return true;
		if (bs.getBlock() instanceof WallBlock) return true;
		return false;
	}
	
	
	
	public static void doGloomMobCreepers(LivingEntity le, long gameTime, ServerWorld serverLevel) {
		if (creeperTimer < gameTime) {
			creeperTimer = gameTime + 240;
			Utility.updateEffect(le, 0, StatusEffects.INVISIBILITY, 960);
		}
	}

	public static void doGloomMobPhantoms(LivingEntity le, BlockPos pos, long gameTime, ServerWorld serverLevel) {
		if (phantomTimer < gameTime) {
			phantomTimer = gameTime + 160;
			Utility.updateEffect(le, 0, StatusEffects.FIRE_RESISTANCE, 640);
			if (serverLevel.getRandom().nextInt(6) == 1) {
				TntEntity tnt = EntityType.TNT.spawn(serverLevel, null, null, null, pos, SpawnReason.NATURAL, true,
						true);
				tnt.setFuse(80);
			}
		}
	}

	public static void doGloomMobSkeletons(LivingEntity le, BlockPos pos, long gameTime, ServerWorld serverLevel) {
		if (skeletonTimer < gameTime) {
			skeletonTimer = gameTime + 120;
//			Block b = serverLevel.getBlockState(pos).getBlock(); possible buff based on block standing on feature.
			Utility.updateEffect(le, 0, StatusEffects.STRENGTH, 480);
			Utility.updateEffect(le, 0, StatusEffects.FIRE_RESISTANCE, 480);
			if (le instanceof WitherSkeletonEntity) {
				Utility.updateEffect(le, 0, StatusEffects.INVISIBILITY, 480);
			} else {
				if (serverLevel.getLightLevel(pos) < 9) {
					if (witherSkeletonTimer < gameTime) {
						witherSkeletonTimer = gameTime + 1800;
						Utility.populateEntityType(EntityType.WITHER_SKELETON, serverLevel, le.getBlockPos(), 1, 0);
					}
				}
			}
		}
	}

	public static void doGloomMobSpiders(LivingEntity le, BlockPos pos, long gameTime, ServerWorld serverLevel) {
		if (spiderTimer < gameTime) {
			spiderTimer = gameTime + 80;
			Utility.updateEffect(le, 0, StatusEffects.STRENGTH, 480);
			Utility.updateEffect(le, 0, StatusEffects.ABSORPTION, 480);
		}
		if (spiderWebTimer < gameTime) {
			spiderWebTimer = gameTime + 1200; // 1 per two minutes.
			if (Utility.isNotNearWebs(pos, serverLevel)) {
				le.world.setBlockState(pos, Blocks.COBWEB.getDefaultState(), 3);
			}
		}
	}

	public static void doGloomMobZoglins(LivingEntity le, BlockPos pos, long gameTime, ServerWorld serverLevel) {
		if (Utility.isOutside(pos, serverLevel)) {
			if (zoglinTimer < gameTime) {
				zoglinTimer = gameTime + 500;
				doGloomGrassTransform(pos, serverLevel);
				doGloomGroundTransform(le, serverLevel);
			} else {
				zoglinTimer--; // when lots of zoglin speed up timer
			}
		}
	}

	public static void doGloomMobZombies(LivingEntity le, long gameTime, ServerWorld serverLevel) {
		if (zombieTimer < gameTime) {
			zombieTimer = gameTime + 240;
			Utility.updateEffect(le, 1, StatusEffects.REGENERATION, Utility.FOUR_SECONDS);
			Utility.updateEffect(le, AMP_1, StatusEffects.FIRE_RESISTANCE, 720);
		}
	}

	public static void doGloomMobZombifiedPiglin(LivingEntity le, BlockPos pos, long gameTime,
			ServerWorld serverLevel) {
		if (Utility.isOutside(pos, serverLevel)) {
			if (zombifiedPiglinTimer < gameTime) {
				zombifiedPiglinTimer = gameTime + 600;
				doGloomGrassTransform(pos, serverLevel);
				doGloomGroundTransform(le, serverLevel);
				if (serverLevel.toServerWorld().getRandom().nextInt(10000) == 42) {
					ZombifiedPiglinEntity ze = (ZombifiedPiglinEntity) le;
					ze.setAttacking(true);
				}
			} else {
				zombifiedPiglinTimer--; // when lots of zoglin speed up timer
			}
		}
	}

	public static void doGloomMonsters(LivingEntity le, BlockPos pos, long gameTime, ServerWorld serverLevel) {
		if (le instanceof AbstractSkeletonEntity) {
			doGloomMobSkeletons(le, pos, gameTime, serverLevel);
		} else if (le instanceof ZombieEntity) {
			doGloomMobZombies(le, gameTime, serverLevel);
		} else if (le instanceof CreeperEntity) {
			doGloomMobCreepers(le, gameTime, serverLevel);
		} else if (le instanceof PhantomEntity) {
			doGloomMobPhantoms(le, pos, gameTime, serverLevel);
		} else if (le instanceof SpiderEntity) {
			doGloomMobSpiders(le, pos, gameTime, serverLevel);
		} else if (le instanceof ZoglinEntity) {
			doGloomMobZoglins(le, pos, gameTime, serverLevel);
		} else if (le instanceof ZombifiedPiglinEntity) {
			doGloomMobZombifiedPiglin(le, pos, gameTime, serverLevel);
		}
	}

	public static void doGloomPlayer(ServerPlayerEntity p, BlockPos pos, ServerWorld serverLevel, float difficulty,
			int gloomType, int amplitude) {

		Block b = serverLevel.getBlockState(pos).getBlock();
		Block bBelow = serverLevel.getBlockState(pos.down()).getBlock();
		if (gloomHungerBlocks.contains(b) || gloomHungerBlocks.contains(bBelow)) {
			Utility.updateEffect((LivingEntity) p, AMP_1, StatusEffects.HUNGER, Utility.FOUR_SECONDS);
		}
		doGloomPlayerCurse(difficulty, gloomType, amplitude, p);
		if (GrimCitadelManager.isGCNear(difficulty)) {
			Utility.slowFlyingMotion(p);
			if (p.isFallFlying()) {
				Utility.updateEffect((LivingEntity) p, AMP_1, StatusEffects.POISON, Utility.FOUR_SECONDS);
			}
		}
		if ((difficulty > Utility.Pct09) && (serverLevel.getRandom().nextInt(36000) == 42)) {
			BlockPos phantomPos = new BlockPos(pos.getX(),
					serverLevel.getTopPosition(Type.MOTION_BLOCKING, pos).getY() + 12, pos.getZ());
			Utility.populateEntityType(EntityType.PHANTOM, serverLevel, phantomPos, 1, 0);
		}
	}

	public static void doGloomVillagers(VillagerEntity ve, BlockPos pos, long gameTime, ServerWorld serverLevel) {
		if (MyConfig.isGrimEffectVillagers()) {
			if (villagerTimer < gameTime) {
				villagerTimer = gameTime + 2400;
				Utility.populateEntityType(EntityType.WITCH, serverLevel, pos, 1, 0);
				Utility.updateEffect(ve, 9, StatusEffects.WITHER, 240);
			}
		}
	}

	public static void doGloomGrassTransform(BlockPos pos, ServerWorld serverLevel) {
		BlockPos workPos = pos;
		if (serverLevel.getBlockState(pos.down()).getBlock() == Blocks.AIR) {
			workPos = pos.down();
		}
		if ((serverLevel.getBlockState(pos).getBlock() instanceof FernBlock)
				|| (serverLevel.getBlockState(workPos).getBlock() instanceof TallPlantBlock)) {

			Block b = Blocks.NETHER_SPROUTS;
			if (serverLevel.getBlockState(workPos).getBlock() == Blocks.TALL_GRASS) {
				b = Blocks.CRIMSON_ROOTS;
			} else if (serverLevel.getBlockState(workPos).getBlock() == Blocks.LARGE_FERN) {
				b = Blocks.WARPED_ROOTS;
			}
			serverLevel.setBlockState(pos, b.getDefaultState(), 3);
		}

	}

	public static void doGloomGroundTransform(LivingEntity le, ServerWorld level) {
		if (level.getBlockState(le.getBlockPos().down()).getBlock() == Blocks.COARSE_DIRT) {
			level.setBlockState(le.getBlockPos().down(), Blocks.NETHERRACK.getDefaultState(), 3);
		}
		if (level.getBlockState(le.getBlockPos().down()).getBlock() == Blocks.GRASS_BLOCK) {
			level.setBlockState(le.getBlockPos().down(), Blocks.COARSE_DIRT.getDefaultState(), 3);
		}
	}

	public static void doGloomPlayerCurse(float difficulty, int gloomType, int amplitude, ServerPlayerEntity sp) {

		RandomGenerator rand = sp.getWorld().getRandom();
		boolean hasLifeHeart = sp.getInventory().contains(new ItemStack(ModItems.LIFE_HEART));

		if (hasLifeHeart) {
			if ((difficulty > Utility.Pct50) && (rand.nextInt(42) == 42)) {
				// System.out.println("regen");
				Utility.updateEffect((LivingEntity) sp, 0, StatusEffects.REGENERATION, Utility.FOUR_SECONDS);
			}
		}
		doGrimPlayerCurses(difficulty, gloomType, amplitude, sp, hasLifeHeart);
		doTimePlayerCurses(difficulty, gloomType, amplitude, sp);
	}

	private static void doTimePlayerCurses(float difficulty, int gloomType, int amplitude, ServerPlayerEntity sp) {
		if (gloomType == Glooms.TIME) {
			if (difficulty > Utility.Pct25)
				Utility.updateEffect((LivingEntity) sp, AMP_1, StatusEffects.WEAKNESS, Utility.FOUR_SECONDS);
			if (difficulty > Utility.Pct75)
				Utility.updateEffect((LivingEntity) sp, amplitude, StatusEffects.SLOWNESS, Utility.FOUR_SECONDS);
		}
	}

	private static void doGrimPlayerCurses(float difficulty, int gloomType, int amplitude, ServerPlayerEntity sp,
			boolean hasLifeHeart) {
		if (gloomType == Glooms.GRIM) {
			if (difficulty > Utility.Pct00)
				Utility.updateEffect((LivingEntity) sp, AMP_1, StatusEffects.WEAKNESS, Utility.FOUR_SECONDS);
			if (hasLifeHeart) {
				if ((difficulty > Utility.Pct25) && (difficulty < Utility.Pct91))
					Utility.updateEffect((LivingEntity) sp, amplitude, StatusEffects.SLOWNESS,
							Utility.FOUR_SECONDS);
				if ((difficulty > Utility.Pct50) && (difficulty < Utility.Pct84))
					Utility.updateEffect((LivingEntity) sp, amplitude, StatusEffects.MINING_FATIGUE, Utility.FOUR_SECONDS);

			} else {
				if (difficulty > Utility.Pct09)
					Utility.updateEffect((LivingEntity) sp, amplitude, StatusEffects.SLOWNESS,
							Utility.FOUR_SECONDS);
				if ((difficulty > Utility.Pct50) && (difficulty < Utility.Pct95))
					Utility.updateEffect((LivingEntity) sp, amplitude, StatusEffects.MINING_FATIGUE, Utility.FOUR_SECONDS);
			}
		}
	}

	public static void doResetTimers() {
		pigTimer = 0;
		fishTimer = 0;

		villagerTimer = 0;
		phantomTimer = 0;
		invisTimer = 0;
		skeletonTimer = 0;
		spiderTimer = 0;
		spiderWebTimer = 0;
		zoglinTimer = 0;
		zombifiedPiglinTimer = 0;
		zombieTimer = 0;
		witherSkeletonTimer = 0;
		creeperTimer = 0;
	}

}
