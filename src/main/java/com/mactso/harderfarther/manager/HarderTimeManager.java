package com.mactso.harderfarther.manager;

import java.util.Arrays;
import java.util.List;

import com.mactso.harderfarther.config.PrimaryConfig;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FernBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.GravelBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.events.FogColorsEventHandler;

public class HarderTimeManager {

	static float pitch = 0.67f; // slower and longer

	static List<SoundEvent> spookySounds = Arrays.asList(SoundEvents.AMBIENT_CAVE, SoundEvents.ENTITY_WITCH_AMBIENT,
			SoundEvents.PARTICLE_SOUL_ESCAPE, SoundEvents.ENTITY_ZOMBIE_AMBIENT, SoundEvents.BLOCK_SOUL_SAND_STEP,
			SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD);

	private static void doClientParticles(PlayerEntity cp, RandomGenerator rand,
			DefaultParticleType p1,
			DefaultParticleType p2,
			DefaultParticleType p3,
			SoundEvent soundEvent) 
	{
		BlockPos pos = cp.getBlockPos();
		Vec3d lookv = cp.getRotationVecClient();
		if (rand.nextInt(20) == 1) {
			lookv = lookv.negate();
		}
		lookv.multiply(7.0);
		BlockPos pPos = new BlockPos(pos.getX()+lookv.x,pos.getY()+lookv.y,pos.getZ()+lookv.z);
		for (int k = 0; k < 5; ++k) {
			
			int xv = (rand.nextInt(7) - 4) * 2;
			int yv = (rand.nextInt(5) - 2)  ;
			int zv = (rand.nextInt(7) - 4) * 2;

			BlockPos temp = pPos.east(xv).up(yv).north(zv);
			for (int j = 0; j < 2; ++j) {

				double x = (double) temp.getX() + rand.nextDouble() * (double) 0.1F;
				double y = (double) temp.getY() + rand.nextDouble();
				double z = (double) temp.getZ() + rand.nextDouble();

				cp.world.addParticle(p1,  x, y, z, xv/3, yv/2, zv/2);
				cp.world.addParticle(p2,  x, y, z, xv/3, yv/2, zv/2);
				cp.world.addParticle(p3,  x, y, z, xv/3, yv/2, zv/2);
			}

			cp.world.playSound(cp, pPos, soundEvent, SoundCategory.AMBIENT, 0.95f, pitch);

		}
	}

	private static void doNiceAtmosphere(PlayerEntity cp) {

		RandomGenerator rand = cp.world.getRandom();

		
		float timeDifficulty = FogColorsEventHandler.getServerTimeDifficulty();

		if (timeDifficulty > 0) {
			return;
		}

		if (!cp.world.isSkyVisible(cp.getBlockPos())) {
			if (rand.nextInt(2400) == 43) {
				cp.world.playSound(cp, cp.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.AMBIENT, 0.95f, pitch);
			}	
			return;
		}
		

		int chance = 5;
		if (cp.world.isNight())
			chance += 3;
		if (rand.nextInt(1200) > (chance))
			return;

		doClientParticles(cp, rand,
				ParticleTypes.GLOW,
				ParticleTypes.SPORE_BLOSSOM_AIR,
				ParticleTypes.BUBBLE,
				SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME);
	}

