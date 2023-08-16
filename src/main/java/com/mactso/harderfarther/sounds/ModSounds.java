package com.mactso.harderfarther.sounds;

import com.mactso.harderfarther.Main;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
//	Attribution Tags for Ambient Music
//
//	Lake of Destiny by Darren Curtis | https://www.darrencurtismusic.com/
//	Music promoted by https://www.chosic.com/free-music/all/
//	Creative Commons Attribution 3.0 Unported License
//	https://creativecommons.org/licenses/by/3.0/
//
//	Dusty Memories by Darren Curtis | https://www.darrencurtismusic.com/
//	Music promoted by https://www.chosic.com/free-music/all/
//	Creative Commons Attribution 3.0 Unported License
//	https://creativecommons.org/licenses/by/3.0/
//	 
//	Labyrinth of Lost Dreams by Darren Curtis | https://www.darrencurtismusic.com/
//	Music promoted on https://www.chosic.com/free-music/all/
//	Creative Commons Attribution 3.0 Unported (CC BY 3.0)
//	https://creativecommons.org/licenses/by/3.0/
	
	

	public static final SoundEvent DUSTY_MEMORIES = create ("dust");
	public static final SoundEvent LABYRINTH_LOST_DREAMS = create ("laby");
	public static final SoundEvent LAKE_DESTINY = create ("lake");

	public static int NUM_DUSTY_MEMORIES = 1;
	public static int NUM_LABYRINTH_LOST_DREAMS  = 2;
	public static int NUM_LAKE_DESTINY  = 3;
	public static int NUM_SONGS = 3;
	
//    private static RegistryObject<SoundEvent> registerSoundEvent(final String soundName) {
//        return DEFERRED_REG.register(soundName, () -> new SoundEvent(new ResourceLocation(Main.MODID, soundName)));
//    }

	public static void register()
	{
		Registry.register(Registry.SOUND_EVENT, new ResourceLocation(Main.MODID, "dust"), DUSTY_MEMORIES);
		Registry.register(Registry.SOUND_EVENT, new ResourceLocation(Main.MODID, "lady"), LABYRINTH_LOST_DREAMS);
		Registry.register(Registry.SOUND_EVENT, new ResourceLocation(Main.MODID, "lake"), LAKE_DESTINY);
	}

	private static SoundEvent create(String key)
	{
		ResourceLocation res = new ResourceLocation(Main.MODID, key);
		SoundEvent ret = new SoundEvent(res);
		return ret;
	}
	
}
