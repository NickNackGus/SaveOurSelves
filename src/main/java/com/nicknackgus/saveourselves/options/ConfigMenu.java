package com.nicknackgus.saveourselves.options;

import ch.njol.minecraft.config.ModMenuConfigSetup;
import com.nicknackgus.saveourselves.SaveOurSelvesClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ConfigMenu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ModMenuConfigSetup.getModConfigScreenFactory(SaveOurSelvesClient.MOD_IDENTIFIER + ".config", () -> SaveOurSelvesClient.options, new Options());
	}

}
