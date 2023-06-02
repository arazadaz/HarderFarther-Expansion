package com.mactso.harderfarther;

import com.mactso.harderfarther.RegisterHandlers.InitClientRH;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class MainClient implements ClientModInitializer {


    @Override
    public void onInitializeClient(ModContainer modContainer) {
        InitClientRH.registerAll();
    }

}
