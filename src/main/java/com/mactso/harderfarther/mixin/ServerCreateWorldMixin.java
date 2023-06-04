package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.mixinInterfaces.IExtendedBiomeSourceHF;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class ServerCreateWorldMixin {

    @Inject(at = @At(value = "TAIL"), method = "createWorlds", cancellable = false)
    private void onGenerate(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {

        for(ServerWorld serverWorld : this.getWorlds()) {
            ((IExtendedBiomeSourceHF)serverWorld.getChunkManager().getChunkGenerator().getBiomeSource()).setDirtyWorld(serverWorld);
            ((IExtendedBiomeSourceHF)serverWorld.getChunkManager().getChunkGenerator().getBiomeSource()).setInit(true);
            System.out.println("hi");
        }

    }

    @Shadow
    public Iterable<ServerWorld> getWorlds(){
        return null;
    }

}
