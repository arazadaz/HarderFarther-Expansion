package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.manager.HarderFartherManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Holder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.structure.StructureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OreFeature.class)
public class ChunkGenOrePlacementMixin {


    @Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/world/gen/feature/OreFeature;place(Lnet/minecraft/world/gen/feature/util/FeatureContext;)Z", cancellable = true)
    private void onGenerate(FeatureContext<OreFeatureConfig> context, CallbackInfoReturnable<Boolean> cir) {

        //return false if generation is not allowed.
        //return true if generation is allowed
        //context contains ore to be placed among other things like world & blockpos & config
        //config is the block

        ServerWorld world = context.getWorld().toServerWorld();


        if(world.getRegistryKey() == World.OVERWORLD){

            BlockPos pos = context.getOrigin();
            HarderFartherManager.getDistanceDifficultyHere(world, new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
            String block = context.getConfig().targets.get(0).state.getBlock().toString().substring(6);
            block = block.substring(0, block.length()-1);

            //System.out.print(block);

            if(!block.equals("minecraft:iron_ore")){
                //System.out.print(block);
                cir.setReturnValue(false);
            }

            //System.out.println(block);

        }

    }

}
