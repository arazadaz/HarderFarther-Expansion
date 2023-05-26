package com.mactso.harderfarther.events;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.utility.Utility;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ServerStoppingEventHandler {

    public static void register(){
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            GrimCitadelManager.clear();
            Utility.debugMsg(0, Main.MODID + "Cleanup Successful");
        });
    }

}
