package com.mactso.harderfarther.RegisterHandlers;

import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.blockentities.ModBlockEntities;
import com.mactso.harderfarther.item.ModItems;

public class InitRH {


    public static void registerAll(){

        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();

    }

}
