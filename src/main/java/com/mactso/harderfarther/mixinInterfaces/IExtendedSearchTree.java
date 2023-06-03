package com.mactso.harderfarther.mixinInterfaces;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.List;

public interface IExtendedSearchTree<T> {

    List<Pair<MultiNoiseUtil.NoiseHypercube, T>> getOriginalList();

    void setOriginalList(List<Pair<MultiNoiseUtil.NoiseHypercube, T>> entries);


}
