package com.mactso.harderfarther.config;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class Platform {
    public static Path configDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}

