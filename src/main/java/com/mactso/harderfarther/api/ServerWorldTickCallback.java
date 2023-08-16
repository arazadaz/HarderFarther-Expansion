package com.mactso.harderfarther.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;

public interface ServerWorldTickCallback {

    Event<ServerWorldTickCallback> EVENT = EventFactory.createArrayBacked(ServerWorldTickCallback.class,
            (listeners) -> (player) -> {
                for (ServerWorldTickCallback listener : listeners) {
                    InteractionResult result = listener.interact(player);

                    if(result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    InteractionResult interact(ServerLevel world);

}
