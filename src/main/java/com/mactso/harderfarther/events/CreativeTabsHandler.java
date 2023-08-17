package com.mactso.harderfarther.events;

import com.mactso.harderfarther.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class CreativeTabsHandler {

    public static void onTabRegister() {

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {

            content.accept(ModItems.BURNISHING_STONE);
            content.accept(ModItems.LIFE_HEART);

        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(content -> {

            content.accept(ModItems.DEAD_BRANCHES);

        });
    }

}
