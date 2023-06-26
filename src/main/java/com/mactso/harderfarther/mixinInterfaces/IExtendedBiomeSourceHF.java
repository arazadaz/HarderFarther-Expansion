package com.mactso.harderfarther.mixinInterfaces;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface IExtendedBiomeSourceHF {

    void setWorld(ServerWorld dirtyWorld);

    ServerWorld getWorld();

    void setInit(boolean i);

    boolean getInit();


}
