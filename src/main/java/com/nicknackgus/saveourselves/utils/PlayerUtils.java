package com.nicknackgus.saveourselves.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class PlayerUtils {

	public static float getMaxHealth(PlayerEntity player) {
		float maxHealth = player.getMaxHealth();
		if (maxHealth <= 0.0F) {
			return 0.0F;
		}
		return maxHealth * (1.0F + 0.5F * getMonumentaSecondWindLevel(player));
	}

	public static float getHealth(PlayerEntity player) {
		float vanillaMaxHealth = player.getMaxHealth();
		if (vanillaMaxHealth <= 0.0F) {
			return 0.0F;
		}

		int secondWindLevel = getMonumentaSecondWindLevel(player);
		float halfVanillaMaxHealth = 0.5F * vanillaMaxHealth;

		float normalHealth = player.getHealth();
		if (normalHealth < halfVanillaMaxHealth) {
			normalHealth = normalHealth * (secondWindLevel + 1);
		} else {
			normalHealth += vanillaMaxHealth * 0.5F * secondWindLevel;
		}

		float absorptionHealth = player.getAbsorptionAmount();
		return normalHealth + absorptionHealth;
	}

	public static float getHealthPercent(PlayerEntity player) {
		float maxHealth = getMaxHealth(player);
		if (maxHealth == 0.0F) {
			return 0.0F;
		}
		return 100.0F * getHealth(player) / maxHealth;
	}

	public static int getMonumentaSecondWindLevel(PlayerEntity player) {
		int level = 0;
		for (ItemStack itemStack : player.getItemsHand()) {
			level += ItemUtils.getMonumentaSecondWindLevel(itemStack);
		}
		for (ItemStack itemStack : player.getArmorItems()) {
			level += ItemUtils.getMonumentaSecondWindLevel(itemStack);
		}
		return level;
	}

}
