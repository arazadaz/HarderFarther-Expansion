package com.mactso.harderfarther.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.PotionItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SignItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.utility.Utility;

public class ChestLootManager {

	private static final Logger LOGGER = LogManager.getLogger();

	public static List<ChestLootItem> chestLootTable = new ArrayList<>();
	public static boolean init = false;

	public static void init(String[] lootItems) {

		chestLootTable.clear();

		int i = 0;
		for (String lootLine : lootItems) {
			lootLine = lootLine.trim();
			if (lootLine.isEmpty())
				continue;
			try {
				i++;
				StringTokenizer st = new StringTokenizer(lootLine, ",");
				int lootpct = Integer.parseInt(st.nextToken());
				Item lootItem = Utility.getItemFromString(st.nextToken());
				int lootMin = Integer.parseInt(st.nextToken());
				if (lootMin <= 0)
					lootMin = 1;
				int lootMax = Integer.parseInt(st.nextToken());
				if (lootMax <= 0)
					lootMax = 1;
				chestLootTable.add(new ChestLootItem(lootpct, lootItem, lootMin, lootMax));

			} catch (Exception e) {
				LOGGER.warn("WARNING: Harder Farther Debug:  Bad LootItem Config at line " + i + ",  '" + lootLine + "' ");
			}

		}

	}

	public static String asString(ChestLootItem ci) {
		return ("(" + ci.lootpct + ") " + ci.lootItem.getName().getString().toString() + ", " + ci.lootMin
				+ " to " + ci.lootMax);
	}

	
	
	public static ItemStack doGetLootStack(ServerWorld level, Vec3d origin) {

		RandomGenerator rand = level.getRandom();
		float difficulty = HarderFartherManager.getDistanceDifficultyHere(level, origin);

		int rawroll = (int) (100 * difficulty);
		int roll = rawroll;
		int modifier = 0;

		if (rawroll > 6) { // omit weaker loot farther as difficulty increases
			roll = rawroll / 2;
			modifier = rawroll / 2;
		}

		if (roll < 1)
			roll = 1;
		
		int enchantmentLevel = 4 + MyConfig.getBonusLootEnchantmentLevelModifier();

		int chestLootRoll = Math.min(rand.nextInt(roll) + modifier, chestLootTable.size()-1);
		ChestLootItem ci = chestLootTable.get(chestLootRoll);
		int amt = 1;
		int x = 6;
		if (ci.lootMax > 1) {
			amt = rand.nextInt(ci.lootMax - ci.lootMin) + ci.lootMin;
		}

		ItemStack stack = new ItemStack(ci.lootItem,amt);
		if (ci.lootItem instanceof ArmorItem) {
			  stack.addEnchantment(Enchantments.PROTECTION, enchantmentLevel);
				stack.setCustomName(Text.translatable("item.harderfarther.ancient_armor.name"));
		}
		else
		if ((ci.lootItem instanceof WallStandingBlockItem) && !(ci.lootItem instanceof SignItem) && !(ci.lootItem instanceof BannerItem)) {
			  stack.addEnchantment(Enchantments.PROTECTION, enchantmentLevel);
				stack.setCustomName(Text.translatable("item.harderfarther.ancient_armor.name"));
		}
		else
		if (ci.lootItem instanceof BowItem) {
			  stack.addEnchantment(Enchantments.POWER, 6);
				stack.setCustomName(Text.translatable("item.harderfarther.ancient_bow.name"));
				Utility.setLore(stack,
						Text.Serializer.toJson(Text.translatable("item.harderfarther.ancient_bow.lore")));
		}
		else
		if (ci.lootItem instanceof SwordItem) {
			int swordroll = level.getRandom().nextInt(30);
			if (swordroll < 15) {
				stack.addEnchantment(Enchantments.SHARPNESS, 7);
				stack.setCustomName(Text.translatable("item.harderfarther.ancient_sword.name"));
				Utility.setLore(stack,
						Text.Serializer.toJson(Text.translatable("item.harderfarther.ancient_sword.lore")));
			} else if (swordroll < 25) {
				stack.addEnchantment(Enchantments.SMITE, 6);
				stack.setCustomName(Text.translatable("item.harderfarther.ancient_sword.name"));
				Utility.setLore(stack,
				Text.Serializer.toJson(Text.translatable("item.harderfarther.ancient_smite_sword.lore")));
			} else {
				stack.addEnchantment(Enchantments.BANE_OF_ARTHROPODS, 6);
				stack.setCustomName(Text.translatable("item.harderfarther.ancient_sword.name"));
				Utility.setLore(stack,
				Text.Serializer.toJson(Text.translatable("item.harderfarther.ancient_bane_sword.lore")));
			}
		}
		else
		if (ci.lootItem instanceof PickaxeItem) {
			  stack.addEnchantment(Enchantments.EFFICIENCY, 6);
			  stack.setCustomName(Text.translatable("item.harderfarther.ancient_pickaxe.name"));
		}
		else
		if (ci.lootItem instanceof AxeItem) {
			  stack.addEnchantment(Enchantments.EFFICIENCY, 6);
				stack.setCustomName(Text.translatable("item.harderfarther.ancient_axe.name"));
		}
		else
		if (ci.lootItem instanceof ShovelItem) {
			  stack.addEnchantment(Enchantments.EFFICIENCY, 6);
				stack.setCustomName(Text.translatable("item.harderfarther.ancient_shovel.name"));
		}
		else
		if (ci.lootItem instanceof PotionItem) {
			int potionroll = level.getRandom().nextInt(30);
			if (potionroll <15) {
				stack = LootManager.makeLifeSavingPotion(difficulty);
			} else {
				stack = LootManager.makeOgreStrengthPotion(difficulty);
				
			}
		}
		
		return stack;
	}

	public static class ChestLootItem {

		int lootpct;
		Item lootItem;
		int lootMin;
		int lootMax;

		public ChestLootItem(int lootpct, Item lootItem, int lootMin, int lootMax) {
			this.lootpct = lootpct;
			this.lootItem = lootItem;
			this.lootMin = lootMin;
			this.lootMax = lootMax;
		}
	}
}
