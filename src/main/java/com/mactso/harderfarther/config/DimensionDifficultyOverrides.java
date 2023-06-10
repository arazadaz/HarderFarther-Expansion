package com.mactso.harderfarther.config;

import com.ibm.icu.impl.Pair;
import com.mactso.harderfarther.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class DimensionDifficultyOverrides {

    private static int size;

    private static ArrayList<String> dimensionOverridesAsString = new ArrayList<>();
    private static ArrayList<Pair<Boolean, Float>> dimensionOverrides = new ArrayList<>();

    private static boolean isTheOverworldOverridden;
    private static boolean isTheNetherOverridden;

    private static boolean isTheEndOverridden;

    private static float overworldDifficulty;
    private static float netherDifficulty;

    private static float endDifficulty;

    public static void initConfig() {
        final File configFile = getConfigFile();
        final Properties properties = new Properties();

        if (configFile.exists()) {
            try (FileInputStream stream = new FileInputStream(configFile)) {
                properties.load(stream);
            } catch (final IOException e) {
                Main.LOGGER.warn("[HarderFarther] Could not read property file '" + configFile.getAbsolutePath() + "'", e);
            }
        }

        dimensionOverridesAsString.add(properties.computeIfAbsent("the_overworld", (a) -> "false:20").toString());
        dimensionOverridesAsString.add(properties.computeIfAbsent("the_nether", (a) -> "false:60").toString());
        dimensionOverridesAsString.add(properties.computeIfAbsent("the_end", (a) -> "false:100").toString());


        computeConfigValues();
        saveConfig();

    }

    private static File getConfigFile() {
        final File configDir = Platform.configDirectory().toFile();

        if (!configDir.exists()) {
            Main.LOGGER.warn("[Harder Farther] Could not access configuration directory: " + configDir.getAbsolutePath());
        }

        return new File(configDir, "Dimension_Overrides.properties");
    }

    private static void computeConfigValues() {

        for(int x = 0; x< dimensionOverridesAsString.size(); x++) {
            boolean isDimensionOverriden = Boolean.parseBoolean(dimensionOverridesAsString.get(x).substring(1).split(":",2)[0]);
            float difficulty = Float.parseFloat(dimensionOverridesAsString.get(x).split(":", 2)[1]);
            dimensionOverrides.add(Pair.of(isDimensionOverriden, difficulty));
        }

        isTheOverworldOverridden = dimensionOverrides.get(0).first.booleanValue();
        isTheNetherOverridden = dimensionOverrides.get(1).first.booleanValue();
        isTheEndOverridden = dimensionOverrides.get(2).first.booleanValue();

        overworldDifficulty = dimensionOverrides.get(0).second;
        netherDifficulty = dimensionOverrides.get(1).second;
        endDifficulty = dimensionOverrides.get(2).second;


    }

    public static void saveConfig() {
        final File configFile = getConfigFile();
        final Properties properties = new Properties();

        properties.put("the_overworld", dimensionOverridesAsString.get(0));
        properties.put("the_nether", dimensionOverridesAsString.get(1));
        properties.put("the_end", dimensionOverridesAsString.get(2));



        try (FileOutputStream stream = new FileOutputStream(configFile)) {
            properties.store(stream, "Override the difficulty calculation for a dimension with a constant.");
        } catch (final IOException e) {
            Main.LOGGER.warn("[HarderFarther] Could not store property file '" + configFile.getAbsolutePath() + "'", e);
        }
    }



    public static int getSize(){
        return size;
    }

    public static boolean isTheOverworldOverridden(){
        return isTheOverworldOverridden;
    }
    public static boolean isTheNetherOverridden(){
        return isTheNetherOverridden;
    }

    public static boolean isTheEndOverridden(){
        return isTheEndOverridden;
    }

    public static float getOverworldDifficulty(){
        return overworldDifficulty;
    }
    public static float getNetherDifficulty(){
        return netherDifficulty;
    }

    public static float getEndDifficulty(){
        return endDifficulty;
    }

}
