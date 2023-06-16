package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.config.OreConfig;
import com.mactso.harderfarther.api.DifficultyCalculator;
import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.utility.Utility;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(OreFeature.class)
public class ChunkGenOrePlacementMixin {

    private boolean areListInitialized = false;

    private ArrayList<Float> difficultySectionNumbers = new ArrayList<>();
    private ArrayList<List<String>> difficultySectionOres = new ArrayList<>();


    @Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/world/gen/feature/OreFeature;place(Lnet/minecraft/world/gen/feature/util/FeatureContext;)Z", cancellable = true)
    private void onGenerate(FeatureContext<OreFeatureConfig> context, CallbackInfoReturnable<Boolean> cir) {

        //return false if generation is not allowed.
        //return true if generation is allowed
        //context contains ore to be placed among other things like world & blockpos & config
        //config is the block


        if(!areListInitialized) {
            synchronized (this) {
                if (!areListInitialized) {

                    OreConfig.getDifficultySections().forEach(section -> {
                        difficultySectionNumbers.add(section.getLeft());
                        difficultySectionOres.add(section.getRight());
                    });


                    areListInitialized = true;
                }
            }
        }
        //end of listInitialization






        ServerWorld world = context.getWorld().toServerWorld();

        if(world.getRegistryKey() == World.OVERWORLD){

            BlockPos pos = context.getOrigin();
            String block = context.getConfig().targets.get(0).state.getBlock().toString().substring(6);
            block = block.substring(0, block.length()-1);


            float difficulty = DifficultyCalculator.getDistanceDifficultyHere(world, new Vec3d(pos.getX(), pos.getY(), pos.getZ())) * 100;

            int[] choosenAreaIndex = {-1};
            difficultySectionNumbers.forEach(difficultySectionNumber -> {
                if(difficulty >= difficultySectionNumber) choosenAreaIndex[0]++;
            });


            //default to alllow all ores if list is empty. - .isEmpty doesn't work as it seems initialized with empty strings.
            if(difficultySectionOres.get(choosenAreaIndex[0]).get(0).equals("")){
                return;
            }

            if(!difficultySectionOres.get(choosenAreaIndex[0]).contains(block)){
                if(PrimaryConfig.getDebugLevel() > 0){
                    Utility.debugMsg(1, "Harder Farther cancled ore: " + block);
                }
                cir.setReturnValue(false);
            }


        }

    }

}
