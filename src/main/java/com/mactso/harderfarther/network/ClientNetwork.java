package com.mactso.harderfarther.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetwork {

    public static void registerClient(){

        ClientPlayNetworking.registerGlobalReceiver(GrimClientSongPacket.GAME_PACKET_SET_GRIM_CLIENT_SONG_S2C, (GrimClientSongPacket::processPacket));

    }

}
