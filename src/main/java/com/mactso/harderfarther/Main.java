// 16.2+ harder farther
package com.mactso.harderfarther;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.blockentities.ModBlockEntities;
import com.mactso.harderfarther.command.HarderFartherCommands;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.events.BlockEvents;
import com.mactso.harderfarther.events.ChunkEvent;
import com.mactso.harderfarther.events.ExperienceDropEventHandler;
import com.mactso.harderfarther.events.FogColorsEventHandler;
import com.mactso.harderfarther.events.LivingEventMovementHandler;
import com.mactso.harderfarther.events.MonsterDropEventHandler;
import com.mactso.harderfarther.events.PlayerLoginEventHandler;
import com.mactso.harderfarther.events.PlayerTickEventHandler;
import com.mactso.harderfarther.events.PlayerTeleportHandler;
import com.mactso.harderfarther.events.WorldTickHandler;
import com.mactso.harderfarther.item.ModItems;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.network.Register;
import com.mactso.harderfarther.sounds.ModSounds;
import com.mactso.harderfarther.utility.LootHandler.HFLootModifier;
import com.mactso.harderfarther.utility.Utility;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;


public class Main {

	    public static final String MODID = "harderfarther"; 
		private static final Logger LOGGER = LogManager.getLogger();
		private static final int MAX_USABLE_VALUE = 16000000;  // you can subtract 1 from this number.

	    
	    public Main()
	    {

	    	Utility.debugMsg(0,MODID + ": Registering Mod.");
	  		FMLJavaModLoadingContext.get().getModEventBus().register(this);
 	        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,MyConfig.COMMON_SPEC );

	    }

		@OnlyIn(Dist.CLIENT)
	    @SubscribeEvent
	    public void setupClient(final FMLClientSetupEvent event) {
	    	
			MinecraftForge.EVENT_BUS.register(new FogColorsEventHandler());
			ModBlocks.setRenderLayer();
			
	    }
	    
	    @SubscribeEvent
	    public void setupCommon(final FMLCommonSetupEvent event)
	    {
	        Register.initPackets();
	    }

		@SubscribeEvent 
		public void preInit (final FMLCommonSetupEvent event) {
				Utility.debugMsg(0, MODID + ": Registering Handlers");
				MinecraftForge.EVENT_BUS.register(new WorldTickHandler());
				MinecraftForge.EVENT_BUS.register(new MonsterDropEventHandler());
				MinecraftForge.EVENT_BUS.register(new ExperienceDropEventHandler());
				MinecraftForge.EVENT_BUS.register(new ChunkEvent());
				MinecraftForge.EVENT_BUS.register(new PlayerLoginEventHandler());
				MinecraftForge.EVENT_BUS.register(new PlayerTickEventHandler());
				MinecraftForge.EVENT_BUS.register(new PlayerTeleportHandler());
				MinecraftForge.EVENT_BUS.register(new LivingEventMovementHandler());
				MinecraftForge.EVENT_BUS.register(new BlockEvents());
				fixAttributeMax();
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



		@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	    public static class ModEvents
	    {

		    @SubscribeEvent
		    public static void onRegister(final RegisterEvent event)
		    {
		    	@Nullable
				IForgeRegistry<Object> fr = event.getForgeRegistry();
		    	
		    	@NotNull
				RegistryKey<? extends Registry<?>> key = event.getRegistryKey();
		    	if (key.equals(ForgeRegistries.Keys.BLOCKS))
		    		ModBlocks.register(event.getForgeRegistry());
		    	else if (key.equals(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES))
		    		ModBlockEntities.register(event.getForgeRegistry());
		    	else if (key.equals(ForgeRegistries.Keys.ITEMS))
		    		ModItems.register(event.getForgeRegistry());
		    	else if (key.equals(ForgeRegistries.Keys.SOUND_EVENTS))
		    		ModSounds.register(event.getForgeRegistry());
		    	else if (key.equals(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS))
		    		event.getForgeRegistry().register("special", HFLootModifier.CODEC.get());
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
