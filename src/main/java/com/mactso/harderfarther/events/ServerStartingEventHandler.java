package com.mactso.harderfarther.events;

import com.mactso.harderfarther.manager.GrimCitadelManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ServerStartingEventHandler {

    public static void register(){
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            GrimCitadelManager.load(server);
        });
    }

}
