package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.mixinInterfaces.IExtendedSearchTree;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MultiNoiseUtil.SearchTree.class)
public class SearchTreeMixin<T>  implements IExtendedSearchTree<T> {

    private List<Pair<MultiNoiseUtil.NoiseHypercube, T>> originalBiomePairs;

    @Inject(at = @At(value = "RETURN"), method = "Lnet/minecraft/world/biome/source/util/MultiNoiseUtil$SearchTree;create(Ljava/util/List;)Lnet/minecraft/world/biome/source/util/MultiNoiseUtil$SearchTree;", cancellable = true)
    private static <T> void onCreate(List<Pair<MultiNoiseUtil.NoiseHypercube, T>> entries, CallbackInfoReturnable<MultiNoiseUtil.SearchTree<T>> cir) {
        MultiNoiseUtil.SearchTree searchTree = cir.getReturnValue();
        ((IExtendedSearchTree)(Object)searchTree).setOriginalList(entries);
        cir.setReturnValue(searchTree);
    }


    @Override
    public List<Pair<MultiNoiseUtil.NoiseHypercube, T>> getOriginalList() {
        return this.originalBiomePairs;
    }

    @Override
    public void setOriginalList(List entries) {
        this.originalBiomePairs = entries;
    }

}