	private static void doRandomScaryThings(ServerPlayerEntity sp) {

		ServerWorld sl = sp.getWorld();
		RandomGenerator rand = sl.getRandom();

		float timeDifficulty = getTimeDifficulty(sl, sp);
		if (timeDifficulty == 0)
			return;

		int chance = 37;
		if (sl.isNight())
			chance += 19;

		if (rand.nextInt(1800) > (chance))
			return;
		
		BlockPos pos = sp.getBlockPos();
		Vec3d lookv = sp.getRotationVecClient();
		if (rand.nextInt(20) == 1) {
			lookv = lookv.negate();
		}
		BlockPos pPos = new BlockPos(1+pos.getX()+(lookv.x*7),3+ pos.getY()+(lookv.y*3),1+pos.getZ()+(lookv.z*7));
		sl.spawnParticles(sp, ParticleTypes.SOUL, false, pPos.getX(), pPos.getY(), pPos.getZ(), 1, 0, 0.08d, 0, 1);

		for (int k = 0; k < 3; ++k) {

			int xv = (rand.nextInt(6) - 2) * 3;
			int yv = (rand.nextInt(5) - 2) * 2;
			int zv = (rand.nextInt(6) - 2) * 3;

			BlockPos temp = pPos.east(xv).up(yv).north(zv);
			BlockState bs = sl.getBlockState(temp);
			Block b = bs.getBlock();
			FluidState fs = sl.getFluidState(temp); 
//			if (fs.is(FluidTags.WATER)) {
//				sl.setBlock(temp, BlockState.MUD, 3);   // FOR 1.19.1
//			sl.setBlock(temp, BlockState.BLUE, 3);   // FOR 1.19.1

			//			}
			if (!bs.isAir()) {
				if ((b instanceof FernBlock) ) {
					sl.setBlockState(temp.down(), Blocks.COARSE_DIRT.getDefaultState(), 3);
					if (rand.nextInt(3)==1) {
						sl.setBlockState(temp, Blocks.CRIMSON_ROOTS.getDefaultState(), 3);
					} else {
						sl.setBlockState(temp, Blocks.WARPED_ROOTS.getDefaultState(), 3);
					}
				} else if (b instanceof CropBlock) {
					int r = rand.nextInt(4);
					if (r<=1) {
						sl.setBlockState(temp, Blocks.WARPED_ROOTS.getDefaultState(), 3);
					} else if (r==2){
						sl.setBlockState(temp, Blocks.CRIMSON_ROOTS.getDefaultState(), 3);
					} else {
						sl.setBlockState(temp, Blocks.DEAD_BUSH.getDefaultState(), 3);
						sl.setBlockState(temp.down(), Blocks.COARSE_DIRT.getDefaultState(), 3);
					}
				} else if (b instanceof FlowerBlock) {
					int r = rand.nextInt(6);
					if (r==0) {
						sl.setBlockState(temp, Blocks.WARPED_FUNGUS.getDefaultState(), 3);
					} else if (r<=2){
						sl.setBlockState(temp, Blocks.CRIMSON_FUNGUS.getDefaultState(), 3);
					} else {
						sl.setBlockState(temp, Blocks.TALL_GRASS.getDefaultState(), 3);
					}
				} else if (b instanceof LeavesBlock) {
					sl.setBlockState(temp, ModBlocks.DEAD_BRANCHES.getDefaultState(), 3);;
				} else if (b instanceof GrassBlock) {
					sl.setBlockState(temp, Blocks.COARSE_DIRT.getDefaultState(), 3);
					sl.setBlockState(temp.up(), Blocks.FIRE.getDefaultState(), 131);
					//FlammableBlockRegistry.getDefaultInstance().get(bs.getBlock()).getSpreadChance() > 0 means is flamable
				} else if ((rand.nextInt(8)==1) && (FlammableBlockRegistry.getDefaultInstance().get(bs.getBlock()).getSpreadChance() > 0)) {
					sl.setBlockState(temp, Blocks.FIRE.getDefaultState(), 131);
				}   else if (b instanceof SnowBlock) {
					sl.setBlockState(temp, Blocks.ICE.getDefaultState(), 3);
					sl.setBlockState(temp.up(), Blocks.SOUL_FIRE.getDefaultState(), 131);
				} else if (bs.isIn(BlockTags.BASE_STONE_OVERWORLD)) {
					if (!sl.isSkyVisible(pos)) {
						sl.setBlockState(temp, Blocks.GRAVEL.getDefaultState(),3);
						sl.playSound(null, temp, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.AMBIENT, 0.11f, pitch);
					}
				} else if (b instanceof GravelBlock) {
					sl.setBlockState(temp.up(), Blocks.GRAVEL.getDefaultState(),3);
					sl.playSound(null, temp.up(), SoundEvents.ENTITY_SILVERFISH_STEP, SoundCategory.AMBIENT, 0.11f, pitch);
				} 
			}

		}
	}

	public static void doScarySpookyThings(PlayerEntity p) {
		if (p.world.isClient) {
			doNiceAtmosphere(p);
			doSpookyAtmosphere(p);
			return;
		} 
		doRandomScaryThings((ServerPlayerEntity) p);
	}

	// clientside
	private static void doSpookyAtmosphere(PlayerEntity cp) {

		RandomGenerator rand = cp.world.getRandom();

		float timeDifficulty = FogColorsEventHandler.getServerTimeDifficulty();
		if (timeDifficulty == 0)
			return;

		int chance = 5;
		if (cp.world.isNight())
			chance -= 3;
		if (rand.nextInt(320) > (chance))
			return;
		
		int i = rand.nextInt(spookySounds.size());
		cp.world.playSound(cp, cp.getBlockPos(), spookySounds.get(i), SoundCategory.AMBIENT, 0.12f, pitch);
		if (rand.nextInt(100) == 42) {
			cp.world.playSound(cp, cp.getBlockPos(), SoundEvents.ENTITY_GOAT_SCREAMING_PREPARE_RAM, SoundCategory.AMBIENT, 0.12f, pitch);
		}
		if (rand.nextInt(100) < 5) 
			cp.world.playSound(cp, cp.getBlockPos(), SoundEvents.AMBIENT_CAVE, SoundCategory.AMBIENT, 0.23f, 0.66f);
		doClientParticles(cp, rand,
				ParticleTypes.LARGE_SMOKE,
				ParticleTypes.SMALL_FLAME,
				ParticleTypes.SOUL,
				SoundEvents.PARTICLE_SOUL_ESCAPE);

	}

	// must be server side.  chunk inhabited time is 0 on client side.
	public static float getTimeDifficulty(ServerWorld level, LivingEntity entity) {
		if (!PrimaryConfig.isMakeHarderOverTime())
			return 0;

		long startHarderTime = (long) (PrimaryConfig.getMaxHarderTimeMinutes() *.66f);
		long inhabitedMinutes = level.getChunk(entity.getBlockPos()).getInhabitedTime() / 1200; // 60 sec * 20

		if (inhabitedMinutes < startHarderTime)
			return 0;

		long minutes = inhabitedMinutes - startHarderTime;
		float timeDifficulty = Math.min(1.0f, (float) minutes / startHarderTime);
		timeDifficulty = (float) Math.max(0.33, timeDifficulty);
		return timeDifficulty;
	}
	
}
