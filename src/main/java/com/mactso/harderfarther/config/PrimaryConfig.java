package com.mactso.harderfarther.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

//16.2 - 1.0.0.0 HarderFarther

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.HarderFarther;
import com.mactso.harderfarther.manager.LootManager;



public class PrimaryConfig {

	private static String dimensionOmitList;
	private static String lootItemsList;
	private static String grimCitadelsList;


	public static void initConfig() {
		final File configFile = getConfigFile();
		final Properties properties = new Properties();

		if (configFile.exists()) {
			try (FileInputStream stream = new FileInputStream(configFile)) {
				properties.load(stream);
			} catch (final IOException e) {
				HarderFarther.LOG.warn("[Harder Farther] Could not read property file '" + configFile.getAbsolutePath() + "'", e);
			}
		}


//		Harder Farther Control Values"."Debug Settings
		try {
			HarderFarther.debugLevel = Integer.parseInt(properties.computeIfAbsent("debug_level", (a) -> "0").toString());
		} catch (final Exception e) {
			HarderFarther.debugLevel = 0;
			HarderFarther.LOG.warn("[HarderFarther] Invalid configuration value for 'debug_level'. Using default value.");
		}


//		Harder Farther Control Values"."Farm Limiter Settings
		HarderFarther.limitMobFarmsTimer = Integer.parseInt(properties.computeIfAbsent("limit_mob_farm_timer", (a) -> "32").toString());


//		Harder Farther Control Values"."HarderFarther Settings
		HarderFarther.onlyOverworld = properties.computeIfAbsent("only_overworld", (a) -> "false").equals("true");
		dimensionOmitList = properties.computeIfAbsent("dimension_omit_list", (a) -> "[\"minecraft:the_nether\", \"minecraft:the_end\"]").toString();
		HarderFarther.makeMonstersHarderFarther = properties.computeIfAbsent("make_monsters_harder_farther", (a) -> "true").equals("true");
		HarderFarther.boostMaxDistance = Integer.parseInt(properties.computeIfAbsent("boost_max_distance", (a) -> "30000").toString());
		HarderFarther.boostMinDistance = Integer.parseInt(properties.computeIfAbsent("boost_min_distance", (a) -> "1000").toString());
		HarderFarther.safeDistance = Integer.parseInt(properties.computeIfAbsent("safe_distance", (a) -> "64").toString());
		HarderFarther.minimalSafeAltitude = Integer.parseInt(properties.computeIfAbsent("minimal_safe_altitude", (a) -> "32").toString());
		HarderFarther.maximumSafeAltitude = Integer.parseInt(properties.computeIfAbsent("maximum_safe_altitude", (a) -> "99").toString());


//		Harder Farther Control Values"."Loot Settings
		HarderFarther.useLootDrops = properties.computeIfAbsent("use_loot_drops", (a) -> "true").equals("true");
		HarderFarther.oddsDropExperienceBottle = Integer.parseInt(properties.computeIfAbsent("odds_drop_experience_bottle", (a) -> "33").toString());
		lootItemsList = properties.computeIfAbsent("loot_items_list", (a) -> "[\"r,23,minecraft:netherite_scrap,1,1\", \"r,1,minecraft:nether_wart,1,2\", \"r,1,minecraft:music_disc_far,1,1\", \"u,2,minecraft:nether_wart,1,1\", \"u,3,minecraft:golden_carrot,1,1\", \"u,12,minecraft:diamond,1,1\", \"u,5,minecraft:emerald,1,3\", \"u,3,minecraft:oak_planks,1,5\", \"u,1,minecraft:book,1,1\", \"u,1,minecraft:gold_ingot,1,1\", \"u,2,minecraft:chicken,1,2\", \"u,5,minecraft:glowstone_dust,1,2\", \"u,1,minecraft:lead,1,1\", \"u,5,minecraft:stone_axe,1,2\", \"u,3,minecraft:stone_pickaxe,1,1\", \"u,1,minecraft:iron_axe,1,1\", \"u,1,minecraft:beetroot_seeds,1,1\", \"c,3,minecraft:leather_boots,1,1\", \"c,2,minecraft:gold_nugget,1,3\", \"c,2,minecraft:candle,1,2\", \"c,5,minecraft:baked_potato,1,2\", \"c,2,minecraft:fishing_rod,1,1\", \"c,5,minecraft:cooked_cod,1,3\", \"c,3,minecraft:string,1,2\", \"c,3,minecraft:iron_nugget,1,3\", \"c,3,minecraft:honey_bottle,1,2\", \"c,3,minecraft:stick,1,3\", \"c,1,minecraft:emerald,1,1\", \"c,1,minecraft:paper,1,2\"]").toString();


//		Harder Farther Control Values"."Boost Settings
		HarderFarther.hpMaxBoost = Integer.parseInt(properties.computeIfAbsent("hp_max_boost", (a) -> "200").toString());
		HarderFarther.speedBoost = Integer.parseInt(properties.computeIfAbsent("speed_boost", (a) -> "20").toString());
		HarderFarther.atkDmgBoost = Integer.parseInt(properties.computeIfAbsent("atk_dmg_boost", (a) -> "100").toString());
		HarderFarther.knockbackBoost = Integer.parseInt(properties.computeIfAbsent("knockback_boost", (a) -> "95").toString());


//		Harder Farther Control Values"."Harder Over Time Settings
		HarderFarther.makeHarderOverTime = properties.computeIfAbsent("make_harder_over_time", (a) -> "false").equals("true");
		HarderFarther.maxHarderTimeMinutes = Integer.parseInt(properties.computeIfAbsent("max_harder_time_minutes", (a) -> "720").toString());



//		Harder Farther Control Values"."Grim Citadel Settings
		HarderFarther.useGrimCitadels = properties.computeIfAbsent("use_grim_citadels", (a) -> "true").equals("true");
		grimCitadelsList = properties.computeIfAbsent("grim_citadels_list", (a) -> "[\"3600,3500\", \"3500,-100\", \"3500,-3550\", \"0,3596\", \"128,-3500\", \"-2970,3516\", \"-3517,80\", \"-3528,-3756\"]").toString();
		HarderFarther.grimCitadelsCount = Integer.parseInt(properties.computeIfAbsent("grim_citadels_count", (a) -> "5").toString());
		HarderFarther.grimCitadelsRaidus = Integer.parseInt(properties.computeIfAbsent("grim_citadels_radius", (a) -> "5").toString());
		HarderFarther.grimCitadelBonusDistance = Integer.parseInt(properties.computeIfAbsent("grim_citadel_bonus_distance", (a) -> "1750").toString());
		HarderFarther.grimCitadelPlayerCurseDistance = Integer.parseInt(properties.computeIfAbsent("grim_citadel_player_curse_distance", (a) -> "1250").toString());
		HarderFarther.grimCitadelMaxBoostPercent = Integer.parseInt(properties.computeIfAbsent("grim_citadel_max_boost_percent", (a) -> "96").toString());


//		Harder Farther Control Values"."Grim Effects Settings
		HarderFarther.grimEffectTrees = properties.computeIfAbsent("grim_effect_trees", (a) -> "true").equals("true");
		HarderFarther.grimEffectAnimals = properties.computeIfAbsent("grim_effect_animals", (a) -> "true").equals("true");
		HarderFarther.grimEffectPigs = properties.computeIfAbsent("grim_effect_pigs", (a) -> "true").equals("true");
		HarderFarther.grimEffectVillagers = properties.computeIfAbsent("grim_effect_villagers", (a) -> "true").equals("true");
		HarderFarther.grimLifeHeartPulseSeconds = Integer.parseInt(properties.computeIfAbsent("grim_life_heart_pulse_seconds", (a) -> "120").toString());
//			Grim Fog Color Settings
		HarderFarther.grimFogRedPercent = Double.parseDouble(properties.computeIfAbsent("grim_fog_red_percent", (a) -> "0.95").toString());
		HarderFarther.grimFogBluePercent = Double.parseDouble(properties.computeIfAbsent("grim_fog_blue_percent", (a) -> "0.05").toString());
		HarderFarther.grimFogGreenPercent = Double.parseDouble(properties.computeIfAbsent("grim_fog_green_percent", (a) -> "0.05").toString());




		computeConfigValues();

		saveConfig();
	}

