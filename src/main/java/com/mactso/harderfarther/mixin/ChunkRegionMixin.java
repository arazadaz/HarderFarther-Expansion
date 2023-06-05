package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.mixinInterfaces.IExtendedChunkRegion;
import net.minecraft.world.ChunkRegion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Mixin(ChunkRegion.class)
public class ChunkRegionMixin implements IExtendedChunkRegion {

    @Shadow
    private @Nullable Supplier<String> f_avgybjko;

    @Override
    public String getGeneratingStructureResourceKey() {
        return this.f_avgybjko.get();
    }


}
