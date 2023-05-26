package com.mactso.harderfarther.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetwork {

    public static void registerClient(){

        ClientPlayNetworking.registerGlobalReceiver(GrimClientSongPacket.GAME_PACKET_SET_GRIM_CLIENT_SONG_S2C, (GrimClientSongPacket::processPacket));
        ClientPlayNetworking.registerGlobalReceiver(SyncFogToClientsPacket.GAME_PACKET_SYNC_FOG_COLOR_S2C, SyncFogToClientsPacket::processPacket);
        ClientPlayNetworking.registerGlobalReceiver(SyncAllGCWithClientPacket.GAME_PACKET_SYNC_GRIM_CITADEL_S2C, SyncAllGCWithClientPacket::processPacket);
        ClientPlayNetworking.registerGlobalReceiver(SyncDifficultyToClientsPacket.GAME_PACKET_SYNC_DIFFICULTY_S2C, SyncDifficultyToClientsPacket::processPacket);

    }

}
