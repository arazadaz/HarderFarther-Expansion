package com.mactso.harderfarther.blockentities;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities
{

	public static final BlockEntityType<GrimHeartBlockEntity> GRIM_HEART = BlockEntityType.Builder.of(GrimHeartBlockEntity::new, ModBlocks.GRIM_HEART).build(null);

	public static void register()
	{
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(Main.MODID, "grim_heart"), GRIM_HEART);
	}
}
