package com.mactso.harderfarther;

import com.mactso.harderfarther.RegisterHandlers.InitClientRH;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        InitClientRH.registerAll();
    }

}
