package com.mactso.harderfarther.mixin;


import com.mactso.harderfarther.events.FogRenderCallback;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererFogMixin {

    @Inject(method = "applyFog", at = @At("HEAD"))
    private static void onFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info) {
        ActionResult result = FogRenderCallback.EVENT.invoker().interact(camera, fogType, viewDistance, thickFog, tickDelta);

        if(result == ActionResult.FAIL) {

        }
    }

}