	private static File getConfigFile() {
		final File configDir = Platform.configDirectory().toFile();

		if (!configDir.exists()) {
			HarderFarther.LOG.warn("[Harder Farther] Could not access configuration directory: " + configDir.getAbsolutePath());
		}

		return new File(configDir, "harderfarther.properties");
	}

	public static void saveConfig() {
		final File configFile = getConfigFile();
		final Properties properties = new Properties();


//		Harder Farther Control Values"."Debug Settings
		properties.put("debug_level", Integer.toString(HarderFarther.debugLevel));


//		Harder Farther Control Values"."Farm Limiter Settings
		properties.put("limit_mob_farms_timer", Integer.toString(HarderFarther.limitMobFarmsTimer));


//		Harder Farther Control Values"."HarderFarther Settings
		properties.put("only_overworld", Boolean.toString(HarderFarther.onlyOverworld));
		properties.put("dimension_omit_list", HarderFarther.dimensionOmitList.toString());
		properties.put("make_monsters_harder_farther", Boolean.toString(HarderFarther.makeMonstersHarderFarther));
		properties.put("boost_max_distance", Integer.toString(HarderFarther.boostMaxDistance));
		properties.put("boost_min_distance", Integer.toString(HarderFarther.boostMinDistance));
		properties.put("safe_distance", Integer.toString(HarderFarther.safeDistance));


//		Harder Farther Control Values"."Loot Settings
		properties.put("use_loot_drops", Boolean.toString(HarderFarther.useLootDrops));
		properties.put("odds_drop_experience_bottle", Integer.toString(HarderFarther.boostMaxDistance));


//		Harder Farther Control Values"."Boost Settings
		properties.put("hp_max_boost", Integer.toString(HarderFarther.hpMaxBoost));
		properties.put("speed_boost", Integer.toString(HarderFarther.hpMaxBoost));
		properties.put("atk_dmg_boost", Integer.toString(HarderFarther.atkDmgBoost));
		properties.put("knockback_boost", Integer.toString(HarderFarther.knockbackBoost));


//		Harder Farther Control Values"."Harder Over Time Settings
		properties.put("make_harder_over_time", Boolean.toString(HarderFarther.makeHarderOverTime));
		properties.put("max_harder_time_minutes", Integer.toString(HarderFarther.knockbackBoost));


//		Harder Farther Control Values"."Grim Citadel Settings
		properties.put("use_grim_citadels", Boolean.toString(HarderFarther.useGrimCitadels));
		properties.put("grim_citadels_list", PrimaryConfig.grimCitadelsList);
		properties.put("grim_citadels_count", Integer.toString(HarderFarther.grimCitadelsCount));
		properties.put("grim_citadels_radius", Integer.toString(HarderFarther.grimCitadelsRaidus));
		properties.put("grim_citadel_bonus_distance", Integer.toString(HarderFarther.grimCitadelBonusDistance));
		properties.put("grim_citadel_player_curse_distance", Integer.toString(HarderFarther.grimCitadelPlayerCurseDistance));
		properties.put("grim_citadel_max_boost_percent", Integer.toString(HarderFarther.grimCitadelMaxBoostPercent));


//		Harder Farther Control Values"."Grim Effects Settings
		properties.put("grim_effect_trees", Boolean.toString(HarderFarther.grimEffectTrees));
		properties.put("grim_effect_animals", Boolean.toString(HarderFarther.grimEffectAnimals));
		properties.put("grim_effect_pigs", Boolean.toString(HarderFarther.grimEffectPigs));
		properties.put("grim_effect_villagers", Boolean.toString(HarderFarther.grimEffectVillagers));
		properties.put("grim_life_heart_pulse_seconds", Integer.toString(HarderFarther.grimLifeHeartPulseSeconds));
//			Grim Fog Color Settings


		try (FileOutputStream stream = new FileOutputStream(configFile)) {
			properties.store(stream, "Harder Farther properties file");
		} catch (final IOException e) {
			HarderFarther.LOG.warn("[HarderFarther] Could not store property file '" + configFile.getAbsolutePath() + "'", e);
		}
	}

