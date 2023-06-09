package com.mactso.harderfarther.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.config.StructureConfig;
import com.mactso.harderfarther.api.DifficultyCalculator;
import com.mactso.harderfarther.mixinInterfaces.IExtendedBiomeSourceHF;
import com.mactso.harderfarther.mixinInterfaces.IExtendedChunkRegion;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mactso.harderfarther.api.DifficultyCalculator.getNearestOutpost;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGenStructurePlacementMixin {

    private boolean shouldgen;

    private boolean areListInitialized = false;

    private ArrayList<Float> difficultySectionNumbers = new ArrayList<>();
    private ArrayList<List<String>> difficultySectionStructure = new ArrayList<>();

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

    @WrapWithCondition(at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"), method = "generateFeatures")
    private boolean harderfarther$skipGenerationIfStructureDisabled(List instance, Consumer consumer){
        return this.shouldgen;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/StructureManager;getStructureStarts(Lnet/minecraft/util/math/ChunkSectionPos;Lnet/minecraft/world/gen/feature/StructureFeature;)Ljava/util/List;"), method = "generateFeatures")
    private void harderfarther$setShouldGenStructure(StructureWorldAccess world, Chunk chunk, StructureManager structureManager, CallbackInfo ci){

        //((IExtendedChunkRegion)world).setBiomeSource(this.getBiomeSource());

        if(!areListInitialized) {
            synchronized (this) {
                if (!areListInitialized) {

                    StructureConfig.getDifficultySections().forEach(section -> {
                        difficultySectionNumbers.add(section.first);
                        difficultySectionStructure.add(section.second);
                    });


                    areListInitialized = true;
                }
            }
        }
        //end of listInitialization











        this.shouldgen = true;
        if (((IExtendedBiomeSourceHF) this.getBiomeSource()).getInit()) {

            ServerWorld worldReal = ((IExtendedBiomeSourceHF) this.getBiomeSource()).getDirtyWorld();

            ChunkPos chunkPos = chunk.getPos();
            int x = BiomeCoords.fromChunk(chunkPos.getCenterX());
            int z = BiomeCoords.fromChunk(chunkPos.getCenterZ());

            String structure = ((IExtendedChunkRegion) world).getGeneratingStructureResourceKey();
            structure = structure.substring(0, structure.length() - 1);
            structure = structure.split("/")[2].substring(1);

            //System.out.println(structure);


            if (worldReal.getRegistryKey() == World.OVERWORLD) {


                float difficulty = DifficultyCalculator.getDistanceDifficultyHere(worldReal, new Vec3d(x, 0, z)) * 100;

                int[] choosenAreaIndex = {-1};
                difficultySectionNumbers.forEach(difficultySectionNumber -> {
                    if (difficulty >= difficultySectionNumber) choosenAreaIndex[0]++;
                });


                if (difficultySectionStructure.get(choosenAreaIndex[0]).get(0).equals("")) {
                    return;
                }

                if (!difficultySectionStructure.get(choosenAreaIndex[0]).contains(structure)) {
                    //System.out.println("cancled:" + structure);
                    this.shouldgen = false;
                }
            }

        }



        //repeat logic for spawn before worlds are initialized.
        if(!((IExtendedBiomeSourceHF) this.getBiomeSource()).getInit()){

            String structure = ((IExtendedChunkRegion) world).getGeneratingStructureResourceKey();
            structure = structure.substring(0, structure.length() - 1);
            structure = structure.split("/")[2].substring(1);


            Vec3d spawnVec = ((IExtendedBiomeSourceHF) this.getBiomeSource()).getOverworldSpawn();
            Vec3d location = new Vec3d(0, 0, 0);

            //Add spawn to outpost list if enabled & get nearest outpost
            Vec3d[] outposts = PrimaryConfig.getOutpostPositions().clone();
            if(PrimaryConfig.isSpawnAnOutpost()){
                outposts[0] = spawnVec;
            }

            Vec3d nearestOutpost = getNearestOutpost(outposts, location);

            float difficulty = DifficultyCalculator.calcDistanceModifier(location, nearestOutpost) * 100;

            int[] choosenAreaIndex = {-1};
            difficultySectionNumbers.forEach(difficultySectionNumber -> {
                if(difficulty >= difficultySectionNumber) choosenAreaIndex[0]++;
            });



            if (difficultySectionStructure.get(choosenAreaIndex[0]).get(0).equals("")) {
                return;
            }

            if (!difficultySectionStructure.get(choosenAreaIndex[0]).contains(structure)) {
                //System.out.println("cancled:" + structure);
                this.shouldgen = false;
            }

        }
    }



    @Shadow
    public BiomeSource getBiomeSource(){
        return null;
    }


}
