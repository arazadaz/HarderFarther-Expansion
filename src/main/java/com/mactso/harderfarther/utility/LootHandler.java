package com.mactso.harderfarther.utility;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mactso.harderfarther.manager.ChestLootManager;
import com.mactso.harderfarther.manager.LootTableListManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.math.Vec3d;

public class LootHandler
{
        //Need to find working alternative to get loot table origin on fabric since I don't know how to get loot context with this event
        /*public static void register(){
            LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
                // Let's only modify built-in loot tables and leave data pack loot tables untouched by checking the source.
                // We also check that the loot table ID is equal to the ID we want.
                if (source.isBuiltin() && !LootTableListManager.isBonusLootTable(id)) {
//        		    System.out.println("false:" + context.getQueriedLootTableId());
                    //do nothing

                }else if (source.isBuiltin()) {
                    Vec3d origin = .get(LootContextParameters.ORIGIN); // originally context.get(LootContextParameters.ORIGIN);
                    LootPool.builder().with(ItemEntry.builder(Items.EGG)).conditionally()
                    if (origin == null) { // can't calculate difficulty without coordinates
//        		System.out.println("No Origin for loot:" + context.getQueriedLootTableId());
                        return generatedLoot;
                    }


                    ItemStack stack = ChestLootManager.doGetLootStack(context.getWorld(), origin);
//    		System.out.println("Adding Bonus Loot:" + context.getQueriedLootTableId() + ": " +stack.getItem().toString());
                    generatedLoot.add(stack);
                    return generatedLoot;
                }
            });
        }*/
  
       
}

//Possible solution? Requires a fair amount of rework & I don't know if it would actually work the way I want it.
class ChestLocationLootCondition implements LootCondition {

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.VALUE_CHECK;
    }

    @Override
    public boolean test(LootContext lootContext) {
        Vec3d origin = lootContext.get(LootContextParameters.ORIGIN);
        return true;
        //return blockState != null && blockState.isOf(this.block) && this.properties.test(blockState); //not at all related, just keeping as an example.
    }
}