	private static void computeConfigValues() {

		HarderFarther.debugLevel = MathHelper.clamp(HarderFarther.debugLevel, 0, 2);
		HarderFarther.boostMaxDistance = HarderFarther.boostMaxDistance > 0 ? HarderFarther.boostMaxDistance : 0;
		HarderFarther.boostMaxDistance = HarderFarther.boostMaxDistance > 0 ? HarderFarther.boostMaxDistance : 0;

		HarderFarther.dimensionOmitList = List.of(PrimaryConfig.dimensionOmitList.substring(1, PrimaryConfig.dimensionOmitList.length() - 1).split(", "));
		HarderFarther.lootItemsList = List.of(PrimaryConfig.lootItemsList.substring(1, PrimaryConfig.lootItemsList.length() - 1).split(", "));
		HarderFarther.grimCitadelsBlockPosList = getBlockPositions(List.of(PrimaryConfig.grimCitadelsList.substring(1, PrimaryConfig.grimCitadelsList.length() - 1).split(", ")));

	}

	private static List<BlockPos> getBlockPositions(List<? extends String> list) {

		List< BlockPos> returnList = new ArrayList<>();
		for (String pos : list) {
			String[] posParts = pos.split(",");
			int x = Integer.valueOf(posParts[0]);
			int y = -1;
			int z = Integer.valueOf(posParts[1]);
			returnList.add(new BlockPos(x,y,z));
		}
		return returnList;
	}



