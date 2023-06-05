package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.mixinInterfaces.IExtendedBiomeSourceHF;
import com.mactso.harderfarther.mixinInterfaces.IExtendedChunkRegion;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Mixin(ChunkRegion.class)
public class ChunkRegionMixin implements IExtendedChunkRegion {

    private BiomeSource biomesource;

    @Shadow
    private @Nullable Supplier<String> f_avgybjko;

    @Override
    public String getGeneratingStructureResourceKey() {
        return this.f_avgybjko.get();
    }

    @Override
    public void setBiomeSource(BiomeSource biomeSource) {
        this.biomesource = biomeSource;
    }

    @Override
    public BiomeSource getBiomeSource() {
        return this.biomesource;
    }


}
