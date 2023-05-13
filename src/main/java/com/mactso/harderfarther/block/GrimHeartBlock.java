package com.mactso.harderfarther.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import com.mactso.harderfarther.blockentities.GrimHeartBlockEntity;
import com.mactso.harderfarther.client.PlayGrimSongs;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.sounds.ModSounds;

public class GrimHeartBlock extends BlockWithEntity {
	protected final ParticleEffect particle;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(7.0D, 7.0D, 7.0D, 10.0D, 10.0D, 10.0D);

	
	public GrimHeartBlock(Settings properties, ParticleEffect particleChoice) {
		super(properties);
	    this.particle = particleChoice;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new GrimHeartBlockEntity(pos, state);
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState p_60584_) {
		return PistonBehavior.DESTROY;
	}

	@Override
	public int getExpDrop(BlockState state, WorldView world, RandomGenerator rand, BlockPos pos, int fortune, int silktouch) {

		return 171;
	}

	@Override
	public void onStateReplaced(BlockState oldbs, World level, BlockPos pos, BlockState newbs, boolean moving) {

		if (level instanceof ServerWorld) {
			level.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE , SoundCategory.AMBIENT, 4.0f, 0.8f);
			// start flute music SoundEvent.
			GrimCitadelManager.removeHeart((ServerWorld) level, pos);
//			level.playSound(null, pos, ModSounds.LAKE_DESTINY, SoundSource.MUSIC, 2.0f, 1.0f);

		} else {
			if (level.isClient()) {
//				PlayGrimSongs.stopCurrentSong();
				PlayGrimSongs.playSong(ModSounds.LAKE_DESTINY);
			}			
		}
		super.onStateReplaced(oldbs, level, pos, newbs, moving);
	}
	
		// this is client side.
	   public void animateTick(BlockState bs, World level, BlockPos pos, Random rand) {
		      double d0 = (double)pos.getX() + 0.5D;
		      double d1 = (double)pos.getY() + 0.5D;
		      double d2 = (double)pos.getZ() + 0.5D;
		      double vx = (rand.nextDouble()-0.5)/64;
		      double vz = (rand.nextDouble()-0.5)/64;
		      double vy = (rand.nextDouble()-0.65)/64;
		      if (rand.nextInt(3) == 1) {
			      level.addParticle(ParticleTypes.FALLING_OBSIDIAN_TEAR, d0, d1, d2, vx, vy, vz);
		      }
		      vx = (rand.nextDouble()-0.5)/32;
		      vz = (rand.nextDouble()-0.5)/32;
		      vy = (rand.nextDouble()-0.55)/32;
    		  level.addParticle(this.particle, d0, d1, d2, vx, vy, vz);
	   }
}