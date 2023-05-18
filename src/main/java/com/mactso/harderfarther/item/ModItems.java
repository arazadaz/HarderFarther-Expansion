package com.mactso.harderfarther.item;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.utility.Utility;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;


public class ModItems {

	public static final Item LIFE_HEART = new Item(new Item.Settings().group(ItemGroup.MISC).fireproof().rarity(Rarity.EPIC));
	public static ItemStack LIFE_HEART_STACK = null;
	public static final Item DEAD_BRANCHES = new BlockItem(ModBlocks.DEAD_BRANCHES, (new Item.Settings()).group(ItemGroup.DECORATIONS));
	public static final Item BURNISHING_STONE = new BurnishingStone(new Item.Settings().group(ItemGroup.MISC).fireproof().rarity(Rarity.RARE));
	public static ItemStack BURNISHING_STONE_STACK = null;


	public static void register(){
		Registry.register(Registry.ITEM, new Identifier(Main.MODID, "dead_branches"), DEAD_BRANCHES);
		Registry.register(Registry.ITEM, new Identifier(Main.MODID, "life_heart"), LIFE_HEART);
		Registry.register(Registry.ITEM, new Identifier(Main.MODID, "burnishing_stone"), BURNISHING_STONE);

		LIFE_HEART_STACK = new ItemStack(LIFE_HEART, 1);
		Utility.setLore(LIFE_HEART_STACK,
				Text.Serializer.toJson(Text.translatable("item.harderfarther.life_heart.lore")));

		BURNISHING_STONE_STACK = new ItemStack(BURNISHING_STONE, 1);
		Utility.setLore(BURNISHING_STONE_STACK,
				Text.Serializer.toJson(Text.translatable("item.harderfarther.burnishing_stone.lore")));
	}


}
