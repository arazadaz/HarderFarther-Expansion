package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.mixinInterfaces.IExtendedBiomeSourceHF;
import com.mactso.harderfarther.utility.Utility;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class ServerCreateWorldMixin {

    @Inject(at = @At(value = "TAIL"), method = "createWorlds", cancellable = false)
    private void harderfarther$onServerCreateWorlds(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {

        for(ServerWorld serverWorld : this.getWorlds()) {
            ((IExtendedBiomeSourceHF)serverWorld.getChunkManager().getChunkGenerator().getBiomeSource()).setDirtyWorld(serverWorld);
            ((IExtendedBiomeSourceHF)serverWorld.getChunkManager().getChunkGenerator().getBiomeSource()).setInit(true);
            if(PrimaryConfig.getDebugLevel() > 0) {
                Utility.debugMsg(1, "World " + serverWorld.getRegistryKey().getValue() + " initialized");
            }
        }

    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getPersistentStateManager()Lnet/minecraft/world/PersistentStateManager;"), method = "createWorlds", cancellable = false)
    private void harderfarther$onServerCreateOverworld(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {

        for(ServerWorld serverWorld : this.getWorlds()) {
            double xzf = serverWorld.getDimension().coordinateScale();
            if (xzf == 0.0) {
                xzf = 1.0d;
            }
            WorldProperties winfo = serverWorld.getLevelProperties();
            Vec3d spawnVec = new Vec3d(winfo.getSpawnX() / xzf, winfo.getSpawnY(), winfo.getSpawnZ() / xzf);
            ((IExtendedBiomeSourceHF)serverWorld.getChunkManager().getChunkGenerator().getBiomeSource()).setOverworldSpawn(spawnVec);
        }

    }

    @Shadow
    public Iterable<ServerWorld> getWorlds(){
        return null;
    }

}
