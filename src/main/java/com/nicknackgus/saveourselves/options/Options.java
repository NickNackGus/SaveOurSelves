package com.nicknackgus.saveourselves.options;

import ch.njol.minecraft.config.annotations.Category;
import ch.njol.minecraft.config.annotations.Dropdown;
import ch.njol.minecraft.config.annotations.FloatSlider;
import ch.njol.minecraft.config.annotations.IntSlider;
import com.nicknackgus.saveourselves.SaveOurSelvesClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class Options implements ch.njol.minecraft.config.Options {
	@Category("self")
	@FloatSlider(min = 0.0F, max = 100.0F, step = 1.0F, unit = " %")
	public float selfLowHealthPercentage = 50.0F;

	@Dropdown("selfCustomSound")
	@Category("self")
	public boolean selfLowHealthEnableCustomSound = false;
	@Dropdown("selfCustomSound")
	@Category("self")
	public String selfLowHealthCustomSound = "minecraft:entity.wither.spawn";
	@Dropdown("selfCustomSound")
	@Category("self")
	public int selfLowHealthCustomSoundMillisLow = 2000;
	@Dropdown("selfCustomSound")
	@Category("self")
	public int selfLowHealthCustomSoundMillisCritical = 1000;
	@Dropdown("selfCustomSound")
	@Category("self")
	@FloatSlider(min = 0.0F, max = 100.0F, step = 1.0F, unit = " %")
	public float selfLowHealthCustomSoundVolumeLow = 25f;
	@Dropdown("selfCustomSound")
	@Category("self")
	@FloatSlider(min = 0.0F, max = 100.0F, step = 1.0F, unit = " %")
	public float selfLowHealthCustomSoundVolumeCritical = 30f;
	@Dropdown("selfCustomSound")
	@Category("self")
	@FloatSlider(min = 0.5F, max = 2.0F, step = 0.01F, unit = "x")
	public float selfLowHealthCustomSoundPitchLow = 0.50f;
	@Dropdown("selfCustomSound")
	@Category("self")
	@FloatSlider(min = 0.5F, max = 2.0F, step = 0.01F, unit = "x")
	public float selfLowHealthCustomSoundPitchCritical = 1.00f;

	@Dropdown("selfHeartbeat")
	@Category("self")
	public boolean selfLowHealthEnableHeartbeat = true;
	@Dropdown("selfHeartbeat")
	@Category("self")
	@FloatSlider(min = 0.0F, max = 100.0F, step = 1.0F, unit = " %")
	public float selfLowHealthHeartbeatVolume = 25.0F;
	@Dropdown("selfHeartbeat")
	@Category("self")
	@IntSlider(min = 0, max = 240, unit = " BPM")
	public int selfAlarmHeartbeatHealthLow = 60;
	@Dropdown("selfHeartbeat")
	@Category("self")
	@IntSlider(min = 0, max = 240, unit = " BPM")
	public int selfAlarmHeartbeatHealthCritical = 120;


	@Category("player")
	@FloatSlider(min = 0.0F, max = 100.0F, step = 1.0F, unit = " %")
	public float playerLowHealthPercentage = 50.0F;

	@Dropdown("playerCustomSound")
	@Category("player")
	public boolean playerLowHealthEnableCustomSound = false;
	@Dropdown("playerCustomSound")
	@Category("player")
	public String playerLowHealthCustomSound = "minecraft:entity.elder_guardian.curse";
	@Dropdown("playerCustomSound")
	@Category("player")
	public int playerLowHealthCustomSoundMillisLow = 2000;
	@Dropdown("playerCustomSound")
	@Category("player")
	public int playerLowHealthCustomSoundMillisCritical = 500;
	@Dropdown("playerCustomSound")
	@Category("player")
	@FloatSlider(min = 0.0F, max = 100.0F, step = 1.0F, unit = " %")
	public float playerLowHealthCustomSoundVolumeLow = 25f;
	@Dropdown("playerCustomSound")
	@Category("player")
	@FloatSlider(min = 0.0F, max = 100.0F, step = 1.0F, unit = " %")
	public float playerLowHealthCustomSoundVolumeCritical = 30f;
	@Dropdown("playerCustomSound")
	@Category("player")
	@FloatSlider(min = 0.5F, max = 2.0F, step = 0.01F, unit = "x")
	public float playerLowHealthCustomSoundPitchLow = 0.50f;
	@Dropdown("playerCustomSound")
	@Category("player")
	@FloatSlider(min = 0.5F, max = 2.0F, step = 0.01F, unit = "x")
	public float playerLowHealthCustomSoundPitchCritical = 2.00f;

	@Dropdown("playerHeartbeat")
	@Category("player")
	public boolean playerLowHealthEnableHeartbeat = false;
	@Dropdown("playerHeartbeat")
	@Category("player")
	@FloatSlider(min = 0.0F, max = 100.0F, step = 1.0F, unit = " %")
	public float playerLowHealthHeartbeatVolume = 25.0F;
	@Dropdown("playerHeartbeat")
	@Category("player")
	@IntSlider(min = 0, max = 240, unit = " BPM")
	public int playerAlarmHeartbeatHealthLow = 60;
	@Dropdown("playerHeartbeat")
	@Category("player")
	@IntSlider(min = 0, max = 240, unit = " BPM")
	public int playerAlarmHeartbeatHealthCritical = 120;

	@Override
	public void onUpdate() {
		if (selfLowHealthCustomSoundMillisLow < 50) {
			selfLowHealthCustomSoundMillisLow = 50;
		}
		if (selfLowHealthCustomSoundMillisCritical < 50) {
			selfLowHealthCustomSoundMillisCritical = 50;
		}
		try {
			new Identifier(selfLowHealthCustomSound);
		} catch (InvalidIdentifierException ex) {
			selfLowHealthCustomSound = "minecraft:entity.wither.spawn";
		}

		if (playerLowHealthCustomSoundMillisLow < 50) {
			playerLowHealthCustomSoundMillisLow = 50;
		}
		if (playerLowHealthCustomSoundMillisCritical < 50) {
			playerLowHealthCustomSoundMillisCritical = 50;
		}
		try {
			new Identifier(playerLowHealthCustomSound);
		} catch (InvalidIdentifierException ex) {
			playerLowHealthCustomSound = "minecraft:entity.elder_guardian.curse";
		}

		SaveOurSelvesClient.saveConfig();
	}
}
