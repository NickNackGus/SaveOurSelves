package com.nicknackgus.saveourselves.options;

import ch.njol.minecraft.config.annotations.Category;
import ch.njol.minecraft.config.annotations.FloatSlider;
import ch.njol.minecraft.config.annotations.IntSlider;
import com.nicknackgus.saveourselves.SaveOurSelvesClient;

public class Options implements ch.njol.minecraft.config.Options {
	@Category("self")
	@FloatSlider(min = 0.0F, max = 100.0F, step = 1.0F, unit = " %")
	public float selfLowHealthPercentage = 50.0F;
	@Category("self")
	public boolean selfLowHealthEnableHeartbeat = true;
	@Category("self")
	@FloatSlider(min = 0.0F, max = 100.0F, step = 1.0F, unit = " %")
	public float selfLowHealthHeartbeatVolume = 25.0F;
	@Category("self")
	@IntSlider(min = 0, max = 240, unit = " BPM")
	public int selfAlarmHeartbeatHealthLow = 60;
	@Category("self")
	@IntSlider(min = 0, max = 240, unit = " BPM")
	public int selfAlarmHeartbeatHealthCritical = 120;

	@Override
	public void onUpdate() {
		SaveOurSelvesClient.saveConfig();
	}
}
