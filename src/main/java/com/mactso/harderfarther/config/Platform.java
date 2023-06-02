package com.mactso.harderfarther.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Platform {
    public static Path configDirectory() {

        String location = FabricLoader.getInstance().getConfigDir().toString() + "/HarderFarther";

        File theDir = new File(location);
        if (!theDir.exists()){
            theDir.mkdirs();
        }


        return Paths.get(location);
    }
}

