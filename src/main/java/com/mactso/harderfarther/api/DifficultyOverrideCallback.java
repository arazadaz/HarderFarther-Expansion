package com.mactso.harderfarther.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface DifficultyOverrideCallback {

    Event<DifficultyOverrideCallback> EVENT = EventFactory.createArrayBacked(DifficultyOverrideCallback.class,
            (listeners) -> (currentDifficulty, world, outposts, minBoostDistance, maxBoostDistance) -> {
        for (DifficultyOverrideCallback listener : listeners) {

            listener.interact(currentDifficulty, world, outposts, minBoostDistance, maxBoostDistance);

        }
    });

    void interact(float currentDifficulty[], ServerWorld world, Vec3d[] outposts, int minBoostDistance, int maxBoostDistance);

}