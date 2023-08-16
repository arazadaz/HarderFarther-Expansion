package com.mactso.harderfarther.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface LivingEntityDropCallback {

    Event<LivingEntityDropCallback> EVENT = EventFactory.createArrayBacked(LivingEntityDropCallback.class,
            (listeners) -> (damageSource, entity) -> {
                for (LivingEntityDropCallback listener : listeners) {
                    InteractionResult result = listener.interact(damageSource, entity);

                    if(result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    InteractionResult interact(DamageSource damageSource, LivingEntity entity);

}