	public static boolean isDimensionOmitted(String dimensionName) {
			return dimensionOmitList.contains(dimensionName);
	}

	public static int getBoostMaxDistance() {
		return boostMaxDistance;
	}

	public static void setBoostMaxDistance(int modifierMaxDistance) {
		PrimaryConfig.boostMaxDistance = modifierMaxDistance;
	}

	public static int getBoostMinDistance() {
		if (boostMinDistance >= boostMaxDistance) {
			return boostMaxDistance - 1;
		}
		return boostMinDistance;
	}

	public static void setBoostMinDistance(int boostMinDistance) {
		PrimaryConfig.boostMinDistance = boostMinDistance;
	}

	public static int getSafeDistance() {
		return safeDistance;
	}

	public static void setSafeDistance(int safeDistance) {
		PrimaryConfig.safeDistance = safeDistance;
	}

	public static boolean isHpMaxBoosted() {
		if (hpMaxBoost > 0) return true;
		return false;
	}

	public static boolean isSpeedBoosted() {
		if (speedBoost > 0) return true;
		return false;
	}

	public static boolean isAtkDmgBoosted() {
		if (atkDmgBoost > 0) return true;
		return false;
	}

	public static boolean isKnockBackBoosted() {
		if (knockbackBoost > 0) return true;
		return false;
	}

	public static int getHpMaxBoost() {
		return hpMaxBoost;
	}

	public static int getSpeedBoost() {
		return speedBoost;
	}

	public static int getAtkDmgBoost() {
		return atkDmgBoost;
	}

	public static int getKnockBackMod() {
		return knockbackBoost;
	}

	public static float getHpMaxPercent() {
		return (float) (hpMaxBoost/100);
	}

	public static float getSpeedPercent()  {
		return ((float)speedBoost/100);
	}

	public static float getAtkPercent()  {
		return (float) (atkDmgBoost/100);
	}

	public static float getKnockBackPercent() {
		return (float) (knockbackBoost/100);
	}
	
	public static int getMobFarmingLimitingTimer() {
		return limitMobFarmsTimer;
	}
	
	public static boolean isMakeHarderOverTime() {
		return makeHarderOverTime;
	}

	public static void setMakeHarderOverTime(boolean newValue) {
		PrimaryConfig.makeHarderOverTime = newValue;
		COMMON.makeHarderOverTime.set(newValue);
	}

	public static int getMaxHarderTimeMinutes() {
		return maxHarderTimeMinutes;
	}

