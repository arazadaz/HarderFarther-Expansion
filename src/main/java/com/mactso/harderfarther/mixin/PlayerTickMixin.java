package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.api.PlayerTickCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerTickMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onDrop(final CallbackInfo info) {
        InteractionResult result = PlayerTickCallback.EVENT.invoker().interact( (Player) (Object) this);

        if(result == InteractionResult.FAIL) {
            info.cancel();
        }
    }

}
