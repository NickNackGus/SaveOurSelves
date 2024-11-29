package com.nicknackgus.saveourselves.utils;

import net.minecraft.command.argument.NbtPathArgumentType.NbtPath;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class ItemUtils {

	public static final NbtPath MONUMENTA_OFFHAND_ATTRIBUTES_PATH =
			NbtUtils.getNbtPath("Monumenta.Stock.Attributes[{Slot:'offhand'}]").orElse(null);
	static final NbtPath SECOND_WIND_PATH =
			NbtUtils.getNbtPath("Monumenta.Stock.Enchantments.\"Second Wind\".Level").orElse(null);

	public static boolean isMonumentaOffhandItem(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if (item instanceof Equipment && !(item instanceof ShieldItem)) {
			return false;
		}

		NbtComponent component = itemStack.get(DataComponentTypes.CUSTOM_DATA);
		if (component == null) {
			return false;
		}
		NbtCompound itemTag = component.copyNbt();
		if (itemTag == null) {
			return false;
		}

		return !NbtUtils.getNbtAtPath(itemTag, MONUMENTA_OFFHAND_ATTRIBUTES_PATH).isEmpty();
	}

	public static int getMonumentaSecondWindLevel(ItemStack itemStack) {
		NbtComponent component = itemStack.get(DataComponentTypes.CUSTOM_DATA);
		if (component == null) {
			return 0;
		}
		NbtCompound itemTag = component.copyNbt();
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
