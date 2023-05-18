package com.mactso.harderfarther.blockentities;

import com.mactso.harderfarther.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;

public class ModBlockEntities
{

	public static final BlockEntityType<GrimHeartBlockEntity> GRIM_HEART = BlockEntityType.Builder.create(GrimHeartBlockEntity::new, ModBlocks.GRIM_HEART).build(null);

	public static void register(IForgeRegistry<BlockEntityType<?>> forgeRegistry)
	{
		forgeRegistry.register("grim_heart",GRIM_HEART);
	}
}
