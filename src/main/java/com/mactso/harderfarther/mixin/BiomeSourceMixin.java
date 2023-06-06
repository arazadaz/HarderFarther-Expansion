package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.mixinInterfaces.IExtendedBiomeSourceHF;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.source.BiomeSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BiomeSource.class)
public class BiomeSourceMixin implements IExtendedBiomeSourceHF {

    private ServerWorld dirtyWorld; //Dirty because it isn't synced with the original world. Works just fine for all biome intent & purposes.
    private boolean init;

    private Vec3d overworldSpawn;

    @Override
    public void setDirtyWorld(ServerWorld dirtyWorld) {
        this.dirtyWorld = dirtyWorld;
    }

    @Override
    public ServerWorld getDirtyWorld() {
        return this.dirtyWorld;
    }

    public void setInit(boolean i){
        if(i){
            this.init = i;
        }
    }

    public boolean getInit(){
        return this.init;
    }

    public void setOverworldSpawn(Vec3d spawn){
        this.overworldSpawn = spawn;
    }

    public Vec3d getOverworldSpawn(){
        return this.overworldSpawn;
    }

}
