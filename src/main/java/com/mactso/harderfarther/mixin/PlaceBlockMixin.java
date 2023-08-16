package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.api.PlaceBlockCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(BlockItem.class)
public class PlaceBlockMixin {

    @Inject(method = "canPlace", at = @At("HEAD"), cancellable = true)
    private void onPlace(BlockPlaceContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        InteractionResult result = PlaceBlockCallback.EVENT.invoker().interact(context, state);
        if(result == InteractionResult.FAIL) {
            cir.setReturnValue(false);
        }
    }


    /**
     * @param context the context of the placement
     * @param state the state to place
     * @return return false if the player cannot place a block there
     */
}

