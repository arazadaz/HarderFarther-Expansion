package com.mactso.harderfarther.events;

import com.mactso.harderfarther.command.HarderFartherCommands;

public class ModEvents {

    public static void register(){
        HarderFartherCommands.register();

        registerServerEvents();
        PlayerLoginEventHandler.register();

        registerBlockEvents();
        registerLivingMovementEvents();
        registerMonsterDropEvents();
        registerPlayerInteractionEvents();
        registerPlayerTeleportEvents();
        registerPlayerTickEvents();
        registerWorldTickEvents();

    }

    public static void registerClient(){
        registerFogEvents();
    }

    private static void registerServerEvents(){
        ServerStartingEventHandler.register();
        ServerStoppingEventHandler.register();
    }

    private static void registerFogEvents(){
        FogColorsEventHandler.onFogColorRegister();
        FogColorsEventHandler.onFogRenderRegister();
    }

    private static void registerBlockEvents(){
        BlockEvents.onBlockPlacementRegister();
        BlockEvents.onBreakBlockAfterRegister();
        BlockEvents.onBreakBlockBeforeRegister();
    }

    private static void registerLivingMovementEvents(){
        LivingEventMovementHandler.onEntityTickRegister();
    }

    private static void registerMonsterDropEvents(){
        MonsterDropEventHandler.onMonsterDropEventRegister();
    }

    private static void registerPlayerInteractionEvents(){
        //PlayerInteractionEventHandler.
    }

    private static void registerPlayerTeleportEvents(){
        //PlayerTeleportHandler.
    }

    private static void registerPlayerTickEvents(){
        PlayerTickEventHandler.onPlayerTickRegister();
    }

    private static void registerWorldTickEvents(){
        WorldTickHandler.onWorldTickRegister();
    }

}