	public static void setMaxHarderTimeMinutes(int newValue) {
		PrimaryConfig.maxHarderTimeMinutes = newValue;
		COMMON.maxHarderTimeMinutes.set(newValue);	
	}



	public static boolean isMakeMonstersHarderFarther() {
		return makeMonstersHarderFarther;
	}


	public static int getMinimumSafeAltitude() {
		return minimumSafeAltitude;
	}

	public static int getMaximumSafeAltitude() {
		return maximumSafeAltitude;
	}


	public static int getOddsDropExperienceBottle() {
		return oddsDropExperienceBottle;
	}

	public static boolean isUseGrimCitadels() {
		return useGrimCitadels;
	}
	
	public static int getGrimCitadelsRadius() {
		return grimCitadelsRadius;
	}

	public static void setGrimCitadelsRadius(int grimCitadelsRadius) {
		PrimaryConfig.grimCitadelsRadius = grimCitadelsRadius;
		COMMON.grimCitadelsRadius.set(grimCitadelsRadius);
	}

	public static int getGrimCitadelMaxBoostValue() {
		return grimCitadelMaxBoostPercent;
	}

	public static float getGrimCitadelMaxBoostPercent() {
		return (float)(grimCitadelMaxBoostPercent)/100;
	}
	
	public static void setGrimCitadelMaxBoostPercent(int newValue) {
		PrimaryConfig.grimCitadelMaxBoostPercent = newValue;
		COMMON.grimCitadelMaxBoostPercent.set(newValue);
	}
	
	public static int getGrimCitadelsCount() {
		return grimCitadelsCount;
	}

	public static int getGrimCitadelBonusDistance() {
		return grimCitadelBonusDistance;
	}
	
	public static int getGrimCitadelBonusDistanceSq() {
		return grimCitadelBonusDistanceSq;
	}
	
	public static int getGrimCitadelPlayerCurseDistance() {
		return grimCitadelPlayerCurseDistance;
	}

	public static int getGrimCitadelPlayerCurseDistanceSq() {
		return grimCitadelPlayerCurseDistanceSq;
	}

	public static List<BlockPos> getGrimCitadelsBlockPosList() {
		return grimCitadelsBlockPosList;
	}

	public static void setGrimCitadelsBlockPosList(List<BlockPos> grimCitadelsBlockPosList) {
		PrimaryConfig.grimCitadelsBlockPosList = grimCitadelsBlockPosList;
	}
	
	public static boolean isGrimEffectTrees() {
		return grimEffectTrees;
	}

	public static void setGrimEffectTrees(boolean grimEffectTrees) {
		PrimaryConfig.grimEffectTrees = grimEffectTrees;
	}

	public static boolean isGrimEffectAnimals() {
		return grimEffectAnimals;
	}
	
	public static boolean isGrimEffectPigs() {
		return grimEffectPigs;
	}

	public static boolean isGrimEffectVillagers() {
		return grimEffectVillagers;
	}
	
	public static int getGrimLifeheartPulseSeconds() {
		return grimLifeheartPulseSeconds;
	}

	public static double getGrimFogRedPercent() {
		return grimFogRedPercent;
	}

	public static double getGrimFogBluePercent() {
		return grimFogBluePercent;
	}

	public static double getGrimFogGreenPercent() {
		return grimFogGreenPercent;
	}
	
	public static void setGrimFogRedPercent(double grimFogRedPercent) {
		PrimaryConfig.grimFogRedPercent = grimFogRedPercent/100;
		COMMON.grimFogRedPercent.set(grimFogRedPercent/100);
	}
	public static void setGrimFogGreenPercent(double grimFogGreenPercent) {
		PrimaryConfig.grimFogGreenPercent = grimFogGreenPercent/100;
		COMMON.grimFogGreenPercent.set(grimFogGreenPercent/100);
	}

	public static void setGrimFogBluePercent(double grimFogBluePercent) {
		PrimaryConfig.grimFogBluePercent = grimFogBluePercent/100;
		COMMON.grimFogBluePercent.set(grimFogBluePercent/100);
	}

	private static int 	    limitMobFarmsTimer;

	private static int      safeDistance;
	private static int      oddsDropExperienceBottle;

