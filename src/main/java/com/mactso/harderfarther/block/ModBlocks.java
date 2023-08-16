package com.mactso.harderfarther.block;

import com.mactso.harderfarther.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;


public class ModBlocks
{
	public static final Block GRIM_HEART = 
			new GrimHeartBlock(BlockBehaviour
					.Properties.of(Material.CLAY).lightLevel((state) -> { return 7;}).sound(SoundType.METAL), ParticleTypes.FLAME
					);
	
	public static final Block GRIM_GATE = 
			new GrimGateBlock(BlockBehaviour
					.Properties.of(Material.GLASS).instabreak().noOcclusion().lightLevel((state) -> { return 3;}).sound(SoundType.GLASS)
					);
	
//	   public static final Block WALL_TORCH = register("wall_torch", new WallTorchBlock(BlockBehaviour
//			   .Properties.of(Material.DECORATION).noCollission().instabreak().lightLevel((p_152607_) -> {
//		      return 14;   }).sound(SoundType.WOOD).dropsLike(TORCH), ParticleTypes.FLAME));
	public static final Block DEAD_BRANCHES = new LeavesBlock(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never));

	
	public static void register()
	{

		Registry.register(Registry.BLOCK, new ResourceLocation(Main.MODID, "grim_heart"), GRIM_HEART);
		Registry.register(Registry.BLOCK, new ResourceLocation(Main.MODID, "grim_gate"), GRIM_GATE);
		Registry.register(Registry.BLOCK, new ResourceLocation(Main.MODID, "dead_branches"), DEAD_BRANCHES);

	}


   private static Boolean never(BlockState p_50779_, BlockGetter p_50780_, BlockPos p_50781_) {
      return (boolean)false;
   }

// Need to figure out alternative still
/*	@Environment(EnvType.CLIENT)
	public static void setRenderLayer()
	{
		RenderLayers.setRenderLayer(GRIM_GATE, RenderLayer.getTranslucent());
	
	}*/
}