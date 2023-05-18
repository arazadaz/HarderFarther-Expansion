package com.mactso.harderfarther.timer;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CapabilityChunkLastMobDeathTime {

	public static final Capability<IChunkLastMobDeathTime> LASTMOBDEATHTIME = CapabilityManager
			.get(new CapabilityToken<>() {
			});;

	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event) {
		event.register(IChunkLastMobDeathTime.class);
	}

}
