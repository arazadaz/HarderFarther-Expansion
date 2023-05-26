package com.mactso.harderfarther.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

public interface ServerWorldTickCallback {

    Event<ServerWorldTickCallback> EVENT = EventFactory.createArrayBacked(ServerWorldTickCallback.class,
            (listeners) -> (player) -> {
                for (ServerWorldTickCallback listener : listeners) {
                    ActionResult result = listener.interact(player);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult interact(ServerWorld world);

}