	private static int hpMaxBoost;
	private static int speedBoost;
	private static int atkDmgBoost;
	private static int knockbackBoost;

	private static boolean  makeHarderOverTime;
	private static int      maxHarderTimeMinutes;

	private static boolean  useGrimCitadels;
	private static int      grimCitadelsRadius;
	private static int      grimCitadelsCount;
	private static int 		grimCitadelMaxBoostPercent;
	private static int 	    grimCitadelBonusDistance;
	private static int 	    grimCitadelBonusDistanceSq;
	private static int 		grimCitadelPlayerCurseDistance;
	private static int 		grimCitadelPlayerCurseDistanceSq;

	private static boolean  grimEffectTrees;
	private static boolean  grimEffectAnimals;
	private static boolean  grimEffectPigs;
	private static boolean  grimEffectVillagers;
	private static int      grimLifeheartPulseSeconds;
	
	private static double 	grimFogRedPercent;
	private static double 	grimFogGreenPercent;
	private static double 	grimFogBluePercent;

	private static List<BlockPos> grimCitadelsBlockPosList;
	
	private static int      minimumSafeAltitude;
	private static int      maximumSafeAltitude;
	public static final int KILLER_ANY   = 0;
	public static final int KILLER_MOB_OR_PLAYER = 1;
	public static final int KILLER_PLAYER = 2;

	@SubscribeEvent
	public static <ModConfig> void onModConfigEvent(final ModConfigEvent configEvent)
	{

		if (configEvent.getConfig().getSpec() == PrimaryConfig.COMMON_SPEC)
		{
			bakeConfig();
		}
	}	


	public static void pushValues() {
		COMMON.debugLevel.set(debugLevel);

		COMMON.limitMobFarmsTimer.set(limitMobFarmsTimer);
		
		COMMON.onlyOverworld.set(onlyOverworld);
		COMMON.dimensionOmitList.set(dimensionOmitList);
		COMMON.makeMonstersHarderFarther.set(makeMonstersHarderFarther);
		COMMON.useLootDrops.set(useLootDrops);
		COMMON.boostMaxDistance.set(boostMaxDistance);
		COMMON.boostMinDistance.set(boostMinDistance);

		COMMON.safeDistance.set(safeDistance);
		COMMON.minimumSafeAltitude.set(minimumSafeAltitude);
		COMMON.maximumSafeAltitude.set(maximumSafeAltitude);

		COMMON.lootItemsList.set(lootItemsList);
		COMMON.oddsDropExperienceBottle.set(oddsDropExperienceBottle);
		
		COMMON.hpMaxBoost.set(hpMaxBoost);
		COMMON.speedBoost.set(speedBoost);
		COMMON.atkDmgBoost.set(atkDmgBoost);
		COMMON.knockbackBoost.set(knockbackBoost);
		
		COMMON.useGrimCitadels.set(useGrimCitadels);
		COMMON.grimCitadelsRadius.set(grimCitadelsRadius);
		COMMON.grimCitadelsCount.set(grimCitadelsCount);
		COMMON.grimCitadelsList.set(grimCitadelsList);
		COMMON.grimCitadelMaxBoostPercent.set(grimCitadelMaxBoostPercent);
		COMMON.grimCitadelBonusDistance.set(grimCitadelBonusDistance);
		COMMON.grimCitadelPlayerCurseDistance.set(grimCitadelPlayerCurseDistance);
		
		COMMON.grimEffectTrees.set(grimEffectTrees);
		COMMON.grimEffectAnimals.set(grimEffectAnimals);
		COMMON.grimEffectPigs.set(grimEffectPigs);
		COMMON.grimEffectVillagers.set(grimEffectVillagers);
		COMMON.grimLifeheartPulseSeconds.set(grimLifeheartPulseSeconds);
		
		COMMON.grimFogRedPercent.set (grimFogRedPercent);
		COMMON.grimFogBluePercent.set (grimFogBluePercent);
		COMMON.grimFogGreenPercent.set (grimFogGreenPercent);
	}
	
