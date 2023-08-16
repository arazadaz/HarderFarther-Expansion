package com.mactso.harderfarther.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;

public interface LivingEntityTickCallback {

    Event<LivingEntityTickCallback> EVENT = EventFactory.createArrayBacked(LivingEntityTickCallback.class,
            (listeners) -> (entity) -> {
                for (LivingEntityTickCallback listener : listeners) {
                    InteractionResult result = listener.interact(entity);

                    if(result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    InteractionResult interact(LivingEntity entity);


}
