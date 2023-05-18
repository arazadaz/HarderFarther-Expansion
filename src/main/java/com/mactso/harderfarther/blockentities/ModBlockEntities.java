package com.mactso.harderfarther.blockentities;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockEntities
{

	public static final BlockEntityType<GrimHeartBlockEntity> GRIM_HEART = BlockEntityType.Builder.create(GrimHeartBlockEntity::new, ModBlocks.GRIM_HEART).build(null);

	public static void register()
	{
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Main.MODID, "grim_heart"), GRIM_HEART);
	}
}
