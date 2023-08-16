package com.mactso.harderfarther.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public interface DifficultyOverrideCallback {

    Event<DifficultyOverrideCallback> EVENT = EventFactory.createArrayBacked(DifficultyOverrideCallback.class,
            (listeners) -> (currentDifficulty, world, outposts, minBoostDistance, maxBoostDistance) -> {
        for (DifficultyOverrideCallback listener : listeners) {

            listener.interact(currentDifficulty, world, outposts, minBoostDistance, maxBoostDistance);

        }
    });

    void interact(float currentDifficulty[], ServerLevel world, Vec3[] outposts, int minBoostDistance, int maxBoostDistance);

}