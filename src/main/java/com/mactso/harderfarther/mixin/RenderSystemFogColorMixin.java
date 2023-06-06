package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.api.FogColorCallback;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public abstract class RenderSystemFogColorMixin {

    @Shadow(remap = false)
    static void _setShaderFogColor(float f, float g, float h, float i){};

    @Inject(method = "setShaderFogColor", at = @At("TAIL"), remap = false)
    private static void harderfarther$computeFogEvent(float f, float g, float h, float i, CallbackInfo info) {
        float[] newColor = new float[]{f, g, h, i};
        FogColorCallback.EVENT.invoker().interact(newColor);

        f = newColor[0];
        g = newColor[1];
        h = newColor[2];
        i = newColor[3];
        _setShaderFogColor(f, g, h, i);
    }

}