	public static void setUseGrimCitadels(boolean newValue) {
		COMMON.useGrimCitadels.set(newValue);
		useGrimCitadels = COMMON.useGrimCitadels.get();
	}

	
	public static void setBonusRange(int newRange) {
		COMMON.grimCitadelBonusDistance.set(newRange);
		COMMON.grimCitadelPlayerCurseDistance.set((int)(newRange*0.7f));
		bakeGrimRanges();
	}

	private static void bakeGrimRanges() {
		grimCitadelBonusDistance = COMMON.grimCitadelBonusDistance.get();
		grimCitadelBonusDistanceSq = grimCitadelBonusDistance*grimCitadelBonusDistance;
		grimCitadelPlayerCurseDistance = COMMON.grimCitadelPlayerCurseDistance.get();
		grimCitadelPlayerCurseDistanceSq = grimCitadelPlayerCurseDistance * grimCitadelPlayerCurseDistance;
	}

	public static void setOddsDropExperienceBottle(int newOdds) {
		COMMON.oddsDropExperienceBottle.set(newOdds);
		oddsDropExperienceBottle = newOdds;
	}
	
	// remember need to push each of these values separately once we have commands.
	// this copies file changes into the running program variables.
	
	public static void bakeConfig()
	{
		debugLevel = COMMON.debugLevel.get();

		limitMobFarmsTimer = COMMON.limitMobFarmsTimer.get();
		
		onlyOverworld = COMMON.onlyOverworld.get();

		dimensionOmitList = COMMON.dimensionOmitList.get();
		makeMonstersHarderFarther = COMMON.makeMonstersHarderFarther.get();
		boostMaxDistance = COMMON.boostMaxDistance.get();
		boostMinDistance = COMMON.boostMinDistance.get();
		if (boostMinDistance >= boostMaxDistance) {
			LOGGER.error("ERROR: boostMinDistance should be less than boostMaxDistance.");
			LOGGER.error("ERROR: boostMinDistance will use (boostMaxDistance - 1).");
			boostMinDistance = boostMaxDistance-1;
			COMMON.boostMinDistance.set(boostMinDistance);
		}
		minimumSafeAltitude = COMMON.minimumSafeAltitude.get();
		maximumSafeAltitude = COMMON.maximumSafeAltitude.get();
		safeDistance =COMMON.safeDistance.get();

		lootItemsList = COMMON.lootItemsList.get();
		LootManager.initLootItems(extract(lootItemsList));
		useLootDrops = COMMON.useLootDrops.get();
		oddsDropExperienceBottle = COMMON.oddsDropExperienceBottle.get();
		
		hpMaxBoost=COMMON.hpMaxBoost.get();
		speedBoost=COMMON.speedBoost.get();
		atkDmgBoost=COMMON.atkDmgBoost.get();
		knockbackBoost=COMMON.knockbackBoost.get();

		makeHarderOverTime = COMMON.makeHarderOverTime.get() ;
		maxHarderTimeMinutes = COMMON.maxHarderTimeMinutes.get() ;

		
		useGrimCitadels = COMMON.useGrimCitadels.get();
		grimCitadelsList = COMMON.grimCitadelsList.get();
		grimCitadelsCount = COMMON.grimCitadelsCount.get();
		grimCitadelsRadius= COMMON.grimCitadelsRadius.get();
		grimCitadelMaxBoostPercent = COMMON.grimCitadelMaxBoostPercent.get();
		bakeGrimRanges();


		grimEffectTrees = COMMON.grimEffectTrees.get();
		grimEffectAnimals = COMMON.grimEffectAnimals.get();
		grimEffectPigs = COMMON.grimEffectPigs.get();
		grimEffectVillagers = COMMON.grimEffectVillagers.get();
		grimLifeheartPulseSeconds = COMMON.grimLifeheartPulseSeconds.get();
		
		grimFogRedPercent = COMMON.grimFogRedPercent.get();
		grimFogBluePercent = COMMON.grimFogBluePercent.get();
		grimFogGreenPercent = COMMON.grimFogGreenPercent.get();

	}
	


	private static String[] extract(List<? extends String> value)
	{
		return value.toArray(new String[value.size()]);
	}
	
}

