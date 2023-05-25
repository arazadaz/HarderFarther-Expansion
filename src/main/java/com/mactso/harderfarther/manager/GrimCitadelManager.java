package com.mactso.harderfarther.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;


import com.mactso.harderfarther.block.GrimGateBlock;
import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.block.properties.GrimGateType;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.events.FogColorsEventHandler;
import com.mactso.harderfarther.item.ModItems;
import com.mactso.harderfarther.network.GrimClientSongPacket;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.network.SyncAllGCWithClientPacket;
import com.mactso.harderfarther.sounds.ModSounds;
import com.mactso.harderfarther.utility.Glooms;
import com.mactso.harderfarther.utility.Utility;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

public class GrimCitadelManager {
	private static long checkTimer = 0;
	private static long ambientSoundTimer = 0;
	private static long directionalSoundTimer = 0;

	private static int grimBonusDistSqr = 0;

	private static int currentCitadelIndex = -1;

	public static List<BlockPos> realGCList = new ArrayList<BlockPos>();

	private static List<Block> protectedBlocks = Arrays.asList(Blocks.NETHERRACK, Blocks.BLACKSTONE, Blocks.BASALT,
			Blocks.POLISHED_BASALT, Blocks.CRIMSON_PLANKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS,
			Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.BLACKSTONE, Blocks.GILDED_BLACKSTONE, Blocks.TINTED_GLASS,
			Blocks.CHEST, Blocks.ANCIENT_DEBRIS);

	private static List<Block> floorBlocks = Arrays.asList(Blocks.BASALT, Blocks.CRIMSON_PLANKS, Blocks.NETHERRACK);
	private static int FLOOR_BLOCKS_TOP = 0;
	private static int FLOOR_BLOCKS_MIDDLE = 1;
	private static int FLOOR_BLOCKS_BOTTOM = 2;

	private static List<Block> wallBlocks = Arrays.asList(Blocks.POLISHED_BASALT,
			Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, Blocks.NETHERRACK);
	private static int WALLTRIM = 2;

	private static BlockState GRIM_GATE_FLOOR = ModBlocks.GRIM_GATE.getDefaultState();
	private static BlockState GRIM_GATE_DOOR = ModBlocks.GRIM_GATE.getDefaultState().with(GrimGateBlock.TYPE,
			GrimGateType.DOOR);

	private static BlockState AIR = Blocks.AIR.getDefaultState();
	private static BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
	private static BlockState BASALT = Blocks.BASALT.getDefaultState();
	private static BlockState POLISHEDBASALT = Blocks.POLISHED_BASALT.getDefaultState();
	private static BlockState BLACKSTONE = Blocks.BLACKSTONE.getDefaultState();
	private static BlockState GILDED_BLACKSTONE = Blocks.GILDED_BLACKSTONE.getDefaultState();
	private static BlockState WINDOW = Blocks.TINTED_GLASS.getDefaultState();
	private static BlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
	private static BlockState FIRE = Blocks.FIRE.getDefaultState();
	private static BlockState BROWN_MUSHROOM = Blocks.BROWN_MUSHROOM.getDefaultState();
	private static File grimFile;
	private static UUID ITEM_SPEED_UUID = UUID.fromString("4ce59996-ed35-11ec-8ea0-0242ac120002");

	static List<SoundEvent> gcDirectionalSoundEvents = Arrays.asList(SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY,
			SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT, SoundEvents.BLOCK_LAVA_AMBIENT, SoundEvents.ENTITY_ZOMBIE_VILLAGER_STEP,
			SoundEvents.ENTITY_HOGLIN_ANGRY, SoundEvents.ENTITY_BLAZE_AMBIENT, SoundEvents.ENTITY_HOGLIN_AMBIENT,
			SoundEvents.AMBIENT_NETHER_WASTES_MOOD, SoundEvents.BLOCK_FIRE_AMBIENT, SoundEvents.ENTITY_WITHER_SKELETON_STEP);
	static List<SoundEvent> gcAmbientSoundEvents = Arrays.asList(SoundEvents.AMBIENT_CAVE, SoundEvents.ENTITY_WITCH_AMBIENT,
			SoundEvents.ENTITY_WOLF_HOWL, SoundEvents.AMBIENT_CAVE, SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD);

	// TODO: This is becoming obsolete replaced by difficulty.
	public static int gcDist100 = MyConfig.getGrimCitadelBonusDistanceSq(); // 100%
	public static int gcDist70 = gcDist100 / 2; // 70% .. and so on.
	public static int gcDist50 = gcDist100 / 4;
	public static int gcDist30 = gcDist100 / 8;
	public static int gcDist25 = gcDist100 / 16;
	public static int gcDist16 = gcDist100 / 32;
	public static int gcDist12 = gcDist100 / 64;
	public static int gcDist09 = gcDist100 / 128;
	public static int gcDist05 = gcDist100 / 256;

	private static void addAntiClimbingRing(ServerWorld level, BlockPos pos, int top) {
		RandomGenerator rand = level.getRandom();
		Mutable mutPos = new Mutable();
		mutPos.setY(pos.getY() + top);
		int bottomPosX = pos.getX();
		int bottomPosZ = pos.getZ();
		int wallRadius = getGrimRadius() + 2;
		for (int fx = -wallRadius; fx <= wallRadius; fx++) {
			for (int fz = -wallRadius; fz <= wallRadius; fz++) {
				if ((Math.abs(fx) == wallRadius) || (Math.abs(fz) == wallRadius)) {
					mutPos.setX(bottomPosX + fx);
					mutPos.setZ(bottomPosZ + fz);
					if (rand.nextFloat() < 0.67F) {

						level.setBlockState(mutPos, floorBlocks.get(FLOOR_BLOCKS_TOP).getDefaultState(), 0);
					} else {
						level.setBlockState(mutPos, floorBlocks.get(FLOOR_BLOCKS_MIDDLE).getDefaultState(), 0);
					}
				}
			}
		}

	}

	private static void addCorners(ServerWorld level, BlockPos bottomPos, int offset) {
		BlockState bs = POLISHEDBASALT;

		addOneCorner(level, bottomPos.north(getGrimRadius() + 2).west(getGrimRadius() + 2), offset, bs);
		addOneCorner(level, bottomPos.north(-getGrimRadius() - 1).west(getGrimRadius() + 2), offset, bs);
		addOneCorner(level, bottomPos.north(-getGrimRadius() - 1).west(-getGrimRadius() - 1), offset, bs);
		addOneCorner(level, bottomPos.north(getGrimRadius() + 2).west(-getGrimRadius() - 1), offset, bs);
	}

