package com.mactso.harderfarther.utility;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mactso.harderfarther.manager.ChestLootManager;
import com.mactso.harderfarther.manager.LootTableListManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

public class LootHandler
{
 
    public static class HFLootModifier extends LootModifier
    {

        public static final Supplier<Codec<HFLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, HFLootModifier::new)));


        public HFLootModifier(final LootCondition[] conditionsIn) {
            super(conditionsIn);

        }

        @Override
        protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {

        	if (!LootTableListManager.isBonusLootTable(context.getQueriedLootTableId())) {
//        		System.out.println("false:" + context.getQueriedLootTableId());
        		return generatedLoot;
        	}

        	Vec3d origin = context.get(LootContextParameters.ORIGIN);
        	if (origin == null) { // can't calculate difficulty without coordinates
//        		System.out.println("No Origin for loot:" + context.getQueriedLootTableId());
        		return generatedLoot;        	
        	}
        	

        	ItemStack stack = ChestLootManager.doGetLootStack(context.getWorld(), origin);
//    		System.out.println("Adding Bonus Loot:" + context.getQueriedLootTableId() + ": " +stack.getItem().toString());
        	generatedLoot.add(stack);
			return generatedLoot;

        }


        @Override
        public Codec<? extends IGlobalLootModifier> codec() {
            return CODEC.get();
        }

        

    }
  
       
}