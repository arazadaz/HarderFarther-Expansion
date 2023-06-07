package com.mactso.harderfarther;

import com.mactso.harderfarther.RegisterHandlers.InitClientRH;
import net.fabricmc.api.ClientModInitializer;

public class MainClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        InitClientRH.registerAll();
    }

}
