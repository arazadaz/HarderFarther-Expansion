package com.mactso.harderfarther.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.InteractionResult;

public interface FogRenderCallback {

    Event<FogRenderCallback> EVENT = EventFactory.createArrayBacked(FogRenderCallback.class,
            (listeners) -> (camera, fogType, viewDistance, thickFog, tickDelta) -> {
                for (FogRenderCallback listener : listeners) {
                    InteractionResult result = listener.interact(camera, fogType, viewDistance, thickFog, tickDelta);

                    if(result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    InteractionResult interact(Camera camera, FogRenderer.FogMode fogType, float viewDistance, boolean thickFog, float tickDelta);

}
