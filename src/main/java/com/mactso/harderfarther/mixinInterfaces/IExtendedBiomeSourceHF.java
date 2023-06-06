package com.mactso.harderfarther.mixinInterfaces;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface IExtendedBiomeSourceHF {

    void setDirtyWorld(ServerWorld dirtyWorld);

    ServerWorld getDirtyWorld();

    void setInit(boolean i);

    boolean getInit();

    public void setOverworldSpawn(Vec3d spawn);

    public Vec3d getOverworldSpawn();

}
