package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.events.FogColorCallback;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemFogColorMixin {

    @Inject(method = "setShaderFogColor", at = @At("HEAD"), remap = false)
    private static void onFog(float f, float g, float h, float i, CallbackInfo info) {
        float[] newColor = FogColorCallback.EVENT.invoker().interact(f, g, h, i);

        f = newColor[0];
        g = newColor[1];
        h = newColor[2];
        i = newColor[3];
    }

}
