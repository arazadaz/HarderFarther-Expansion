package com.mactso.harderfarther.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public interface PlaceBlockCallback {

    Event<PlaceBlockCallback> EVENT = EventFactory.createArrayBacked(PlaceBlockCallback.class,
            (listeners) -> (context, state) -> {
                for (PlaceBlockCallback listener : listeners) {
                    InteractionResult result = listener.interact(context, state);

                    if(result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    InteractionResult interact(BlockPlaceContext context, BlockState state);
}

