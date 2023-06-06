package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.api.LivingEntityTickCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityTickMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo info) {
        ActionResult result = LivingEntityTickCallback.EVENT.invoker().interact((LivingEntity) (Object) this);

        if(result == ActionResult.FAIL) {
            info.cancel();
        }
    }

}
