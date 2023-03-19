package com.nicknackgus.saveourselves.options;

import ch.njol.minecraft.config.annotations.Category;
import ch.njol.minecraft.config.annotations.FloatSlider;
import com.nicknackgus.saveourselves.SaveOurSelvesClient;

public class Options implements ch.njol.minecraft.config.Options {
	@Category("self")
	public boolean selfLowHealthEnableLoop = true;
	@Category("self")
	@FloatSlider(min = 0.0F, max = 100.0F, step = 1.0F, unit = " %")
	public float selfLowHealthVolume = 25.0F;
	@Category("self")
	@FloatSlider(min = 0.0F, max = 100.0F, step = 5.0F, unit = " %")
	public float selfLowHealthPercentage = 50.0F;

	@Override
	public void onUpdate() {
		SaveOurSelvesClient.saveConfig();
	}
}
