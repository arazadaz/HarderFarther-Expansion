package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.api.PlayerTickCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerTickMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onDrop(final CallbackInfo info) {
        ActionResult result = PlayerTickCallback.EVENT.invoker().interact( (PlayerEntity) (Object) this);

        if(result == ActionResult.FAIL) {
            info.cancel();
        }
    }

}
