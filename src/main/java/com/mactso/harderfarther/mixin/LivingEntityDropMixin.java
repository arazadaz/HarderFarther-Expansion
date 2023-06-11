package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.api.LivingEntityDropCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityDropMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;dropLoot(Lnet/minecraft/entity/damage/DamageSource;Z)V"), method = "drop", cancellable = true)
    private void harderfarther$onEntityDrop(final DamageSource source, final CallbackInfo info) {
        ActionResult result = LivingEntityDropCallback.EVENT.invoker().interact(source, (LivingEntity) (Object) this);

        if(result == ActionResult.FAIL) {
            info.cancel();
        }
    }

}
