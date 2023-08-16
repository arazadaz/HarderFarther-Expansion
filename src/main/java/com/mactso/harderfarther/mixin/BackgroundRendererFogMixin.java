package com.mactso.harderfarther.mixin;


import com.mactso.harderfarther.api.FogRenderCallback;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class BackgroundRendererFogMixin {

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void onFog(Camera camera, FogRenderer.FogMode fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info) {
        InteractionResult result = FogRenderCallback.EVENT.invoker().interact(camera, fogType, viewDistance, thickFog, tickDelta);

        if(result == InteractionResult.FAIL) {

        }
    }

}
