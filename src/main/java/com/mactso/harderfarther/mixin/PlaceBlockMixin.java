package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.events.PlaceBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(BlockItem.class)
public class PlaceBlockMixin {

    @Inject(method = "Lnet/minecraft/item/BlockItem;place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private void onPlace(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        ActionResult result = PlaceBlockCallback.EVENT.invoker().interact(context, state);
        if(result == ActionResult.FAIL) {
            cir.setReturnValue(false);
        }
    }


    /**
     * @param context the context of the placement
     * @param state the state to place
     * @return return false if the player cannot place a block there
     */
}

