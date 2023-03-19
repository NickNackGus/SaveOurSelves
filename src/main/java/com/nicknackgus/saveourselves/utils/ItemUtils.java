package com.nicknackgus.saveourselves.utils;

import net.minecraft.command.argument.NbtPathArgumentType.NbtPath;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class ItemUtils {

	static final NbtPath SECOND_WIND_PATH =
			NbtUtils.getNbtPath("Monumenta.Stock.Enchantments.\"Second Wind\".Level").orElse(null);

	public static int getMonumentaSecondWindLevel(ItemStack itemStack) {
		NbtCompound itemTag = itemStack.getNbt();
		if (itemTag != null) {
			for (NbtElement levelTag : NbtUtils.getNbtAtPath(itemTag, SECOND_WIND_PATH)) {
				if (levelTag instanceof AbstractNbtNumber levelNumberTag) {
					return levelNumberTag.intValue();
				}
			}
		}
		return 0;
	}

}
