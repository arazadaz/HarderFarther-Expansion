package com.mactso.harderfarther.client;

import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;

public class PlayGrimSongs {
	boolean blockmusic = false;
	private static MinecraftClient mc = null;
	private static MusicTracker musicTicker = null;
	private static final Random rand = new Random();
	// this is kludgy since I'm hard stopping any currently playing song.
	// there *is* an official way of doing this.
	private static long clientPsuedoTicks = 0;
	private static long grimSongDelayTicks = 0;

	public static void playSong(SoundEvent song) {
		playSong(song, 9600, 18200);
	}

	public static void playSong(SoundEvent song, int minDelay, int maxDelay) {

		doInit();

		clientPsuedoTicks = Util.getMeasuringTimeMs() / 50;
		if (grimSongDelayTicks < clientPsuedoTicks) {
			grimSongDelayTicks = clientPsuedoTicks + (1200); // ignore calls within 60 seconds.  
			// TODO: need forcestart=true/false parm
		}
		musicTicker.stop();
		boolean replaceCurrentMusic = true;
		MusicSound m = new MusicSound(song, minDelay, maxDelay, replaceCurrentMusic);
		musicTicker.play(m);
	}

	private static void doInit() {
		if (mc == null) {
			rand.setSeed(Util.getMeasuringTimeMs()); 
			mc = MinecraftClient.getInstance(); 
		}
		if (musicTicker == null) {
			musicTicker = mc.getMusicTracker();
		}
	}
}