	private static void addOneCorner(ServerWorld level, BlockPos pos, int offset, BlockState bs) {
		Mutable mPos = new Mutable();

		int posX = pos.getX();
		int posY = pos.getY() + offset;
		int posZ = pos.getZ();

		for (int fx = 0; fx < 2; fx++) {
			for (int fz = 0; fz < 2; fz++) {
				for (int fy = 0; fy < 3; fy++) {
					mPos.setX(posX + fx);
					mPos.setY(posY + fy);
					mPos.setZ(posZ + fz);
					level.setBlockState(mPos, bs, 0);
				}
			}
		}

	}

	private static void addOptionalNewHearts(ServerWorld level, @Nullable BlockPos pos) {

		while (realGCList.size() < MyConfig.getGrimCitadelsCount()) {
			double randomRadian = level.getRandom().nextFloat() * (Math.PI * 2F);
			double xVec = Math.cos(randomRadian);
			double zVec = Math.sin(randomRadian);

			BlockPos ssPos = level.getSpawnPos();
			int distSq = getRandomGrimCitadelDistanceSq(level, ssPos);
			if (distSq == 0) {
				if (pos != null) {
					distSq = (int) ssPos.getSquaredDistance(pos);
				} else {
					distSq = MyConfig.getGrimCitadelBonusDistanceSq() * 2;
				}
			}
			int dist = (int) Math.sqrt(distSq);
			dist = dist + MyConfig.getGrimCitadelBonusDistance();
			BlockPos newHeartPos = new BlockPos(ssPos.getX() + (dist * xVec), -1, ssPos.getZ() + (dist * zVec));
			realGCList.add(newHeartPos);
			Utility.debugMsg(1, pos, "realGCList size:" + realGCList.size() + "Adding new HeartPos:" + newHeartPos);
		}
	}

	public static void buildAFloor(ServerWorld level, Mutable floorPos, int fy, int height,
			boolean roof) {

		int updateFlag = 131; // 3+128 = also update light.

		int posX = floorPos.getX();
		int posY = floorPos.getY();
		int posZ = floorPos.getZ();

		RandomGenerator rand = level.getRandom();
		
		for (int fx = -getGrimRadius(); fx <= getGrimRadius(); fx++) {
			for (int fz = -getGrimRadius(); fz <= getGrimRadius(); fz++) {
				floorPos.setX(posX + fx);
				floorPos.setZ(posZ + fz);
				int r = rand.nextInt(height);
				if (r > (fy / 4)) {
					level.setBlockState(floorPos, floorBlocks.get(FLOOR_BLOCKS_TOP).getDefaultState(), updateFlag);
				} else if (r > (2 * fy / 4)) {
					level.setBlockState(floorPos, floorBlocks.get(FLOOR_BLOCKS_MIDDLE).getDefaultState(), updateFlag);
				} else {
					level.setBlockState(floorPos, floorBlocks.get(FLOOR_BLOCKS_BOTTOM).getDefaultState(), updateFlag);
				}
			}
		}

		int fx = getValidRandomFloorOffset(rand);
		int fz = getValidRandomFloorOffset(rand);

		floorPos.setX(posX + fx);
		floorPos.setY(posY);
		floorPos.setZ(posZ + fz);
		
		if ((fy > 0) && (rand.nextInt(10) < 2)) {
			level.setBlockState(floorPos, CAVE_AIR, 3);
		} else {
			level.setBlockState(floorPos, GRIM_GATE_FLOOR, 3);
		}
		level.setBlockState(floorPos.up(), CAVE_AIR, 3);
		
		if ((getGrimRadius() > 5) && (fy > 0) && !roof) {
			for (int i = 0; i < getGrimRadius() / 2; i++) {
				int sx = getValidRandomFloorOffset(rand);
				int sz = getValidRandomFloorOffset(rand);
				if ((sx != fx) && (sz != fz)) {
					if (sz == 0) sz++;  // don't smother mobs at spawnin point.
					floorPos.setX(posX + sx);
					floorPos.setZ(posZ + sz);
					buildFloorFeature(level, rand, floorPos, updateFlag, posY);
				}
			}
		}

		floorPos.setX(posX);
		floorPos.setY(posY);
		floorPos.setZ(posZ);

		populateFloor(level, floorPos, fy);

	}

	private static void buildFloorFeature(ServerWorld level, RandomGenerator rand, Mutable floorPos,
			int updateFlag, int posY) {
		int r = rand.nextInt(100);
		if (r < 30) {
			floorPos.setY(posY + 1);
			level.setBlockState(floorPos, Blocks.NETHER_BRICKS.getDefaultState(), updateFlag);
			floorPos.setY(posY + 2);
			level.setBlockState(floorPos, Blocks.BLACK_STAINED_GLASS.getDefaultState(), updateFlag);
			floorPos.setY(posY + 3);
			level.setBlockState(floorPos, Blocks.NETHER_BRICKS.getDefaultState(), updateFlag);
		} else if (r < 50) {
			floorPos.setY(posY + 1);
			level.setBlockState(floorPos, Blocks.NETHER_BRICK_WALL.getDefaultState(), updateFlag);
			floorPos.setY(posY + 2);
			level.setBlockState(floorPos, Blocks.NETHER_BRICK_FENCE.getDefaultState(), updateFlag);
			floorPos.setY(posY + 3);
			level.setBlockState(floorPos, Blocks.NETHER_BRICK_WALL.getDefaultState(), updateFlag);
		} else if (r < 70) {
			floorPos.setY(posY + 1);
			level.setBlockState(floorPos, Blocks.NETHER_BRICKS.getDefaultState(), updateFlag);
			floorPos.setY(posY + 2);
			level.setBlockState(floorPos, Blocks.RED_STAINED_GLASS.getDefaultState(), updateFlag);
			floorPos.setY(posY + 3);
			level.setBlockState(floorPos, Blocks.NETHER_BRICKS.getDefaultState(), updateFlag);
		} else if (r < 90) {
			floorPos.setY(posY + 1);
			level.setBlockState(floorPos, Blocks.NETHER_BRICKS.getDefaultState(), updateFlag);
			floorPos.setY(posY + 2);
			level.setBlockState(floorPos, Blocks.MAGMA_BLOCK.getDefaultState(), updateFlag);
			floorPos.setY(posY + 3);
			level.setBlockState(floorPos, Blocks.BLACK_STAINED_GLASS.getDefaultState(), updateFlag);
		} else {
			floorPos.setY(posY + 1);
			level.setBlockState(floorPos, Blocks.NETHER_BRICK_WALL.getDefaultState(), updateFlag);
			floorPos.setY(posY + 2);
			level.setBlockState(floorPos, Blocks.SHROOMLIGHT.getDefaultState(), updateFlag);
			floorPos.setY(posY + 3);
			level.setBlockState(floorPos, Blocks.NETHER_BRICK_WALL.getDefaultState(), updateFlag);
		}

	}

