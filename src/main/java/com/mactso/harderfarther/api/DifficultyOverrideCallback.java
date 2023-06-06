package com.mactso.harderfarther.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface DifficultyOverrideCallback {

    Event<DifficultyOverrideCallback> EVENT = EventFactory.createArrayBacked(DifficultyOverrideCallback.class,
            (listeners) -> (difficulty, world, outposts, maxDistance) -> {
        for (DifficultyOverrideCallback listener : listeners) {

            listener.interact(difficulty, world, outposts, maxDistance);

        }
    });

    void interact(float difficulty[], ServerWorld world, Vec3d[] outposts, int maxDistance);

}