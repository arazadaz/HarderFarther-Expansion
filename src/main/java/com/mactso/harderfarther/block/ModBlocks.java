package com.mactso.harderfarther.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
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

	
	public static void register(IForgeRegistry<Block> forgeRegistry	)
	{

		
		forgeRegistry.register("grim_heart",GRIM_HEART);
		forgeRegistry.register("grim_gate",GRIM_GATE);
		forgeRegistry.register("dead_branches",DEAD_BRANCHES);


	}


   private static Boolean never(BlockState p_50779_, BlockView p_50780_, BlockPos p_50781_) {
      return (boolean)false;
   }
	
	@OnlyIn(Dist.CLIENT)
	public static void setRenderLayer()
	{
		RenderLayers.setRenderLayer(GRIM_GATE, RenderLayer.getTranslucent());
	
	}
}