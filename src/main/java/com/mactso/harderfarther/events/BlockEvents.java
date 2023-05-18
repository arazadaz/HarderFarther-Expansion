package com.mactso.harderfarther.events;

import java.util.List;
import java.util.ListIterator;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.network.GrimClientSongPacket;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.sounds.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;


@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class BlockEvents {

	static int grimBonusDistSqr = MyConfig.getGrimCitadelBonusDistanceSq();
	static int PROTECTED_DISTANCE = 999; // (about 33 blocks in all directions from heart)
	static int MIN_CANCEL_BLOCKPLACE_DISTANCE = 1200; // (about 33 blocks in all directions from heart)
	static int MAX_CANCEL_BLOCKPLACE_DISTANCE = 1500; // (about 33 blocks in all directions from heart)

	// client side variables.
	static long cGameTime = 0;

	@SubscribeEvent
	public static void onBreakingSpeed(BreakSpeed event) {
		// note: This is both server and clientside. client uses to display properly.
		if (event.getEntity() == null) {
			return;
		} else if (event.getEntity().isCreative()) {
			return;
		} else if (!(event.getPosition().isPresent())) {
			return;
		}

		PlayerEntity p = event.getEntity();
		Vec3d rfv = p.getRotationVecClient().negate().multiply(0.6);
		World level = p.world;
		long gameTime = level.getTime();
		RandomGenerator rand = level.getRandom();
		BlockPos ePos = event.getPosition().get();

		float adjustY = 0;
		if (p.getBlockPos().getY() < ePos.getY()) {
			adjustY = -0.5f;
		}

		if (GrimCitadelManager.getClosestGrimCitadelDistanceSq(ePos) <= PROTECTED_DISTANCE) {
			if (GrimCitadelManager.getProtectedBlocks().contains(level.getBlockState(ePos).getBlock())
					&& event.isCancelable()) {
				event.setNewSpeed(event.getOriginalSpeed() / 20);
				event.setCanceled(true);
				if (level.isClient) {
					if (cGameTime < gameTime ) { 
						cGameTime = gameTime + 20 + rand.nextInt(40);
						level.playSound(p, ePos, SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.AMBIENT, 0.11f, 0.6f);
						for (int j = 0; j < 21; ++j) {
							double x = (double) ePos.getX() + rand.nextDouble() * (double) 0.1F;
							double y = (double) ePos.getY() + rand.nextDouble()+adjustY;
							double z = (double) ePos.getZ() + rand.nextDouble();
							level.addParticle(ParticleTypes.WITCH, x, y, z, rfv.x, rfv.y, rfv.z);
						}
					}
				}
			}
		}

	}

	@SubscribeEvent
	public static void onBreakBlock(BreakEvent event) {

		// server side only event.
		ServerPlayerEntity sp = (ServerPlayerEntity) event.getPlayer();
		ServerWorld serverLevel = (ServerWorld) sp.world;
		BlockPos pos = event.getPos();
		BlockState bs = serverLevel.getBlockState(pos);
		Block b = bs.getBlock();
		if (b == ModBlocks.GRIM_GATE) {
			GrimCitadelManager.doBrokenGrimGate(sp, serverLevel, pos, bs);
		} else if (b == ModBlocks.GRIM_HEART) {
			Network.sendToClient(new GrimClientSongPacket(ModSounds.NUM_LAKE_DESTINY), sp);
		}

		if (sp.isCreative())
			return;

		if (GrimCitadelManager.getClosestGrimCitadelDistanceSq(pos) <= PROTECTED_DISTANCE) {
			if (GrimCitadelManager.getProtectedBlocks().contains(serverLevel.getBlockState(pos).getBlock())
					&& event.isCancelable()) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onBlockPlacement(EntityPlaceEvent event) {

		if (!(event.getEntity() instanceof PlayerEntity))
			return;

		if (GrimCitadelManager.isInGrimProtectedArea(event.getPos())) {
			event.setCanceled(true);
			updateHands((ServerPlayerEntity) event.getEntity());
		}

	}

	/**
	 * fix client side view of the hotbar for non creative This makes it so items
	 * don't look like they poofed.
	 */
	public static void updateHands(ServerPlayerEntity player) {
		if (player.networkHandler == null)
			return;
		ItemStack itemstack = player.getInventory().getMainHandStack();
		if (!itemstack.isEmpty())
			slotChanged(player, 36 + player.getInventory().selectedSlot, itemstack);
		itemstack = player.getInventory().offHand.get(0);
		if (!itemstack.isEmpty())
			slotChanged(player, 45, itemstack);
	}

	public static void slotChanged(ServerPlayerEntity player, int index, ItemStack itemstack) {
		PlayerScreenHandler menu = player.playerScreenHandler;
		player.networkHandler.sendPacket(
				new ScreenHandlerSlotUpdateS2CPacket(menu.syncId, menu.nextRevision(), index, itemstack));
	}

	@SubscribeEvent
	public static void onBucket(FillBucketEvent event) {

		HitResult target = event.getTarget();

		if (target.getType() == HitResult.Type.MISS)
			return;

		if (target.getType() == HitResult.Type.ENTITY)
			return;

		if (target.getType() == HitResult.Type.BLOCK) {
			PlayerEntity player = event.getEntity();
			World world = player.world;
			BlockHitResult blockray = (BlockHitResult) target;
			BlockPos blockpos = blockray.getBlockPos();

			Fluid fluid = null;
			ItemStack stack = event.getEmptyBucket();
			Item item = stack.getItem();
			if (item instanceof BucketItem) {
				BucketItem bucket = (BucketItem) item;
				fluid = bucket.getFluid();
			} else {
				// not a bucket (not sure this will happen), so guess
				FluidState state = world.getFluidState(blockpos);
				if (state.getFluid() != Fluids.EMPTY)
					fluid = Fluids.EMPTY;
			}
			if (fluid != Fluids.EMPTY) {
				boolean next = true;
				if (fluid != null) {
					BlockState state = world.getBlockState(blockpos);
					Block block = state.getBlock();
					if (block instanceof FluidFillable) {
						FluidFillable lc = (FluidFillable) block;
						if (lc.canFillWithFluid(world, blockpos, state, fluid))
							next = false;
					}
				}
				if (next)
					blockpos = blockpos.offset(blockray.getSide());
			}

			if (GrimCitadelManager.getClosestGrimCitadelDistanceSq(blockpos) <= PROTECTED_DISTANCE) {
				if (event.isCancelable())
					event.setCanceled(true);

			}
		}
	}

//	@SubscribeEvent
//	public static void onCreateFluidSourceEvent (CreateFluidSourceEvent event)
//	{	
//
//		if (GrimCitadelManager.isInGrimProtectedArea(event.getPos())) {
//			addToKillWaterPosList(event.getPos());
//		}		
//	}
//
//	public static void addToKillWaterPosList(BlockPos newKillWaterPos) {
//		killWaterPos.add(newKillWaterPos);
//	}
//	
//	public static void killIllegalFluidBlocks(Level l) {
//		for (int i = 0; i < killWaterPos.size();i++ ) {
//			l.setBlock(killWaterPos.get(i), Blocks.AIR.defaultBlockState(), 3);
//		}
//		killWaterPos.clear();
//	}

	@SubscribeEvent
	public static void onExplosionDetonate(Detonate event) {
		World level = event.getLevel();
		List<BlockPos> list = event.getAffectedBlocks();
		Vec3d vPos = event.getExplosion().getPosition();
		if (GrimCitadelManager
				.getClosestGrimCitadelDistanceSq(new BlockPos(vPos.x, vPos.y, vPos.z)) <= PROTECTED_DISTANCE) {
			for (ListIterator<BlockPos> iter = list.listIterator(list.size()); iter.hasPrevious();) {
				BlockPos pos = iter.previous();
				if (GrimCitadelManager.getProtectedBlocks().contains(level.getBlockState(pos).getBlock())) {
					iter.remove();
				}
			}
		}
	}

}