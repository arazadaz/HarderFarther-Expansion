package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.api.ServerWorldTickCallback;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldTickMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onDrop(final CallbackInfo info) {
        ActionResult result = ServerWorldTickCallback.EVENT.invoker().interact( (ServerWorld) (Object) this);

        if(result == ActionResult.FAIL) {
            info.cancel();
        }
    }

}
