package com.mactso.harderfarther.events;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.timer.LastMobDeathTimeProvider;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChunkEvent {

    @SubscribeEvent
    public void onChunk(AttachCapabilitiesEvent<WorldChunk> event)
    {
    	
    	event.addCapability(new Identifier(Main.MODID, "lastmobdeath_capability"), new LastMobDeathTimeProvider());
    }
    
    
}

