package com.nicknackgus.saveourselves.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class PlayerUtils {
	private static final float SECOND_WIND_DAMAGE_RESIST = 0.1F;
	private static final float SECOND_WIND_HEALTH_LIMIT = 0.5F;

	public static float getMaxHealth(PlayerEntity player, int secondWindLevel) {
		float maxHealth = player.getMaxHealth();
		if (maxHealth <= 0.0F) {
			return 0.0F;
		}
		float secondWindDamageMultiplier = (float) Math.pow(1 - SECOND_WIND_DAMAGE_RESIST, secondWindLevel);
		return maxHealth * (1.0F - SECOND_WIND_HEALTH_LIMIT + SECOND_WIND_HEALTH_LIMIT / secondWindDamageMultiplier);
	}

	public static float getHealth(PlayerEntity player, int secondWindLevel) {
		float vanillaMaxHealth = player.getMaxHealth();
		if (vanillaMaxHealth <= 0.0F) {
			return 0.0F;
		}

		float vanillaHealthThreshold = vanillaMaxHealth * SECOND_WIND_HEALTH_LIMIT;
		float secondWindDamageMultiplier = (float) Math.pow(1 - SECOND_WIND_DAMAGE_RESIST, secondWindLevel);

		float normalHealth = player.getHealth();
		if (normalHealth < vanillaHealthThreshold) {
			normalHealth /= secondWindDamageMultiplier;
		} else {
			normalHealth += vanillaHealthThreshold / secondWindDamageMultiplier - vanillaHealthThreshold;
		}

		float absorptionHealth = player.getAbsorptionAmount();
		return normalHealth + absorptionHealth;
	}

	public static float getHealthPercent(PlayerEntity player) {
		int secondWindLevel = getMonumentaSecondWindLevel(player);

		float maxHealth = getMaxHealth(player, secondWindLevel);
		if (maxHealth == 0.0F) {
			return 0.0F;
		}

		float health = getHealth(player, secondWindLevel);
		return 100.0F * health / maxHealth;
	}

	public static int getMonumentaSecondWindLevel(PlayerEntity player) {
		int level = 0;
		ItemStack handItemStack;

		handItemStack = player.getOffHandStack();
		if (ItemUtils.isMonumentaOffhandItem(handItemStack)) {
			level += ItemUtils.getMonumentaSecondWindLevel(handItemStack);
		}

		handItemStack = player.getMainHandStack();
		level += ItemUtils.getMonumentaSecondWindLevel(handItemStack);

		for (ItemStack itemStack : player.getArmorItems()) {
			level += ItemUtils.getMonumentaSecondWindLevel(itemStack);
		}
		return level;
	}

}
