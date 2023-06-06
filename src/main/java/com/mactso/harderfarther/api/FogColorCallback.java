package com.mactso.harderfarther.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface FogColorCallback {

    Event<FogColorCallback> EVENT = EventFactory.createArrayBacked(FogColorCallback.class,
            (listeners) -> (fog) -> {
                for (FogColorCallback listener : listeners) {

                    listener.interact(fog);

                }
            });

    void interact(float fog[]);

}