	private static void buildBalcony(ServerWorld level, BlockPos tempPos) {
		Mutable mPos = new Mutable();
		int posX = tempPos.getX();
		int posY = tempPos.getY();
		int posZ = tempPos.getZ();

		mPos.setX(posX);
		mPos.setY(posY);
		mPos.setZ(posZ);

		level.setBlockState(mPos, POLISHEDBASALT, 0);
		mPos.setY(posY + 1);
		level.setBlockState(mPos, NETHERRACK, 0);
		mPos.setY(posY + 2);
		level.setBlockState(mPos, FIRE, 131); // update light also 3+128

	}

	private static void buildCitadelFloors(ServerWorld level, int bottom, int top, 
			BlockPos bottomPos) {
		Mutable floorPos = new Mutable();
		floorPos.set(bottomPos);
		boolean roof = true;

		// go from top to bottom.
		for (int fy = (top + 2 - bottom); fy >= 0; fy--) {
			if (fy < bottom + 23) {
				floorPos.setY(bottomPos.getY() + fy);
				clearAFloor(level, floorPos, fy, top - bottom, roof);
			}
			if (isGrimCitadelFloorHeight(fy)) {
				floorPos.setX(bottomPos.getX());
				floorPos.setY(bottomPos.getY() + fy );
				floorPos.setZ(bottomPos.getZ());
				buildAFloor(level, floorPos, fy, top - bottom, roof);

				if (roof)
					roof = false;

				if ((fy > 8) && (level.getRandom().nextInt(100) > 20)) {
					buildFloorBalcony(level, bottomPos, fy);
				}
			}
			
			buildOutsideWall(level, bottomPos, fy, top - bottom);
			buildCore(level, bottomPos, fy);

		}
	}

