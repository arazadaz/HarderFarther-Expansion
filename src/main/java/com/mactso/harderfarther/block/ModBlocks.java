package com.mactso.harderfarther.block;

import com.mactso.harderfarther.Main;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;


public class ModBlocks
{
	public static final Block GRIM_HEART = 
			new GrimHeartBlock(AbstractBlock
					.Settings.of(Material.ORGANIC_PRODUCT).luminance((state) -> { return 7;}).sounds(BlockSoundGroup.METAL), ParticleTypes.FLAME
					);
	
	public static final Block GRIM_GATE = 
			new GrimGateBlock(AbstractBlock
					.Settings.of(Material.GLASS).breakInstantly().nonOpaque().luminance((state) -> { return 3;}).sounds(BlockSoundGroup.GLASS)
					);
	
//	   public static final Block WALL_TORCH = register("wall_torch", new WallTorchBlock(BlockBehaviour
//			   .Properties.of(Material.DECORATION).noCollission().instabreak().lightLevel((p_152607_) -> {
//		      return 14;   }).sound(SoundType.WOOD).dropsLike(TORCH), ParticleTypes.FLAME));
	public static final Block DEAD_BRANCHES = new LeavesBlock(AbstractBlock.Settings.of(Material.LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque().suffocates(ModBlocks::never).blockVision(ModBlocks::never));

	
	public static void register()
	{

		Registry.register(Registry.BLOCK, new Identifier(Main.MODID, "grim_heart"), GRIM_HEART);
		Registry.register(Registry.BLOCK, new Identifier(Main.MODID, "grim_gate"), GRIM_GATE);
		Registry.register(Registry.BLOCK, new Identifier(Main.MODID, "dead_branches"), DEAD_BRANCHES);

	}


   private static Boolean never(BlockState p_50779_, BlockView p_50780_, BlockPos p_50781_) {
      return (boolean)false;
   }

// Need to figure out alternative still
/*	@Environment(EnvType.CLIENT)
	public static void setRenderLayer()
	{
		RenderLayers.setRenderLayer(GRIM_GATE, RenderLayer.getTranslucent());
	
	}*/
}