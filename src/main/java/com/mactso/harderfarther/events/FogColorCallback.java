package com.mactso.harderfarther.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface FogColorCallback {

    Event<FogColorCallback> EVENT = EventFactory.createArrayBacked(FogColorCallback.class,
            (listeners) -> (f, g, h, i) -> {
                for (FogColorCallback listener : listeners) {
                    float[] newColor = listener.interact(f, g, h, i);

                    return newColor;

                }

                return new float[]{1.0F, 1.0F, 1.0F, 1.0F};
            });

    float[] interact(float f, float g, float h, float i);

}