	private static void buildCitadelFoundation(ServerWorld sl, BlockPos pos) {

		RandomGenerator rand = sl.getRandom();
		Mutable mPos = new Mutable();
		mPos.setY(pos.getY());
		int posX = pos.getX();
		int posY = pos.getY();
		int posZ = pos.getZ();

		for (int fx = -getGrimRadius() - 3; fx < getGrimRadius() + 3 + 1; fx++) {
			mPos.setX(fx + posX);
			for (int fz = -getGrimRadius() - 3; fz < getGrimRadius() + 3 + 1; fz++) {
				mPos.setZ(fz + posZ);

				int groundws = sl.getTopY(Heightmap.Type.WORLD_SURFACE, mPos.getX(), mPos.getZ());
				int ground1 = sl.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mPos.getX(), mPos.getZ());
				if (groundws > ground1)
					groundws = ground1;

				int groundo = sl.getTopY(Heightmap.Type.OCEAN_FLOOR, mPos.getX(), mPos.getZ());
				if (groundws > groundo)
					groundws = groundo;

				for (int fy = posY - 1; fy >= groundws; fy--) {
					mPos.setY(fy);
					if (rand.nextInt(360) == 42) {
						if ((Math.abs(fx) <= getGrimRadius()) && Math.abs(fz) <= getGrimRadius()) {
							if (rand.nextBoolean()) {
								sl.setBlockState(mPos, Blocks.NETHER_GOLD_ORE.getDefaultState(), 3);
							} else {
								sl.setBlockState(mPos, Blocks.LAVA.getDefaultState(), 3);
							}
						}
					} else {
						sl.setBlockState(mPos, BASALT, 3);
					}
				}
			}
		}

	}

	private static void buildCore(ServerWorld level, BlockPos pos, int fy) {

		BlockState bs = GILDED_BLACKSTONE;
		float roll = level.getRandom().nextFloat();

		if (roll > 0.95f) {
			bs = POLISHEDBASALT;
		} else if (roll > 0.85f) {
			bs = BLACKSTONE;
		}
		int ew = (level.getRandom().nextInt(getGrimRadius() / 2)) - 1;
		int ns = (level.getRandom().nextInt(getGrimRadius() / 2)) - 1;
		level.setBlockState(pos.east(ew).north(ns).up(fy), bs, 0);
		if (level.getRandom().nextFloat() > 0.66) {
			level.setBlockState(pos.east(ew).north(ns).up(fy + 1), NETHERRACK, 0);
			if (level.getRandom().nextFloat() > 0.33f) {
				level.setBlockState(pos.east(ew).north(ns).up(fy + 2), BROWN_MUSHROOM, 3);
			}
		}

	}

	private static void buildFloorBalcony(ServerWorld level, BlockPos bottomPos, int fy) {

		BlockPos tempPos;
		RandomGenerator rand = level.getRandom();
		int side = rand.nextInt(4);
		int balconyRadius = getGrimRadius() + 2;
		switch (side) {
		default:
		case 0:
			tempPos = bottomPos.north(balconyRadius).up(fy).east(rand.nextInt(5) - 2);
			break;
		case 1:
			tempPos = bottomPos.south(balconyRadius).up(fy).east(rand.nextInt(5) - 2);
			break;
		case 2:
			tempPos = bottomPos.east(balconyRadius).up(fy).north(rand.nextInt(5) - 2);
			break;
		case 3:
			tempPos = bottomPos.west(balconyRadius).up(fy).north(rand.nextInt(5) - 2);
			break;
		}
		buildBalcony(level, tempPos);

	}

	private static void buildHeartLevelWindows(ServerWorld level, Mutable mPos, boolean eastwest,
			boolean northsouth) {
		level.setBlockState(mPos, WINDOW, 0);
		level.setBlockState(mPos.up(), WINDOW, 3);

		if (eastwest) {
			level.setBlockState(mPos.east(), WINDOW, 0);
			level.setBlockState(mPos.east().up(), WINDOW, 3);
			level.setBlockState(mPos.west(), WINDOW, 0);
			level.setBlockState(mPos.west().up(), WINDOW, 3);

		} else if (northsouth) {
			level.setBlockState(mPos.north(), WINDOW, 0);
			level.setBlockState(mPos.north().up(), WINDOW, 3);
			level.setBlockState(mPos.south(), WINDOW, 0);
			level.setBlockState(mPos.south().up(), WINDOW, 3);
		}

	}

	private static void buildOutsideWall(ServerWorld level, BlockPos bottomPos, int fy, int height) {
		RandomGenerator rand = level.getRandom();
		BlockState bs1;
		BlockState bs2;
		float percent;
		if (fy < height / 2) {
			percent = (float) fy / (float) (height / 2);
			bs1 = wallBlocks.get(0).getDefaultState();
			bs2 = wallBlocks.get(1).getDefaultState();
		} else {
			percent = (float) (fy - (height / 2)) / (float) (height / 2);
			bs1 = wallBlocks.get(1).getDefaultState();
			bs2 = wallBlocks.get(2).getDefaultState();
		}

		Mutable mPos = new Mutable();
		mPos.setY(bottomPos.getY() + fy);
		int posX = bottomPos.getX();
		int posZ = bottomPos.getZ();
		int wallRadius = getGrimRadius() + 1;
		for (int fx = -wallRadius; fx <= wallRadius; fx++) {
			for (int fz = -wallRadius; fz <= wallRadius; fz++) {
				boolean corner = false;
				boolean centered = false;
				boolean eastwest = false;
				boolean northsouth = false;
				if ((fx == 0)) {
					centered = true; // TODO need to clean this area up.
					eastwest = true;
				}
				if ((fz == 0)) {
					centered = true;
					northsouth = true;
				}
				if ((Math.abs(fx) == wallRadius) && (Math.abs(fz) == wallRadius)) {
					corner = true;
				}
				if ((Math.abs(fx) == wallRadius) || (Math.abs(fz) == wallRadius)) {
					mPos.setX(posX + fx);
					mPos.setZ(posZ + fz);
					if (corner) {
						level.setBlockState(mPos, wallBlocks.get(WALLTRIM).getDefaultState(), 3);
					} else if ((((fy == 31) && (centered)) || (rand.nextInt(15) < 1)) && ((fy + 1) % 4 == 0)) {
						buildHeartLevelWindows(level, mPos, eastwest, northsouth);
					} else if (rand.nextFloat() < percent) {
						level.setBlockState(mPos, bs2, 0);
					} else {
						level.setBlockState(mPos, bs1, 0);
					}
				}
			}
		}
	}

	private static void buildRoofBalconies(ServerWorld level, BlockPos roofPos) {
		int balconyRadius = getGrimRadius() + 2;
		buildBalcony(level, roofPos.north(balconyRadius));
		buildBalcony(level, roofPos.south(balconyRadius));
		buildBalcony(level, roofPos.east(balconyRadius));
		buildBalcony(level, roofPos.west(balconyRadius));
	}

	private static BlockPos calcGCCluePosition(BlockPos pos) {

		Vec3d v = calcGCDirection(pos);
		if (v != null) {
			v = v.multiply(12);
			return new BlockPos(pos.getX() + v.x, pos.getY() + v.y, pos.getZ() + v.z);
		}

		return pos;
	}

	public static Vec3d calcGCDirection(BlockPos pos) {
		long closestSq = Long.MAX_VALUE;
		BlockPos cPos = pos;
		for (BlockPos b : realGCList) {
			if (closestSq > b.getSquaredDistance(pos)) {
				closestSq = (int) b.getSquaredDistance(pos);
				cPos = b;
			}
		}
		if (closestSq != Long.MAX_VALUE) {
			Vec3d v = new Vec3d(cPos.getX() - pos.getX(), cPos.getY() - pos.getY(), cPos.getZ() - pos.getZ());
			return v.normalize();
		}
		return null;
	}

	private static float calcGCDistanceVolume(float difficulty) {
		return (0.22f * difficulty + 0.03f);
	}

	public static void checkCleanUpCitadels(ServerWorld level) {

		if (level.getRegistryKey() != World.OVERWORLD) {
			return;
		}

		if (level.isClient)
			return;

		if (!MyConfig.isUseGrimCitadels())
			return;

		long gameTime = level.getTime();
		if (checkTimer == 0) { // delay creating grim citadels for 1 minute when game started.
			checkTimer = gameTime + 900;
		}

		if (checkTimer > gameTime)
			return;

		long ntt = level.getServer().getTimeReference();
		long i = Util.getMeasuringTimeMs() - ntt;
		if (i > 250L) {
			Utility.debugMsg(1, "Server Slow - Skipped Checking Citadels");
			checkTimer += 15;
			return;
		}

		addOptionalNewHearts(level, null);

		// Iterator<BlockPos> i = realGCList.iterator();

		if (realGCList.isEmpty()) {
			return;
		}

		// Check one possible grim citadel

		currentCitadelIndex++;
		if (currentCitadelIndex >= realGCList.size()) {
			currentCitadelIndex = 0;
		}

		BlockPos pos = realGCList.get(currentCitadelIndex);
		Utility.debugMsg(1, pos, "Does Grim Citadel #" + currentCitadelIndex + " exist?");

		Chunk chunk = level.getChunk(pos);
		checkTimer = gameTime + 600;

		Set<BlockPos> ePosSet = chunk.getBlockEntityPositions();
		boolean foundHeart = false;
		for (BlockPos ePos : ePosSet) {
			if ((ePos.getX() == pos.getX()) && ePos.getZ() == pos.getZ()) {
				if (level.getBlockState(ePos).getBlock() == ModBlocks.GRIM_HEART) {
					foundHeart = true;
					Utility.debugMsg(1, pos, "Aye, Grim Citadel #" + currentCitadelIndex + " still exists.");
					break;
				}
			}
		}
		if (chunk.getInhabitedTime() < 600) { // heart not created yet
			if (!foundHeart) {
				Utility.debugMsg(1, pos, "Creating New Grim Citadel.");
				int bottom = getCitadelBottom(level, pos);
				makeGrimCitadel(level, bottom, pos);
				BlockPos heartPos = new BlockPos(pos.getX(), bottom + 31, pos.getZ());
				level.setBlockState(heartPos, ModBlocks.GRIM_HEART.getDefaultState(), 3);
				realGCList.set(currentCitadelIndex, heartPos);
				save();
				updateGCLocationsToClients(level);
			}
		} else { // heart destroyed / gone / taken.
			if (!foundHeart) {
				realGCList.remove(currentCitadelIndex);
				addOptionalNewHearts(level, pos);
				save();
				updateGCLocationsToClients(level);
				currentCitadelIndex -= 1;
			}
		}

	}

	public static void clear() {
		grimFile = null;
		realGCList.clear();
		checkTimer = 0;
		Glooms.doResetTimers();
	}

	public static void clearAFloor(ServerWorld level, Mutable airPos, int fy, int height,
			boolean first) {
		int posX = airPos.getX();
		int posZ = airPos.getZ();
		int grimRadius = getGrimRadius();
		// bigger to destroy obstacles around tower.
		for (int fx = -grimRadius - 3; fx <= grimRadius + 3; fx++) {
			for (int fz = -grimRadius - 3; fz <= grimRadius + 3; fz++) {
				airPos.setX(posX + fx);
				airPos.setZ(posZ + fz);
				level.setBlockState(airPos, Blocks.CAVE_AIR.getDefaultState(), 0);
			}
		}
		airPos.setX(posX);
		airPos.setZ(posZ);
	}

	private static void createHeartLoot(ServerWorld level, BlockPos pos) {
		level.setBlockState(pos.down(), Blocks.GRAY_SHULKER_BOX.getDefaultState());
		LootableContainerBlockEntity.setLootTable(level, level.random, pos.down(),
				LootTables.NETHER_BRIDGE_CHEST);
		ShulkerBoxBlockEntity sBox = (ShulkerBoxBlockEntity) level.getBlockEntity(pos.down());

		ItemStack itemStackToDrop;

		itemStackToDrop = new ItemStack(ModItems.LIFE_HEART);
		Utility.setLore(itemStackToDrop,
				Text.Serializer.toJson(Text.translatable("item.harderfarther.life_heart.lore")));
		sBox.setStack(getEmptyContainerSlot(level.getRandom(), sBox), itemStackToDrop);

		itemStackToDrop = new ItemStack(ModItems.BURNISHING_STONE, level.getRandom().nextInt(4) + 2);
		Utility.setLore(itemStackToDrop,
				Text.Serializer.toJson(Text.translatable("item.harderfarther.burnishing_stone.lore")));
		sBox.setStack(getEmptyContainerSlot(level.getRandom(), sBox), itemStackToDrop);

		double bootsSpeed = 0.04D + level.getRandom().nextDouble() * 0.06D;
		EntityAttributeModifier am = new EntityAttributeModifier(ITEM_SPEED_UUID, "hfspeed", bootsSpeed,
				EntityAttributeModifier.Operation.ADDITION);
		itemStackToDrop = new ItemStack(Items.DIAMOND_BOOTS);
		itemStackToDrop.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, am, EquipmentSlot.FEET);
		itemStackToDrop.setCustomName(Text.translatable("item.harderfarther.grim_boots.name"));
		Utility.setLore(itemStackToDrop,
				Text.Serializer.toJson(Text.translatable("item.harderfarther.grim_boots.lore")));
		sBox.setStack(getEmptyContainerSlot(level.getRandom(), sBox), itemStackToDrop);

	}

	private static int getEmptyContainerSlot(RandomGenerator rand, LootableContainerBlockEntity r) {
		int slot = 0;
		int counter = 10;
		slot = rand.nextInt(r.size());
		while ((counter >= 0) && (!r.getStack(slot).isEmpty())) {
			slot = rand.nextInt(r.size());
			counter--;
		}
		return slot;
	}

	private static void decorateCitadel(ServerWorld level, BlockPos bottomPos, int top, int bottom) {

		doBuildDoor(level, bottom, top, level.getRandom(), bottomPos.up());
		addCorners(level, bottomPos, -1);
		decorateRoof(level, bottom, bottomPos, top);

	}

	private static void decorateRoof(ServerWorld level, int bottom, BlockPos bottomPos, int top) {

		addCorners(level, bottomPos, top - bottom + 2);
		addAntiClimbingRing(level, bottomPos, top - bottom + 1);
		buildRoofBalconies(level, bottomPos.up(top - bottom + 2));
		level.setBlockState(bottomPos.up(top - bottom + 1), Blocks.ANCIENT_DEBRIS.getDefaultState(), 3);
		level.setBlockState(bottomPos.up(top - bottom + 2), Blocks.LAVA.getDefaultState(), 131);
		level.setBlockState(bottomPos.up(top - bottom + 3), Blocks.GLOWSTONE.getDefaultState(), 131);
		level.setBlockState(bottomPos.up(top - bottom + 4), Blocks.LAVA.getDefaultState(), 131);

	}

	public static void doBrokenGrimGate(ServerPlayerEntity sp, ServerWorld serverLevel, BlockPos pos, BlockState bs) {
		if (bs.get(GrimGateBlock.TYPE) == GrimGateType.FLOOR) {
			GrimCitadelManager.makeSeveralHolesInFloor(serverLevel, pos);
		} else if (bs.get(GrimGateBlock.TYPE) == GrimGateType.DOOR) {
			Network.sendToClient(new GrimClientSongPacket(ModSounds.NUM_LABYRINTH_LOST_DREAMS), sp);
		}
	}

	private static void doBuildDoor(ServerWorld level, int bottom, int top, RandomGenerator randomSource,
			BlockPos bottomPos) {
		int side = level.getRandom().nextInt(4);
		int grimRadius = getGrimRadius();
		if (side == 0) {
			doBuildDoorColumn(level, bottomPos.south(grimRadius + 1).east(), GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.south(grimRadius + 1), GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.south(grimRadius + 1).west(), GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.south(grimRadius + 2).east(), AIR);
			doBuildDoorColumn(level, bottomPos.south(grimRadius + 2), AIR);
			doBuildDoorColumn(level, bottomPos.south(grimRadius + 2).west(), AIR);

		} else if (side == 1) {
			doBuildDoorColumn(level, bottomPos.north(grimRadius + 1).east(), GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.north(grimRadius + 1), GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.north(grimRadius + 1).west(), GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.north(grimRadius + 2).east(), AIR);
			doBuildDoorColumn(level, bottomPos.north(grimRadius + 2), AIR);
			doBuildDoorColumn(level, bottomPos.north(grimRadius + 2).west(), AIR);
		} else if (side == 2) {
			doBuildDoorColumn(level, bottomPos.east(grimRadius + 1).north(), GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.east(grimRadius + 1), GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.east(grimRadius + 1).south(), GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.east(grimRadius + 2).north(), AIR);
			doBuildDoorColumn(level, bottomPos.east(grimRadius + 2), AIR);
			doBuildDoorColumn(level, bottomPos.east(grimRadius + 2).south(), AIR);
		} else if (side == 3) {
			doBuildDoorColumn(level, bottomPos.west(grimRadius + 1).north(), GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.west(grimRadius + 1), GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.west(grimRadius + 1).south(), GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.west(grimRadius + 2).north(), AIR);
			doBuildDoorColumn(level, bottomPos.west(grimRadius + 2), AIR);
			doBuildDoorColumn(level, bottomPos.west(grimRadius + 2).south(), AIR);
		}
	}

	private static void doBuildDoorColumn(ServerWorld level, BlockPos doorColPos, BlockState blockState) {
		level.setBlockState(doorColPos, blockState, 0);
		level.setBlockState(doorColPos.up(1), blockState, 0);
		level.setBlockState(doorColPos.up(2), blockState, 3);
	}

	private static int getCitadelBottom(ServerWorld level, BlockPos pos) {
		int bottom = level.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
		if (bottom > level.getTopY() - 63)
			bottom = level.getTopY() - 63;
		if (bottom < level.getBottomY())
			bottom = level.getBottomY();
		return bottom;
	}

	public static String getCitadelListAsString() {
		return realGCList.toString();
	}

	private static int getCitadelTop(ServerWorld level, BlockPos pos) {
		return getCitadelBottom(level, pos) + 62;
	}

	public static int getClosestGrimCitadelDistanceSq(BlockPos pos) {
		int closestSq = Integer.MAX_VALUE;

		for (BlockPos b : realGCList) {
			closestSq = Math.min((int) b.getSquaredDistance(pos), closestSq);
		}

		return closestSq;
	}

	public static BlockPos getClosestGrimCitadelPos(BlockPos pos) {
		BlockPos grimPos = pos;
		int closestSqr = Integer.MAX_VALUE;
		for (BlockPos b : realGCList) {
			int nextSqr = (int) b.getSquaredDistance(pos);
			if (closestSqr > nextSqr) {
				closestSqr = nextSqr;
				grimPos = b;
			}
		}
		if (grimPos == pos) {
			return null;
		}
		return grimPos;
	}

	public static int getFarthestGrimCitadelDistanceSq(BlockPos pos) {
		int farthestSq = 0;

		for (BlockPos b : realGCList) {
			farthestSq = Math.max((int) b.getSquaredDistance(pos), farthestSq);
		}

		return farthestSq;
	}

	public static List<Block> getFloorBlocks() {
		return floorBlocks;
	}

	public static float getGrimDifficulty(LivingEntity le) {

		if (!MyConfig.isUseGrimCitadels())
			return 0;

		float grimDifficulty = 0;

		double closestGrimDistSq = Math.sqrt(GrimCitadelManager.getClosestGrimCitadelDistanceSq(Utility.getBlockPosition(le)));
		double bonusGrimDistSq = Math.sqrt(MyConfig.getGrimCitadelBonusDistanceSq());
		if (closestGrimDistSq > bonusGrimDistSq)
			return 0;

		grimDifficulty = (float) (1.0 - ((float) closestGrimDistSq / bonusGrimDistSq));

		if (grimDifficulty > MyConfig.getGrimCitadelMaxBoostPercent()) {
			grimDifficulty = MyConfig.getGrimCitadelMaxBoostPercent();
		}

		return grimDifficulty;

	}

	public static int getGrimRadius() {
		return MyConfig.getGrimCitadelsRadius();
	}

	public static List<Block> getProtectedBlocks() {
		return protectedBlocks;
	}

	public static int getRandomGrimCitadelDistanceSq(ServerWorld level, BlockPos pos) {
		int r = level.getRandom().nextInt(realGCList.size());
		int distSq = (int) pos.getSquaredDistance(realGCList.get(r));
		return distSq;
	}

	public static int getValidRandomFloorOffset(RandomGenerator rand) {
		int r = rand.nextInt(getGrimRadius() - 2) + 1;
		if (rand.nextBoolean()) {
			return r;
		}
		return -r;
	}

	public static boolean isGCFar(float difficulty) {
		return difficulty < Utility.Pct09;
	}

	public static boolean isGCNear(float difficulty) {
		return difficulty > Utility.Pct95;
	}

	public static boolean isGrimCitadelFloorHeight(int fy) {
		return fy % 4 == 0;
	}

	public static boolean isInGrimProtectedArea(BlockPos eventPos) {
		if (grimBonusDistSqr == 0)
			grimBonusDistSqr = MyConfig.getGrimCitadelBonusDistanceSq();

		if (getClosestGrimCitadelDistanceSq(eventPos) > grimBonusDistSqr)
			return false;
		BlockPos grimPos = getClosestGrimCitadelPos(eventPos);

		if (grimPos != null) {
			int protectedDistance = GrimCitadelManager.getGrimRadius() + 21;
			int xAbs = Math.abs(eventPos.getX() - grimPos.getX());
			int zAbs = Math.abs(eventPos.getZ() - grimPos.getZ());
			int yOffset = eventPos.getY() - grimPos.getY();
			if (yOffset < protectedDistance) {
				yOffset = protectedDistance;
			}
			// check grim tower protected airspace
			if ((xAbs <= protectedDistance) && (zAbs <= protectedDistance) && (eventPos.getY() > grimPos.getY() + -8)) {
				if ((xAbs > getGrimRadius() + 2) || (zAbs > getGrimRadius() + 2)) {
					return true; // TODO retest protected space outside on walls.
				}
			}
		}

		return false;
	}

	public static boolean isInsideGrimCitadelRadius(BlockPos pos) {
		if (grimBonusDistSqr == 0)
			grimBonusDistSqr = MyConfig.getGrimCitadelBonusDistanceSq();

		if (getClosestGrimCitadelDistanceSq(pos) > grimBonusDistSqr)
			return false;
		
		BlockPos grimPos = getClosestGrimCitadelPos(pos);

		if (grimPos != null) {
			int protectedDistance = GrimCitadelManager.getGrimRadius() + 1 ;
			int xAbs = Math.abs(pos.getX() - grimPos.getX());
			int zAbs = Math.abs(pos.getZ() - grimPos.getZ());
			if ((xAbs <= protectedDistance) && (zAbs <= protectedDistance)) {
					return true; // TODO retest protected space outside on walls.
			}
		}

		return false;
	}
	
	
	public static boolean isInRangeOfGC(BlockPos pos) {

		double closestGrimDistSq = getClosestGrimCitadelDistanceSq(pos);
		if ((closestGrimDistSq > gcDist100)) { // note also MAXINTEGER in here.
			return false;
		}
		return true;
	}

	private static boolean isPlayAmbientSound(PlayerEntity cp) {
		if (ambientSoundTimer > cp.world.getTime())
			return false;
		int reqRoll = 13;
		if (cp.world.isNight())
			reqRoll += 17;
		if (cp.world.getRandom().nextInt(1200) <= reqRoll)
			return true;

		return false;

	}

	private static boolean isPlayDirectionalSound(PlayerEntity cp, float difficulty) {
		if (directionalSoundTimer > cp.world.getTime()) {
			return false;
		}
		if (isGCNear(difficulty)) // close to tower
			return false;
		if (isGCFar(difficulty)) // far from tower
			return false;
		int reqRoll;
		reqRoll = 31;
		if (cp.world.isNight())
			reqRoll += 23;
		if (cp.world.getRandom().nextInt(1200) <= reqRoll)
			return true;

		return false;

	}

	// File Section: Read and write grimcitadel data.
	public static void load(MinecraftServer server) {

//	       File file = new File(
//	               "C:\\Users\\pankaj\\Desktop\\test.txt");
//	    
//	           // Note:  Double backquote is to avoid compiler
//	           // interpret words
//	           // like \test as \t (ie. as a escape sequence)
//	    
//	           // Creating an object of BufferedReader class

		File file1 = server.getSavePath(WorldSavePath.ROOT).toFile();
		grimFile = new File(file1, "data/grimcitadels.dat");
		if (grimFile.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(grimFile));
				readData(br);
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Iterator<BlockPos> iter = MyConfig.getGrimCitadelsBlockPosList().iterator();
			while (iter.hasNext()) {
				BlockPos pos = iter.next();
				realGCList.add(pos);
			}
		}
	}

	private static void makeGrimCitadel(ServerWorld level, int bottom, BlockPos pos) {
		Utility.debugMsg(1, pos, "Creating a GrimCitadel at : " + pos);

		BlockPos bottomPos = new BlockPos(pos.getX(), bottom, pos.getZ());
		int top = getCitadelTop(level, pos);

		buildCitadelFoundation(level, bottomPos);

		buildCitadelFloors(level, bottom, top, bottomPos);

		decorateCitadel(level, bottomPos, top, bottom);
	}

	// note: this may try air outside tower.
	public static int makeOneHoleInGrimCitadelFloor(World level, BlockPos pos, int i, RandomGenerator rand) {
		int x = rand.nextInt(getGrimRadius() * 4 + 1) - getGrimRadius() * 2;
		int z = rand.nextInt(getGrimRadius() * 4 + 1) - getGrimRadius() * 2;
		BlockPos fPos = new BlockPos(pos.getX() + x, pos.getY(), pos.getZ() + z);
		Block b = level.getBlockState(fPos).getBlock();
		if (GrimCitadelManager.getFloorBlocks().contains(level.getBlockState(fPos).getBlock())) {
			level.setBlockState(fPos, Blocks.CAVE_AIR.getDefaultState(), 3);
			level.setBlockState(fPos.down(1), Blocks.CAVE_AIR.getDefaultState(), 3);
			level.setBlockState(fPos.down(2), Blocks.CAVE_AIR.getDefaultState(), 3);
			i += 1;
		}
		return i;
	}

	// Note: This doesn't know where grim heart is. It's random around a position.
	// But it only changes grim citadel floor blocks.
	public static void makeSeveralHolesInFloor(World level, BlockPos pos) {
		int i = 0;
		int passes = 0;
		RandomGenerator rand = level.getRandom();
		while (i < GrimCitadelManager.getGrimRadius() && (passes < getGrimRadius() * 2 + 1)) {
			i = makeOneHoleInGrimCitadelFloor(level, pos, i, rand);
			passes++; // ensure exit if it doesn't find floor blocks..
		}
	}

	private static void playGCAmbientSound(PlayerEntity cp, float pitch, float volume, long gameTime) {
		if (ambientSoundTimer < gameTime) {
			int modifier = 300;
			if (cp.world.isDay()) {
				modifier = 600;
			}
			ambientSoundTimer = gameTime + modifier + cp.world.getRandom().nextInt(1200);
			int i = cp.world.getRandom().nextInt(gcAmbientSoundEvents.size());
			cp.world.playSound(cp, Utility.getBlockPosition(cp), gcAmbientSoundEvents.get(i), SoundCategory.AMBIENT, volume, pitch);
		}
	}

	private static void playGCClueSound(PlayerEntity cp, float pitch, float volume, long gameTime) {
		if (directionalSoundTimer < gameTime) {

			int modifier = 200;
			if (cp.world.isDay()) {
				modifier = 400;
			}
			directionalSoundTimer = gameTime + modifier + cp.world.getRandom().nextInt(600);
			int i = cp.world.getRandom().nextInt(gcDirectionalSoundEvents.size());
			BlockPos cluePos = calcGCCluePosition(Utility.getBlockPosition(cp));
			cp.world.playSound(cp, cluePos, gcDirectionalSoundEvents.get(i), SoundCategory.AMBIENT, volume, pitch);
			RandomGenerator rand = cp.world.getRandom();
			for (int k = 1; k < 15; k++) {
				BlockPos temp = cluePos.east((rand.nextInt(3) - 1)).up((k / 2 + rand.nextInt(3) - 1))
						.north((rand.nextInt(3) - 1));
				double x = (double) temp.getX() + (double) rand.nextDouble() - 0.33d;
				double y = (double) temp.getY() + rand.nextDouble() - 0.33d;
				double z = (double) temp.getZ() + rand.nextDouble() - 0.33d;
				if (rand.nextInt(2) == 0) {
					cp.world.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, x, y, z, 0.0d, 0.5d, 0.0d);
				} else {
					cp.world.addParticle(ParticleTypes.DRIPPING_LAVA, x, y, z, 0.0d, 0.5d, 0.0d);
				}
			}
		}
	}

	// ClientSide
	public static void playGCOptionalSoundCues(PlayerEntity cp) {

		if (!MyConfig.isUseGrimCitadels())
			return;

		float difficulty = FogColorsEventHandler.getServerGrimDifficulty();
		if (difficulty == 0)
			return;

		boolean playAmbientsound = isPlayAmbientSound(cp);
		boolean playClueSound = isPlayDirectionalSound(cp, difficulty);
		if (!playAmbientsound && !playClueSound)
			return;
		float pitch = 0.67f;
		float volume = calcGCDistanceVolume(difficulty);
		long gameTime = cp.world.getTime();

		if (playAmbientsound) {
			playGCAmbientSound(cp, pitch, volume, gameTime);
		}
		if (playClueSound) {
			playGCClueSound(cp, pitch, 1 + volume, gameTime);
		}
	}

	private static void populateFloor(ServerWorld level, BlockPos pos, int fy) {
		BlockPos savePos = pos.up(+1);
		int fx = getValidRandomFloorOffset(level.random);
		int fz = getValidRandomFloorOffset(level.random);
		boolean livingfloor = false;

		if ((fy % 12 == 0)&&(fy<60)) {
			if (level.getBlockState(savePos.south(fx).east(fz).down()).getBlock() != ModBlocks.GRIM_GATE) {
				level.setBlockState(savePos.south(fx).east(fz), Blocks.CHEST.getDefaultState(), 3);
				LootableContainerBlockEntity.setLootTable(level, level.random, savePos.south(fx).east(fz),
						LootTables.NETHER_BRIDGE_CHEST);
				ChestBlockEntity cBox = (ChestBlockEntity) level.getBlockEntity(savePos.south(fx).east(fz));

				ItemStack itemStackToDrop = new ItemStack(ModItems.BURNISHING_STONE,
						(int) level.getRandom().nextInt(3) + 1);
				Utility.setLore(itemStackToDrop, Text.Serializer
						.toJson(Text.translatable("item.harderfarther.burnishing_stone.lore")));
				cBox.setStack(getEmptyContainerSlot(level.getRandom(), cBox), itemStackToDrop);
			}
			livingfloor = true;
		}

		if (!livingfloor) {
			populateUndeadFloor(level, savePos);
		} else {
			populateLivingFloor(level, savePos);
		}

	}

	private static void populateLivingFloor(ServerWorld level, BlockPos savePos) {
		boolean isPersistant = true;
		boolean isBaby = true;
		Utility.populateEntityType(EntityType.PIGLIN_BRUTE, level, savePos, 3, 0, isPersistant, isBaby);
		Utility.populateEntityType(EntityType.HOGLIN, level, savePos, 2, 0, isPersistant, isBaby);
		Utility.populateEntityType(EntityType.BLAZE, level, savePos, 3, -1, isPersistant, isBaby);
	}

	private static void populateUndeadFloor(ServerWorld level, BlockPos savePos) {
		boolean isPersistant = true;
		boolean isBaby = true;
		Utility.populateEntityType(EntityType.ZOMBIFIED_PIGLIN, level, savePos, 4, 0, isPersistant, isBaby);
		Utility.populateEntityType(EntityType.BLAZE, level, savePos, 3, -1, isPersistant, isBaby);
		if (!Utility.populateEntityType(EntityType.WITHER_SKELETON, level, savePos, 5, -1, isPersistant, isBaby)) {
			Utility.populateEntityType(EntityType.ZOGLIN, level, savePos, 2, 0, isPersistant, isBaby);
		}
	}

	public static void readData(BufferedReader br) {
		String line;
		int linecount = 0;
		realGCList.clear();

		try {
			while ((line = br.readLine()) != null) {
				linecount++;
				StringTokenizer st = new StringTokenizer(line, ",");
				try {
					int x = Integer.parseInt(st.nextToken().trim());
					int y = Integer.parseInt(st.nextToken().trim());
					int z = Integer.parseInt(st.nextToken().trim());
					realGCList.add(new BlockPos(x, y, z));
				} catch (Exception e) {
					if (!(line.isEmpty())) {
						Utility.debugMsg(0, "grimcitadels.data line " + linecount + " is malformed.");
					} else if (MyConfig.getDebugLevel() > 0) {
						Utility.debugMsg(0,
								"Harder Farther: Warning blank line at " + linecount + "th line of grimcitadels.dat");
					}
				}
			}
		} catch (Exception e) {
			Utility.debugMsg(0, "grimcitadels.dat not found in sudirectory saves/world/data.");
			// e.printStackTrace();
		}

	}

	public static void removeHeart(ServerWorld level, BlockPos pos) {

		Iterator<BlockPos> iter = realGCList.iterator();
		while (iter.hasNext()) {
			BlockPos gPos = iter.next();
			if ((gPos.getX() == pos.getX()) && gPos.getZ() == pos.getZ()) {
				iter.remove();
				addOptionalNewHearts(level, gPos);
				save();
				updateGCLocationsToClients(level);
				createHeartLoot(level, pos);
				break;
			}
		}
	}

	public static void save() {
		if (grimFile == null)
			return;
		try {
			FileOutputStream fos = new FileOutputStream(grimFile);
			writeData(fos);
			fos.close();
			Utility.debugMsg(1, "grimfile saved.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Used when a player logs in.
	public static void sendAllGCPosToClient(ServerPlayerEntity sp) {

		int size = realGCList.size();
		PacketByteBuf buf = PacketByteBufs.create();

		buf.writeInt(size);
		for (BlockPos pos : realGCList) {
			buf.writeBlockPos(pos);
		}

		ServerPlayNetworking.send((ServerPlayerEntity) sp, SyncAllGCWithClientPacket.GAME_PACKET_SYNC_GRIM_CITADEL_S2C, buf);
	}

	private static void updateGCLocationsToClients(ServerWorld level) {
		List<BlockPos> gcL = GrimCitadelManager.realGCList;

		List<ServerPlayerEntity> allPlayers = level.getServer().getPlayerManager().getPlayerList();
		Iterator<ServerPlayerEntity> apI = allPlayers.iterator();
		// v = new SAGCP(s,gcl)
		int size = realGCList.size();
		PacketByteBuf buf = PacketByteBufs.create();

		buf.writeInt(size);
		for (BlockPos pos : realGCList) {
			buf.writeBlockPos(pos);
		}

		while (apI.hasNext()) { // sends to all players online.
			ServerPlayNetworking.send((ServerPlayerEntity) apI.next(), SyncAllGCWithClientPacket.GAME_PACKET_SYNC_GRIM_CITADEL_S2C, buf);
		}
	}

	public static void writeData(FileOutputStream fos) {
		PrintStream p = null;
		p = new PrintStream(fos, true);
		Iterator<BlockPos> iter = realGCList.iterator();
		while (iter.hasNext()) {
			BlockPos pos = iter.next();
			p.println(pos.getX() + "," + pos.getY() + "," + pos.getZ());
		}
	}

}
