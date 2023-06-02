package com.mactso.harderfarther.mixin;

import net.minecraft.util.Holder;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.StructureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerator.class)
public class ChunkGenStructurePlacementMixin {


    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/StructureSet;placement()Lnet/minecraft/structure/StructurePlacement;"), method = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;m_mzeyuzcs(Lnet/minecraft/util/Holder;Lnet/minecraft/world/gen/RandomState;JIII)Z", cancellable = false)
    private void onGenerate(Holder<StructureSet> holder, RandomState randomState, long l, int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {

        //i is x
        //j is z

    }

}
