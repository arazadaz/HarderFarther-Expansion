package com.mactso.harderfarther.events;

import com.mactso.harderfarther.api.DifficultyOverrideCallback;
import com.mactso.harderfarther.config.DimensionDifficultyOverridesConfig;
import net.minecraft.world.level.Level;

public class DifficultyOverrideEvents {

    public static void onDifficultyOverrideEventRegister() {
        DifficultyOverrideCallback.EVENT.register(
                (currentDifficulty, world, outposts, minBoostDistance, maxBoostDistance) -> {

                    if(DimensionDifficultyOverridesConfig.isTheOverworldOverridden()) {
                        if (world.dimension() == Level.OVERWORLD) {
                            currentDifficulty[0] = DimensionDifficultyOverridesConfig.getOverworldDifficulty()/100;
                        }
                    }

                    if(DimensionDifficultyOverridesConfig.isTheNetherOverridden()) {
                        if (world.dimension() == Level.NETHER) {
                            currentDifficulty[0] = DimensionDifficultyOverridesConfig.getNetherDifficulty()/100;
                        }
                    }


                    if(DimensionDifficultyOverridesConfig.isTheEndOverridden()) {
                        if (world.dimension() == Level.END) {
                            currentDifficulty[0] = DimensionDifficultyOverridesConfig.getEndDifficulty()/100;
                        }
                    }

                });
    }

}
