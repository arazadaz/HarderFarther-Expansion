package com.mactso.harderfarther.utility;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mactso.harderfarther.manager.ChestLootManager;
import com.mactso.harderfarther.manager.LootTableListManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.phys.Vec3;

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
class ChestLocationLootCondition implements LootItemCondition {

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.VALUE_CHECK;
    }

    @Override
    public boolean test(LootContext lootContext) {
        Vec3 origin = lootContext.getParamOrNull(LootContextParams.ORIGIN);
        return true;
        //return blockState != null && blockState.isOf(this.block) && this.properties.test(blockState); //not at all related, just keeping as an example.
    }
}