package com.mactso.harderfarther.utility;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mactso.harderfarther.manager.ChestLootManager;
import com.mactso.harderfarther.manager.LootTableListManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.math.Vec3d;

public class LootHandler
{
        //Need to find working alternative to get loot table origin on fabric
        /*public static void register(){
            LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
                // Let's only modify built-in loot tables and leave data pack loot tables untouched by checking the source.
                // We also check that the loot table ID is equal to the ID we want.
                if (source.isBuiltin() && !LootTableListManager.isBonusLootTable(id)) {
//        		    System.out.println("false:" + context.getQueriedLootTableId());
                    //do nothing

                }else if (source.isBuiltin()) {
                    Vec3d origin = .get(LootContextParameters.ORIGIN); // originally context.get(LootContextParameters.ORIGIN);
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