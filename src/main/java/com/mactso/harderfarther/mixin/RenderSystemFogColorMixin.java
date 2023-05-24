package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.events.FogColorCallback;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemFogColorMixin {
    @Shadow
    @Final
    private static final float[] shaderFogColor = new float[]{1.0F, 1.0F, 1.0F, 1.0F};

    @Inject(method = "_setShaderFogColor", at = @At("TAIL"))
    private static void onFog(float f, float g, float h, float i, CallbackInfo info) {
        float[] newColor = FogColorCallback.EVENT.invoker().interact(f, g, h, i);

        shaderFogColor[0] = newColor[0];
        shaderFogColor[1] = newColor[1];
        shaderFogColor[2] = newColor[2];
        shaderFogColor[3] = newColor[3];
    }

}
