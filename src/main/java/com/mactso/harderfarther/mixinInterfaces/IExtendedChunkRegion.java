package com.mactso.harderfarther.mixinInterfaces;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.biome.source.BiomeSource;

public interface IExtendedChunkRegion {

    String getGeneratingStructureResourceKey();

    void setBiomeSource(BiomeSource biomesource);

    BiomeSource getBiomeSource();

}
