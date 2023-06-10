package com.mactso.harderfarther.events;

import com.mactso.harderfarther.api.DifficultyOverrideCallback;
import com.mactso.harderfarther.config.DimensionDifficultyOverrides;
import net.minecraft.world.World;

public class DifficultyOverrideEvents {

    public static void onDifficultyOverrideEventRegister() {
        DifficultyOverrideCallback.EVENT.register(
                (currentDifficulty, world, outposts, minBoostDistance, maxBoostDistance) -> {

                    if(DimensionDifficultyOverrides.isTheOverworldOverridden()) {
                        if (world.getRegistryKey() == World.OVERWORLD) {
                            currentDifficulty[0] = DimensionDifficultyOverrides.getOverworldDifficulty()/100;
                        }
                    }

                    if(DimensionDifficultyOverrides.isTheNetherOverridden()) {
                        if (world.getRegistryKey() == World.NETHER) {
                            currentDifficulty[0] = DimensionDifficultyOverrides.getNetherDifficulty()/100;
                        }
                    }


                    if(DimensionDifficultyOverrides.isTheEndOverridden()) {
                        if (world.getRegistryKey() == World.END) {
                            currentDifficulty[0] = DimensionDifficultyOverrides.getEndDifficulty()/100;
                        }
                    }

                });
    }

}
