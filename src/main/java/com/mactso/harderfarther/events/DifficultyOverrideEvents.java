package com.mactso.harderfarther.events;

import com.mactso.harderfarther.api.DifficultyOverrideCallback;
import com.mactso.harderfarther.config.DimensionDifficultyOverrides;
import net.minecraft.world.World;

public class DifficultyOverrideEvents {

    public static void onDifficultyOverrideEventRegister() {
        DifficultyOverrideCallback.EVENT.register(
                (currentDifficulty, world, outposts, minBoostDistance, maxBoostDistance) -> {

                    if(DimensionDifficultyOverrides.isTheNetherOverriden()) {
                        if (world.getRegistryKey() == World.NETHER) {
                            currentDifficulty[0] = DimensionDifficultyOverrides.getNetherDifficulty()/100;
                        }
                    }


                    if(DimensionDifficultyOverrides.isTheEndOverriden()) {
                        if (world.getRegistryKey() == World.END) {
                            currentDifficulty[0] = DimensionDifficultyOverrides.getEndDifficulty()/100;
                        }
                    }

                });
    }

}
