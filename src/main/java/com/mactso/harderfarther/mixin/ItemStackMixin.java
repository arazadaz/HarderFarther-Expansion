package com.mactso.harderfarther.mixin;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.random.RandomGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {


    @Shadow
    public abstract Item getItem();


    @Shadow
    public abstract int getDamage();

    @Shadow
    public abstract void setDamage(int damage);

    @Shadow
    public abstract int getMaxDamage();

    //Sets a max value of 6 to durability damage on armor so that armor doesn't get absolutely destroyed by higher damaging entities. Zombies in normal minecraft on normal difficulty do 3 damage for reference. Wither skeletons do 8 for reference.
    @Inject(at = @At(value = "RETURN"), method = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/util/random/RandomGenerator;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", cancellable = true)
    private void harderfarther$SetItemMaxDamage(int amount, RandomGenerator random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir){

        if(this.getItem() instanceof ArmorItem) {
            if(amount >= 6) {
                int i = this.getDamage() - amount + 6;
                this.setDamage(i);
                cir.setReturnValue(i >= this.getMaxDamage());
                //For reference, an iron chest-plate has 240 durability, so 6 damage would allow 40 hits before it breaks.
            }
        }

    }

}
