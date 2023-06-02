// 16.2+ harder farther
package com.mactso.harderfarther;

import java.lang.reflect.Field;

import com.mactso.harderfarther.config.BiomeConfig;
import com.mactso.harderfarther.config.PrimaryConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.RegisterHandlers.InitRH;



import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;


public class Main implements ModInitializer {

	public static final String MODID = "harderfarther";
	public static final Logger LOGGER = LogManager.getLogger();
	private static final int MAX_USABLE_VALUE = 16000000;  // you can subtract 1 from this number.




	public void onInitialize(ModContainer modContainer) {
		PrimaryConfig.initConfig();
		BiomeConfig.initConfig();

		if (PrimaryConfig.getDebugLevel()> 0) {
			System.out.println("Harder Farther Debug Level: " + PrimaryConfig.getDebugLevel() );
		}

		InitRH.registerAll();
	}


		/*private void fixAttributeMax() {
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
				
		}*/
		

}
