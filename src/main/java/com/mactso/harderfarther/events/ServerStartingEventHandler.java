package com.mactso.harderfarther.events;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.config.Platform;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.StructureFeature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class ServerStartingEventHandler {

    private static ArrayList<String> structureList = new ArrayList<>();
    private static ArrayList<String> entityTypeList = new ArrayList<>();
    private static ArrayList<String> biomeList = new ArrayList<>();
    private static ArrayList<String> oreList = new ArrayList<>();

    public static void register(){
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {

            //initialize grim citadels
            GrimCitadelManager.load(server);







            //Dumping registries for convenience
            DynamicRegistryManager registryAccess = server.getRegistryManager();

            Registry<StructureFeature> structures = registryAccess.get(Registry.STRUCTURE_WORLDGEN);
            Registry<EntityType> entitieTypes = registryAccess.get(Registry.ENTITY_TYPE_KEY);
            Registry<Biome> biomes = registryAccess.get(BuiltinRegistries.BIOME.getKey());
            Registry<PlacedFeature> ores = registryAccess.get(Registry.PLACED_FEATURE_KEY);

            structures.getKeys().forEach(structureFeatureKey -> {
                structureList.add(structureFeatureKey.getValue().toString());
            });

            entitieTypes.getKeys().forEach(entityTypeKey -> {
                entityTypeList.add(entityTypeKey.getValue().toString());
            });

            biomes.getKeys().forEach(biomeKey -> {
                biomeList.add(biomeKey.getValue().toString());
            });

            ores.getKeys().forEach(placedFeatureKey -> {
                if(ores.get(placedFeatureKey).feature().value().getFeature() instanceof OreFeature){
                    Block block = ((OreFeatureConfig)ores.get(placedFeatureKey).feature().value().getConfig()).targets.get(0).state.getBlock();
                    String blockId = Registry.BLOCK.getKey(block).get().getValue().toString();
                    if(!oreList.contains(blockId)) {
                        oreList.add(blockId);
                    }
                }
            });

            saveConfig();

        });
    }




    private static void saveConfig() {
        final File configFile = getConfigFile();
        final Properties properties = new Properties();

        properties.put("ores", oreList.toString());
        properties.put("structures", structureList.toString());
        properties.put("biomes", biomeList.toString());
        properties.put("entity_types", entityTypeList.toString());

        try (FileOutputStream stream = new FileOutputStream(configFile)) {
            properties.store(stream, "A fairly useful list of ores/structures/biomes/entities");
        } catch (final IOException e) {
            Main.LOGGER.warn("[HarderFarther] Could not store property file '" + configFile.getAbsolutePath() + "'", e);
        }
    }


    private static File getConfigFile() {
        final File configDir = Platform.configDirectory().toFile();

        if (!configDir.exists()) {
            Main.LOGGER.warn("[Harder Farther] Could not access configuration directory: " + configDir.getAbsolutePath());
        }

        return new File(configDir, "Registry_Dump.properties");
    }

}
