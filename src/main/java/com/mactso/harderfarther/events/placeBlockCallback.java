package com.mactso.harderfarther.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;

public interface placeBlockCallback {

    Event<placeBlockCallback> EVENT = EventFactory.createArrayBacked(placeBlockCallback.class,
            (listeners) -> (context, state) -> {
                for (placeBlockCallback listener : listeners) {
                    ActionResult result = listener.interact(context, state);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult interact(ItemPlacementContext context, BlockState state);
}

