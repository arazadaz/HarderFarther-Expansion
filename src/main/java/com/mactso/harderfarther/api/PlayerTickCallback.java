package com.mactso.harderfarther.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

public interface PlayerTickCallback {

    Event<PlayerTickCallback> EVENT = EventFactory.createArrayBacked(PlayerTickCallback.class,
            (listeners) -> (player) -> {
                for (PlayerTickCallback listener : listeners) {
                    InteractionResult result = listener.interact(player);

                    if(result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    InteractionResult interact(Player player);

}
