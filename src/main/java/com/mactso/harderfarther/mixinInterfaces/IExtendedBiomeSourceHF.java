package com.mactso.harderfarther.mixinInterfaces;

import net.minecraft.server.world.ServerWorld;

public interface IExtendedBiomeSourceHF {

    void setDirtyWorld(ServerWorld dirtyWorld);

    ServerWorld getDirtyWorld();

    void setInit(boolean i);

    boolean getInit();

}
