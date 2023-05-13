package com.mactso.harderfarther.block;

import com.mactso.harderfarther.block.properties.GrimGateType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TransparentBlock;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GrimGateBlock extends TransparentBlock {
	public static final EnumProperty<GrimGateType> TYPE = EnumProperty.of("type", GrimGateType.class);

	@Override
	public void neighborUpdate(BlockState myBlockState, World level, BlockPos myPos, Block neighborOldBlock,
			BlockPos neighborPos, boolean pushing) {
		Block neighborNewBlock = level.getBlockState(neighborPos).getBlock();
		if ((neighborNewBlock == Blocks.AIR) && (neighborOldBlock == ModBlocks.GRIM_GATE)) {
			level.breakBlock(myPos, false);
		}
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(TYPE);
	}

	public GrimGateBlock(Settings prop) {
		super(prop);
        setDefaultState(getStateManager().getDefaultState().with(TYPE, GrimGateType.FLOOR));
	}
}
