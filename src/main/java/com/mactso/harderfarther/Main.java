// 16.2+ harder farther
package com.mactso.harderfarther;

import java.lang.reflect.Field;

import com.mactso.harderfarther.config.PrimaryConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.command.HarderFartherCommands;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.RegisterHandlers.InitRH;
import com.mactso.harderfarther.utility.Utility;

import net.fabricmc.api.ModInitializer;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;


public class Main implements ModInitializer {

	public static final String MODID = "harderfarther";
	public static final Logger LOGGER = LogManager.getLogger();
	private static final int MAX_USABLE_VALUE = 16000000;  // you can subtract 1 from this number.




	public void onInitialize() {
		PrimaryConfig.initConfig();

		if (PrimaryConfig.getDebugLevel()> 0) {
			System.out.println("Harder Farther Debug Level: " + PrimaryConfig.getDebugLevel() );
		}

		InitRH.registerAll();
	}


		
		
		
		private void fixAttributeMax() {
			// don't care about speed and knockback.
			// speed becomes too fast very quickly.
			// knockback maxes at 100% resistance to knockback.
			
				try {
					String name = ASMAPI.mapField("f_22308_");
					Field max = ClampedEntityAttribute.class.getDeclaredField(name);
					max.setAccessible(true);

					max.set(EntityAttributes.GENERIC_MAX_HEALTH, (double) MAX_USABLE_VALUE);
					max.set(EntityAttributes.GENERIC_ATTACK_DAMAGE, (double) MAX_USABLE_VALUE);

				} catch (Exception e) {
					LOGGER.error("XXX Unexpected Reflection Failure changing attribute maximum");
				}
				
		}



		
		@Mod.EventBusSubscriber(bus = Bus.FORGE)
		public static class ForgeEvents
		{
			@SubscribeEvent
			public static void onServerAbout(ServerAboutToStartEvent event)
			{
				GrimCitadelManager.load(event.getServer());
			}

			@SubscribeEvent
			public static void onServerStopping(ServerStoppingEvent event)
			{
				GrimCitadelManager.clear();
				Utility.debugMsg(0, MODID + "Cleanup Successful");
			}

			@SubscribeEvent 		
			public static void onCommandsRegistry(final RegisterCommandsEvent event) {
				Utility.debugMsg(0,"Harder Farther: Registering Command Dispatcher");
				HarderFartherCommands.register(event.getDispatcher());			
			}

		}
		
		
	
		

}
