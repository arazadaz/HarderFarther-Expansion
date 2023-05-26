package com.mactso.harderfarther.RegisterHandlers;


import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.network.ClientNetwork;

public class InitClientRH {


    public static void registerAll(){

        //ModBlocks.setRenderLayer();
        ClientNetwork.registerClient();

    }

}
