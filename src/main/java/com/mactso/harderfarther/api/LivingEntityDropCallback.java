package com.mactso.harderfarther.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;

public interface LivingEntityDropCallback {

    Event<LivingEntityDropCallback> EVENT = EventFactory.createArrayBacked(LivingEntityDropCallback.class,
            (listeners) -> (damageSource, entity) -> {
                for (LivingEntityDropCallback listener : listeners) {
                    ActionResult result = listener.interact(damageSource, entity);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult interact(DamageSource damageSource, LivingEntity entity);

}
