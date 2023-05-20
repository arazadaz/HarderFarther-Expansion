package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.events.LivingEntityDropCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class LivingEntityDropMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;dropLoot(Lnet/minecraft/entity/damage/DamageSource;Z)V"), method = "drop", cancellable = true)
    private void onDrop(final DamageSource source, final CallbackInfoReturnable<Boolean> info) {
        ActionResult result = LivingEntityDropCallback.EVENT.invoker().interact(source, (LivingEntity) (Object) this);

        if(result == ActionResult.FAIL) {
            info.cancel();
        }
    }

}
