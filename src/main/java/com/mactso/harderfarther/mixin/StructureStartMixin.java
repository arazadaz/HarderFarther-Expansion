package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.config.OreConfig;
import com.mactso.harderfarther.manager.HarderFartherManager;
import com.mactso.harderfarther.mixinInterfaces.IExtendedBiomeSourceHF;
import com.mactso.harderfarther.mixinInterfaces.IExtendedChunkRegion;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(StructureStart.class)
public class StructureStartMixin {

    private boolean areListInitialized = false;

    private ArrayList<Float> difficultySectionNumbers = new ArrayList<>();
    private ArrayList<List<String>> difficultySectionStructure = new ArrayList<>();

    @Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/structure/StructureStart;placeInChunk(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/structure/StructureManager;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/util/random/RandomGenerator;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;)V", cancellable = true)
    private void onPlace(StructureWorldAccess world, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomGenerator random, BlockBox boundingBox, ChunkPos chunkPos, CallbackInfo ci){

        if(!areListInitialized){

            OreConfig.getDifficultySections().forEach(section ->{
                difficultySectionNumbers.add(section.first);
                difficultySectionStructure.add(section.second);
            });


            areListInitialized = true;
        }
        //end of listInitialization





        int x = BiomeCoords.fromChunk(chunkPos.getCenterX());
        int z = BiomeCoords.fromChunk(chunkPos.getCenterZ());

        String structure = ((IExtendedChunkRegion)world).getGeneratingStructureResourceKey();
        structure = structure.substring(0, structure.length()-1);
        structure = structure.split("/")[2].substring(1);

        System.out.println(structure);

        BiomeSource biomeSource = ((IExtendedChunkRegion)world).getBiomeSource();


        if(((IExtendedBiomeSourceHF)biomeSource).getInit()) {

            ServerWorld worldReal = ((IExtendedBiomeSourceHF) biomeSource).getDirtyWorld();


            float difficulty = HarderFartherManager.getDistanceDifficultyHere(worldReal, new Vec3d(x, 0, z)) * 100;

            int[] choosenAreaIndex = {-1};
            difficultySectionNumbers.forEach(difficultySectionNumber -> {
                if (difficulty >= difficultySectionNumber) choosenAreaIndex[0]++;
            });


            if (difficultySectionStructure.get(choosenAreaIndex[0]).get(0).equals("")) {
                return;
            }

            if (!difficultySectionStructure.get(choosenAreaIndex[0]).contains(structure)) {
                ci.cancel();
            }
        }

    }

}
