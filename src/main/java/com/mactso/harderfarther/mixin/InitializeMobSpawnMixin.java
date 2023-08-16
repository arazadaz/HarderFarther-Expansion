package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.mixinInterfaces.IExtendedMobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class InitializeMobSpawnMixin implements IExtendedMobEntity {

    private MobSpawnType spawnReason;

    @Inject(at = @At(value = "HEAD"), method = "finalizeSpawn")
    public void harderfarther$RememberSpawnReason(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, SpawnGroupData entityData, CompoundTag entityNbt, CallbackInfoReturnable<SpawnGroupData> cir){

        this.spawnReason = spawnReason;

    }

    @Override
    public MobSpawnType getSpawnReason() {
        return this.spawnReason;
    }

}
