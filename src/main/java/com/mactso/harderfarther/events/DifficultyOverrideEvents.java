package com.mactso.harderfarther.events;

import com.mactso.harderfarther.api.DifficultyOverrideCallback;
import com.mactso.harderfarther.config.DimensionDifficultyOverridesConfig;
import net.minecraft.world.World;

public class DifficultyOverrideEvents {

    public static void onDifficultyOverrideEventRegister() {
        DifficultyOverrideCallback.EVENT.register(
                (currentDifficulty, world, outposts, minBoostDistance, maxBoostDistance) -> {

                    if(DimensionDifficultyOverridesConfig.isTheOverworldOverridden()) {
                        if (world.getRegistryKey() == World.OVERWORLD) {
                            currentDifficulty[0] = DimensionDifficultyOverridesConfig.getOverworldDifficulty()/100;
                        }
                    }

                    if(DimensionDifficultyOverridesConfig.isTheNetherOverridden()) {
                        if (world.getRegistryKey() == World.NETHER) {
                            currentDifficulty[0] = DimensionDifficultyOverridesConfig.getNetherDifficulty()/100;
                        }
                    }


                    if(DimensionDifficultyOverridesConfig.isTheEndOverridden()) {
                        if (world.getRegistryKey() == World.END) {
                            currentDifficulty[0] = DimensionDifficultyOverridesConfig.getEndDifficulty()/100;
                        }
                    }

                });
    }

}
