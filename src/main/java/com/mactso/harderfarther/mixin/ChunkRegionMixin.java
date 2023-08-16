package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.mixinInterfaces.IExtendedChunkRegion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;
import net.minecraft.server.level.WorldGenRegion;

//I think I can delete this, but I'll hold onto it for now.
@Mixin(WorldGenRegion.class)
public class ChunkRegionMixin implements IExtendedChunkRegion {

    @Shadow
    private @Nullable Supplier<String> currentlyGenerating;

    @Override
    public String getGeneratingStructureResourceKey() {
        return this.currentlyGenerating.get();
    }


}
