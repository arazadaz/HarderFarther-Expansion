package com.mactso.harderfarther.events;

import com.mactso.harderfarther.api.PlaceBlockCallback;
import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.network.GrimClientSongPacket;
import com.mactso.harderfarther.sounds.ModSounds;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;


public class BlockEvents {

	static int grimBonusDistSqr = PrimaryConfig.getGrimCitadelBonusDistanceSq();
	static int PROTECTED_DISTANCE = 999; // (about 33 blocks in all directions from heart)
	static int MIN_CANCEL_BLOCKPLACE_DISTANCE = 1200; // (about 33 blocks in all directions from heart)
	static int MAX_CANCEL_BLOCKPLACE_DISTANCE = 1500; // (about 33 blocks in all directions from heart)

	// client side variables.
	static long cGameTime = 0;

	public static void onBreakBlockBeforeRegister(){
		PlayerBlockBreakEvents.BEFORE.register(
				(world, player, pos, state, blockEntity) -> {

					Vec3 rfv = player.getForward().reverse().scale(0.6);
					long gameTime = world.getGameTime();
					RandomSource rand = world.getRandom();

					float adjustY = 0;
					if (player.blockPosition().getY() < pos.getY()) {
						adjustY = -0.5f;
					}

					if(GrimCitadelManager.getClosestGrimCitadelDistanceSq(pos) <= PROTECTED_DISTANCE){
						if(GrimCitadelManager.getProtectedBlocks().contains(world.getBlockState(pos).getBlock())){
							//event.setNewSpeed(event.getOriginalSpeed() / 20);    Might re-add in the future, but not for initial port.
							return false;
						}
						if(world.isClientSide()){
							if(cGameTime < gameTime){
								cGameTime = gameTime + 20 + rand.nextInt(40);
								world.playSound(player, pos, SoundEvents.VILLAGER_NO, SoundSource.AMBIENT, 0.11f, 0.06f);
								for (int j = 0; j < 21; ++j) {
									double x = (double) pos.getX() + rand.nextDouble() * (double) 0.1F;
									double y = (double) pos.getY() + rand.nextDouble() + adjustY;
									double z = (double) pos.getZ() + rand.nextDouble();
									world.addParticle(ParticleTypes.WITCH, x, y, z, rfv.x, rfv.y, rfv.z);
								}
							}
						}
					}

					return true;
				});
	}

	//server-side only event.
	public static void onBreakBlockAfterRegister(){
		PlayerBlockBreakEvents.AFTER.register(
				(world, player, pos, state, blockEntity) -> {

					if (player.isCreative()){
						return;
					}

					Block block = state.getBlock();

					if(block == ModBlocks.GRIM_GATE) {
						GrimCitadelManager.doBrokenGrimGate((ServerPlayer)player, (ServerLevel)world, pos, state);
					}else if(block == ModBlocks.GRIM_HEART){
						new GrimClientSongPacket(ModSounds.NUM_LAKE_DESTINY).send(player);
					}

				});
	}


	//Uses a custom event I made since I couldn't find a block place event in the fabric API, but I'm probably blind & one probably exists.
	public static void onBlockPlacementRegister(){
		PlaceBlockCallback.EVENT.register(
				(context, state) -> {
					if(GrimCitadelManager.isInGrimProtectedArea(context.getClickedPos())){
						updateHands((ServerPlayer) context.getPlayer());
						return InteractionResult.FAIL;
					}
					return InteractionResult.PASS;
				});
	}

	/**
	 * fix client side view of the hotbar for non creative This makes it so items
	 * don't look like they poofed.
	 */
	public static void updateHands(ServerPlayer player) {
		if (player.connection == null)
			return;
		ItemStack itemstack = player.getInventory().getSelected();
		if (!itemstack.isEmpty())
			slotChanged(player, 36 + player.getInventory().selected, itemstack);
		itemstack = player.getInventory().offhand.get(0);
		if (!itemstack.isEmpty())
			slotChanged(player, 45, itemstack);
	}

	public static void slotChanged(ServerPlayer player, int index, ItemStack itemstack) {
		InventoryMenu menu = player.inventoryMenu;
		player.connection.send(
				new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), index, itemstack));
	}

	//Not included in initial port
	/*@SubscribeEvent
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
	}*/

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

	//Not included in initial port
	/*@SubscribeEvent
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
	}*/

}