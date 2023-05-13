package com.mactso.harderfarther.timer;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;


	public class LastMobDeathTimeProvider implements ICapabilityProvider, ICapabilitySerializable<NbtCompound>
	{
		IChunkLastMobDeathTime storage;

		public LastMobDeathTimeProvider() {
			storage = new ChunkLastMobDeathTime();
		}

		
		@SuppressWarnings("unchecked")
		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			if (cap == CapabilityChunkLastMobDeathTime.LASTMOBDEATHTIME)
				return (LazyOptional<T>) LazyOptional.of(() -> storage);
			return LazyOptional.empty();
		}

		@Override
		public NbtCompound serializeNBT() {
			NbtCompound ret = new NbtCompound();
			ret.putLong("lastMobDeathTime", storage.getLastKillTime());
			return ret;
		}

		@Override
		public void deserializeNBT(NbtCompound nbt) {
			long time = nbt.getLong("lastMobDeathTime");
			storage.setLastKillTime(time);
		}
	}

