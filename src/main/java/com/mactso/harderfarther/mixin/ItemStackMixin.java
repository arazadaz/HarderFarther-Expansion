package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.utility.Utility;
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

    //Sets a max value configured by end-user(default 6) to durability damage on armor so that armor doesn't get absolutely destroyed by higher damaging entities. Zombies in normal minecraft on normal difficulty do 3 damage for reference. Wither skeletons do 8 for reference.
    @Inject(at = @At(value = "RETURN"), method = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/util/random/RandomGenerator;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", cancellable = true)
    private void harderfarther$SetItemMaxDamage(int amount, RandomGenerator random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir){

        if(this.getItem() instanceof ArmorItem) {
            if(PrimaryConfig.getDebugLevel() > 0) {
                Utility.debugMsg(1, "Armor piece " + this.getItem().getTranslationKey() + " took " + amount + " damage(before possible reduction)!");
            }
            if(amount >= PrimaryConfig.getMaximumArmorDamage()) {
                int i = this.getDamage() - amount + PrimaryConfig.getMaximumArmorDamage();
                this.setDamage(i);
                cir.setReturnValue(i >= this.getMaxDamage());
                //For reference, an iron chest-plate has 240 durability, so 6 damage would allow 40 hits before it breaks.
            }
        }

    }

}
