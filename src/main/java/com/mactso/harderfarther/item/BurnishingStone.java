package com.mactso.harderfarther.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BurnishingStone extends Item {

	public BurnishingStone(Settings prop) {
		super(prop);
	}

	@Override
	public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand iHand) {
		if (!level.isClient()) {
			ItemStack bstack;
			ItemStack stack;

			if (iHand == Hand.MAIN_HAND) {
				bstack = player.getMainHandStack();
				stack = player.getOffHandStack();
			} else {
				bstack = player.getOffHandStack();
				stack = player.getMainHandStack();
			}
			if ((stack.isDamageable()) && (stack.isDamaged())) {
				int repairAmount = level.getRandom().nextInt(stack.getMaxDamage() / 20) + stack.getMaxDamage() / 20;
				int newDamageValue = Math.max(0, stack.getDamage() - repairAmount);
				stack.setDamage(newDamageValue);
				bstack.setCount(bstack.getCount() - 1);
				level.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_GRINDSTONE_USE,
						SoundCategory.PLAYERS, 1.0F, 1.0F);
				player.playSound(SoundEvents.BLOCK_GRINDSTONE_USE, 1.0F, 1.0F);
				player.getItemCooldownManager().set(this, 60);
			}
		}
		return super.use(level, player, iHand);
	}

	@Override
	public boolean isEnchantable(ItemStack p_41456_) {
		return false;
	}

}
