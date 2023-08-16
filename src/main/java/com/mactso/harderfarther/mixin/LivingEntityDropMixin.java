package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.api.LivingEntityDropCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityDropMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropFromLootTable(Lnet/minecraft/world/damagesource/DamageSource;Z)V"), method = "dropAllDeathLoot", cancellable = true)
    private void harderfarther$onEntityDrop(final DamageSource source, final CallbackInfo info) {
        InteractionResult result = LivingEntityDropCallback.EVENT.invoker().interact(source, (LivingEntity) (Object) this);

        if(result == InteractionResult.FAIL) {
            info.cancel();
        }
    }

}
