package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.config.OreConfig;
import com.mactso.harderfarther.manager.HarderFartherManager;
import com.mactso.harderfarther.mixinInterfaces.IExtendedBiomeSourceHF;
import com.mactso.harderfarther.mixinInterfaces.IExtendedChunkRegion;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Holder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.StructureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGenStructurePlacementMixin {

    //private boolean areListInitialized = false;

    //private ArrayList<Float> difficultySectionNumbers = new ArrayList<>();
    //private ArrayList<List<String>> difficultySectionStructureSet = new ArrayList<>();

    /*@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/StructureSet;placement()Lnet/minecraft/structure/StructurePlacement;"), method = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;m_mzeyuzcs(Lnet/minecraft/util/Holder;Lnet/minecraft/world/gen/RandomState;JIII)Z", cancellable = true)
    private void onGenerate(Holder<StructureSet> holder, RandomState randomState, long l, int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {

        //i is x
        //j is z

        if(!areListInitialized){

            OreConfig.getDifficultySections().forEach(section ->{
                difficultySectionNumbers.add(section.first);
                difficultySectionStructureSet.add(section.second);
            });


            areListInitialized = true;
        }
        //end of listInitialization





        if(((IExtendedBiomeSourceHF)this.getBiomeSource()).getInit()) {

            ServerWorld world = ((IExtendedBiomeSourceHF) this.getBiomeSource()).getDirtyWorld();

            String structureSet = holder.getKey().get().getValue().toString();
            System.out.println(structureSet);


            float difficulty = HarderFartherManager.getDistanceDifficultyHere(world, new Vec3d(i, 0, j)) * 100;

            int[] choosenAreaIndex = {-1};
            difficultySectionNumbers.forEach(difficultySectionNumber -> {
                if(difficulty >= difficultySectionNumber) choosenAreaIndex[0]++;
            });


            if(difficultySectionStructureSet.get(choosenAreaIndex[0]).get(0).equals("")){
                return;
            }

            if(!difficultySectionStructureSet.get(choosenAreaIndex[0]).contains(structureSet)){
                cir.setReturnValue(false);
            }

        }




    }*/

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/StructureManager;getStructureStarts(Lnet/minecraft/util/math/ChunkSectionPos;Lnet/minecraft/world/gen/feature/StructureFeature;)Ljava/util/List;"), method = "generateFeatures")
    private void onGenerate(StructureWorldAccess world, Chunk chunk, StructureManager structureManager, CallbackInfo ci){

        ((IExtendedChunkRegion)world).setBiomeSource(this.getBiomeSource());

    }


    @Shadow
    public BiomeSource getBiomeSource(){
        return null;
    }


}
