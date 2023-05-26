package com.mactso.harderfarther.command;

import java.util.Iterator;
import java.util.List;

import com.mactso.harderfarther.config.PrimaryConfig;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.manager.HarderTimeManager;
import com.mactso.harderfarther.manager.LootManager;
import com.mactso.harderfarther.network.SyncFogToClientsPacket;
import com.mactso.harderfarther.utility.Utility;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class HarderFartherCommands {


	private static void printColorInfo(ServerPlayerEntity p) {

		String chatMessage = "\nFog Color Current Values";
		Utility.sendBoldChat(p, chatMessage, Formatting.DARK_GREEN);
		chatMessage = "R (" + PrimaryConfig.getGrimFogRedPercent() + ")" + " G (" + PrimaryConfig.getGrimFogGreenPercent() + ")"
				+ " B (" + PrimaryConfig.getGrimFogBluePercent() + ")";
		Utility.sendChat(p, chatMessage, Formatting.GREEN);

	}

	private static void printGrimEffectsInfo(ServerPlayerEntity p) {

		Utility.sendBoldChat(p, "\nGrim Effects Info", Formatting.DARK_GREEN);
		if (PrimaryConfig.isUseGrimCitadels()) {
			String chatMessage = (
					"\n  Effect Villagers ..............................: " + PrimaryConfig.isGrimEffectVillagers()
					+ "\n  Effect Trees .....................................: " + PrimaryConfig.isGrimEffectTrees()
					+ "\n  Effect Animals ...................................: " + PrimaryConfig.isGrimEffectAnimals()
					+ "\n  Effect Pigs ..........................................: " + PrimaryConfig.isGrimEffectPigs()
				);

			Utility.sendChat(p, chatMessage, Formatting.GREEN);
		} else {
			Utility.sendChat(p, "\n  Grim Citadels Disabled", Formatting.DARK_GREEN);
		}

	}

	private static void printGrimInfo(ServerPlayerEntity p) {
		BlockPos pPos = p.getBlockPos();
		float grimDifficulty = Math.min(PrimaryConfig.getGrimCitadelMaxBoostValue(),
				100*GrimCitadelManager.getGrimDifficulty((LivingEntity) p));

		Utility.sendBoldChat(p, "\nGrim Citadel Information", Formatting.DARK_GREEN);
		if (PrimaryConfig.isUseGrimCitadels()) {
			String chatMessage = ("   Grim Citadels are Enabled" 
					+ "\n   Nearest Grim Citadel ................................: "
					+ (int) Math.sqrt(GrimCitadelManager.getClosestGrimCitadelDistanceSq(pPos)) + " meters at "
					+ "\n    " + GrimCitadelManager.getClosestGrimCitadelPos(pPos)
					+ "\n   Aura Range ......................................................: "
					+ PrimaryConfig.getGrimCitadelBonusDistance() + " blocks." 
					+ "\n   Player Curse Range ................................: "
					+ PrimaryConfig.getGrimCitadelPlayerCurseDistance() + " blocks."
					+ "\n   Maximum Difficulty .......................................: "
					+ PrimaryConfig.getGrimCitadelMaxBoostValue() + "%"
					+ "\n   Current Difficulty ......................................: "
					+ grimDifficulty + "%"
					+ "\n   Minimum Number of Grim Citadels.......: "
					+ PrimaryConfig.getGrimCitadelsCount()
					+ "\n   Citadel Radius  ..............................................: "
					+ GrimCitadelManager.getGrimRadius());
			Utility.sendChat(p, chatMessage, Formatting.GREEN);
		} else {
			Utility.sendChat(p, "\n  Grim Citadels Disabled", Formatting.DARK_GREEN);
		}

	}

	private static void printGrimMusicInfo(ServerPlayerEntity p) {
		String chatMessage = "\nMusic Attribution";
		Utility.sendBoldChat(p, chatMessage, Formatting.DARK_GREEN);
		chatMessage = "Attribution Tags for Ambient Music\n"
				+ "\n"
				+ "Lake of Destiny by Darren Curtis | https://www.darrencurtismusic.com/\n"
				+ "Music promoted by https://www.chosic.com/free-music/all/\n"
				+ "Creative Commons Attribution 3.0 Unported License\n"
				+ "https://creativecommons.org/licenses/by/3.0/\n"
				+ "\n"
				+ "Dusty Memories by Darren Curtis | https://www.darrencurtismusic.com/\n"
				+ "Music promoted by https://www.chosic.com/free-music/all/\n"
				+ "Creative Commons Attribution 3.0 Unported License\n"
				+ "https://creativecommons.org/licenses/by/3.0/\n"
				+ " \n"
				+ "Labyrinth of Lost Dreams by Darren Curtis | https://www.darrencurtismusic.com/\n"
				+ "Music promoted on https://www.chosic.com/free-music/all/\n"
				+ "Creative Commons Attribution 3.0 Unported (CC BY 3.0)\n"
				+ "https://creativecommons.org/licenses/by/3.0/\n"
				+ " \n"
				+ "\n"
				+ "";
		Utility.sendChat(p, chatMessage, Formatting.GREEN);
		
	}
	
	
	
	private static void printInfo(ServerPlayerEntity p) {

		String dimensionName = p.world.getRegistryKey().getValue().toString();

		String chatMessage = "\nDimension: " + dimensionName + "\n Current Values";
		Utility.sendBoldChat(p, chatMessage, Formatting.DARK_GREEN);

		chatMessage = "  Harder Max Distance From Spawn....: " + PrimaryConfig.getBoostMaxDistance() + " blocks."
				+ "\n  Spawn Safe Distance ..................................: " + PrimaryConfig.getSafeDistance()
				+ " blocks." + "\n  Debug Level .......................................................: "
				+ PrimaryConfig.getDebugLevel() + "\n  Only In Overworld .........................................: "
				+ PrimaryConfig.isOnlyOverworld() + "\n  Grim Citadels Active .....................................: "
				+ PrimaryConfig.isUseGrimCitadels();
		Utility.sendChat(p, chatMessage, Formatting.GREEN);

	}
	
	public static void register(){

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

			dispatcher.register(CommandManager.literal("harderfarther").requires((source) ->
							{
								return source.hasPermissionLevel(3);
							}
					)
					.then(CommandManager.literal("setDebugLevel").then(
							CommandManager.argument("debugLevel", IntegerArgumentType.integer(0, 2)).executes(ctx -> {
								ServerPlayerEntity p = ctx.getSource().getPlayer();
								return setDebugLevel(p, IntegerArgumentType.getInteger(ctx, "debugLevel"));
							})))
					// update or add a speed value for the block the player is standing on.
					.then(CommandManager.literal("setBonusRange").then(
							CommandManager.argument("bonusRange", IntegerArgumentType.integer(500, 6000)).executes(ctx -> {
								ServerPlayerEntity p = ctx.getSource().getPlayer();
								return setBonusRange(p, IntegerArgumentType.getInteger(ctx, "bonusRange"));
							})))
					.then(CommandManager.literal("setUseGrimCitadels").then(
							CommandManager.argument("useGrimCitadels", BoolArgumentType.bool()).executes(ctx -> {
								ServerPlayerEntity p = ctx.getSource().getPlayer();
								return setUseGrimCitadels(p, BoolArgumentType.getBool(ctx, "useGrimCitadels"));
							})))
					.then(CommandManager.literal("setGrimCitadelsRadius").then(
							CommandManager.argument("grimCitadelsRadius", IntegerArgumentType.integer(4, 11)).executes(ctx -> {
								ServerPlayerEntity p = ctx.getSource().getPlayer();
								return setGrimCitadelsRadius(p, IntegerArgumentType.getInteger(ctx, "grimCitadelsRadius"));
							})))
					.then(CommandManager.literal("setMakeHarderOverTime").then(
							CommandManager.argument("setMakeHarderOverTime", BoolArgumentType.bool()).executes(ctx -> {
								ServerPlayerEntity p = ctx.getSource().getPlayer();
								return setMakeHarderOverTime(p, BoolArgumentType.getBool(ctx, "setMakeHarderOverTime"));
							})))
					.then(CommandManager.literal("setMaxHarderTimeMinutes").then(
							CommandManager.argument("setMaxHarderTimeMinutes", IntegerArgumentType.integer(20, 28800)).executes(ctx -> {
								ServerPlayerEntity p = ctx.getSource().getPlayer();
								return setMaxHarderTimeMinutes(p, IntegerArgumentType.getInteger(ctx, "setMaxHarderTimeMinutes"));
							})))
					.then(CommandManager.literal("setXpBottleChance").then(
							CommandManager.argument("xpBottleChance", IntegerArgumentType.integer(0, 33)).executes(ctx -> {
								ServerPlayerEntity p = ctx.getSource().getPlayer();
								return setOddsDropExperienceBottle(p, IntegerArgumentType.getInteger(ctx, "xpBottleChance"));
							})))
					.then(CommandManager.literal("chunkReport").executes(ctx -> {
						ServerPlayerEntity p = ctx.getSource().getPlayer();
						p.world.asString();
						Utility.sendChat(p, "\nChunk\n" + p.world.asString(), Formatting.GREEN);
						return 1;
					}))
					.then(CommandManager.literal("grimReport").executes(ctx -> {
						ServerPlayerEntity p = ctx.getSource().getPlayer();
						String report = "Grim Citadels at : " + GrimCitadelManager.getCitadelListAsString();
						Utility.sendChat(p, report, Formatting.GREEN);
						return 1;
					})).then(CommandManager.literal("lootReport").executes(ctx -> {
						ServerPlayerEntity p = ctx.getSource().getPlayer();
						String report = LootManager.report();
						Utility.sendBoldChat(p, "\nLoot Report", Formatting.GOLD);
						Utility.sendChat(p, report, Formatting.YELLOW);
						reportUseLootDrop(p);
						reportOddsXpBottleDrop(p);
						reportGrimLifeHeartPulseSeconds(p);
						return 1;
					})).then(CommandManager.literal("info").executes(ctx -> {
						ServerPlayerEntity p = ctx.getSource().getPlayer();
						printInfo(p);
						return 1;
						// return 1;
					})).then(CommandManager.literal("grimEffectsInfo").executes(ctx -> {
						ServerPlayerEntity p = ctx.getSource().getPlayer();
						printGrimEffectsInfo(p);
						return 1;
					})).then(CommandManager.literal("grimInfo").executes(ctx -> {
						ServerPlayerEntity p = ctx.getSource().getPlayer();
						printGrimInfo(p);
						return 1;
					})).then(CommandManager.literal("timeInfo").executes(ctx -> {
						ServerPlayerEntity p = ctx.getSource().getPlayer();
						printTimeInfo(p);
						return 1;
					})).then(CommandManager.literal("colorInfo").executes(ctx -> {
						ServerPlayerEntity p = ctx.getSource().getPlayer();
						printColorInfo(p);
						return 1;
					})).then(CommandManager.literal("boostInfo").executes(ctx -> {
						ServerPlayerEntity p = ctx.getSource().getPlayer();
						String chatMessage = "\nHarder Farther Maximum Monster Boosts";
						Utility.sendBoldChat(p, chatMessage, Formatting.DARK_GREEN);

						chatMessage = "  Monster Health ..........................: " + PrimaryConfig.getHpMaxBoost() + " %."
								+ "\n  Damage ..............................................: " + PrimaryConfig.getAtkDmgBoost()
								+ " %." + "\n  Movement .........................................: "
								+ PrimaryConfig.getSpeedBoost() + " %." + "\n  KnockBack Resistance .........: "
								+ PrimaryConfig.getKnockBackMod() + " %.";
						Utility.sendChat(p, chatMessage, Formatting.GREEN);
						return 1;
					}))
					.then(CommandManager.literal("musicInfo").executes(ctx -> {
						ServerPlayerEntity p = ctx.getSource().getPlayer();
						printGrimMusicInfo(p);
						return 1;
					})).then(CommandManager.literal("setFogColors")
							.then(CommandManager.argument("R", IntegerArgumentType.integer(0, 100))
									.then(CommandManager.argument("G", IntegerArgumentType.integer(0, 100))
											.then(CommandManager.argument("B", IntegerArgumentType.integer(0, 100))
													.executes(ctx -> {
														ServerPlayerEntity p = ctx.getSource().getPlayer();
														int r = IntegerArgumentType.getInteger(ctx, "R");
														int g = IntegerArgumentType.getInteger(ctx, "G");
														int b = IntegerArgumentType.getInteger(ctx, "B");
														return setFogColors(p, r, g, b);
													}))))
					));
		});

	}


	private static int setMakeHarderOverTime(ServerPlayerEntity p, boolean b) {
		PrimaryConfig.setMakeHarderOverTime(b);
		printTimeInfo(p);
		return 1;
	}

	
	private static int setMaxHarderTimeMinutes(ServerPlayerEntity p, int newValue) {
		PrimaryConfig.setMaxHarderTimeMinutes(newValue);
		printTimeInfo(p);
		return 1;
	}

	private static void printTimeInfo(ServerPlayerEntity p) {
		long time = p.world.getChunk(p.getBlockPos()).getInhabitedTime() / 1200;
		float timeDifficulty = 100* HarderTimeManager.getTimeDifficulty(p.getWorld(), (LivingEntity) p);
		Utility.sendBoldChat(p,"\nTime Info ", Formatting.AQUA);
		Utility.sendChat(p, "  Make Harder Over Time .........: " + PrimaryConfig.isMakeHarderOverTime() +".", Formatting.AQUA);
		Utility.sendChat(p, "  Maximum Difficulty .......................: " + PrimaryConfig.getMaxHarderTimeMinutes() +" minutes.", Formatting.AQUA);
		Utility.sendChat(p, "  Current Difficulty ......................: " + timeDifficulty +" %.", Formatting.AQUA);
			Utility.sendChat(p, "  Current Chunk Age ...................: " + time+" minutes.", Formatting.AQUA);

	}

	
	private static void reportUseLootDrop(ServerPlayerEntity p) {
		Utility.sendChat(p, "  Use Loot Drops ...................: " + PrimaryConfig.isUseLootDrops() +".", Formatting.YELLOW);
	}
	
	private static void reportOddsXpBottleDrop(ServerPlayerEntity p) {
		Utility.sendChat(p, "  OddsXpBottleDrop ..............: " + PrimaryConfig.getOddsDropExperienceBottle() +"%", Formatting.YELLOW);
	}

	private static void reportGrimLifeHeartPulseSeconds(ServerPlayerEntity p) {
		Utility.sendChat(p, "  Life Heart Pulse Rate......: " + "roughly " + PrimaryConfig.getGrimLifeheartPulseSeconds() +" seconds +/- 50%.", Formatting.YELLOW);
	}
	
	public static int setBonusRange(ServerPlayerEntity p, int newRange) {
		PrimaryConfig.setBonusRange(newRange);
		printGrimInfo(p);
		return 1;
	}

	public static int setDebugLevel(ServerPlayerEntity p, int newDebugLevel) {
		PrimaryConfig.setDebugLevel(newDebugLevel);
		printInfo(p);
		return 1;
	}


	private static int setFogColors(ServerPlayerEntity p, int r, int g, int b) {
		PrimaryConfig.setGrimFogRedPercent(r);
		PrimaryConfig.setGrimFogGreenPercent(g);
		PrimaryConfig.setGrimFogBluePercent(b);
		updateGCFogToAllClients ((ServerWorld)p.world, (double)r/100, (double)g/100, (double)b/100);
		printColorInfo(p);
		return 1;
	}
	
	public static int setUseGrimCitadels(ServerPlayerEntity p, boolean newValue) {
		PrimaryConfig.setUseGrimCitadels(newValue);
		printGrimInfo(p);
		return 1;
	}

	private static int setGrimCitadelsRadius(ServerPlayerEntity p, int radius) {
		PrimaryConfig.setGrimCitadelsRadius(radius);
		printGrimInfo(p);
		return 0;
	}
	
	public static int setOddsDropExperienceBottle(ServerPlayerEntity p, int newOdds) {
		PrimaryConfig.setOddsDropExperienceBottle(newOdds);
		reportOddsXpBottleDrop(p);
		return 1;
	}
	
	private static void updateGCFogToAllClients(ServerWorld level, double r , double g , double b) {
		List<ServerPlayerEntity> allPlayers = level.getServer().getPlayerManager().getPlayerList();
		Iterator<ServerPlayerEntity> apI = allPlayers.iterator();

		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeDouble(r);
		buf.writeDouble(g);
		buf.writeDouble(b);

		while (apI.hasNext()) { // sends to all players online.
			ServerPlayerEntity sp = apI.next();
			ServerPlayNetworking.send((ServerPlayerEntity) sp, SyncFogToClientsPacket.GAME_PACKET_SYNC_FOG_COLOR_S2C, buf);
		}
	}
	
	String subcommand = "";
	
	String value = "";

}
