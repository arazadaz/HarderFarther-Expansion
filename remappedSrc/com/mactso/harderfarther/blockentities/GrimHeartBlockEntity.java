package com.mactso.harderfarther.blockentities;

import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class GrimHeartBlockEntity extends BlockEntity {
	static Set<BlockPos> GrimHeartEntityPositions; 
	public GrimHeartBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.GRIM_HEART, pos, state);
	}
}
