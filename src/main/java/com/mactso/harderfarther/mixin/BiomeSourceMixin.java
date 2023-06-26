package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.mixinInterfaces.IExtendedBiomeSourceHF;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.source.BiomeSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BiomeSource.class)
public class BiomeSourceMixin implements IExtendedBiomeSourceHF {

    private ServerWorld serverWorld; //Dirty because it isn't synced with the original world. Works just fine for all biome intent & purposes.
    private boolean init;

    private Vec3d overworldSpawn;

    @Override
    public void setWorld(ServerWorld dirtyWorld) {
        this.serverWorld = dirtyWorld;
    }

    @Override
    public ServerWorld getWorld() {
        return this.serverWorld;
    }

    public void setInit(boolean i){
        if(i){
            this.init = i;
        }
    }

    public boolean getInit(){
        return this.init;
    }


}
