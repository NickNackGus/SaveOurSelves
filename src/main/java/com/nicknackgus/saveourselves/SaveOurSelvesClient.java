package com.nicknackgus.saveourselves;

import ch.njol.minecraft.config.Config;
import com.google.gson.JsonParseException;
import com.nicknackgus.saveourselves.options.Options;
import com.nicknackgus.saveourselves.alarms.HeartbeatAlarm;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class SaveOurSelvesClient implements ClientModInitializer {

	public static final String MOD_IDENTIFIER = "save-our-selves";

	public static final String OPTIONS_FILE_NAME = "save-our-selves.json";

	public static Options options = new Options();

	@Override
	public void onInitializeClient() {
		try {
			options = Config.readJsonFile(Options.class, OPTIONS_FILE_NAME);
		} catch (FileNotFoundException e) {
			// Config file doesn't exist, so use default config (and write config file).
			try {
				Config.writeJsonFile(options, OPTIONS_FILE_NAME);
			} catch (IOException ex) {
				// ignore
			}
		} catch (IOException | JsonParseException e) {
			// Any issue with the config file silently reverts to the default config
			e.printStackTrace();
		}

		ClientTickEvents.END_CLIENT_TICK.register(new HeartbeatAlarm());


	}

	public static void saveConfig() {
		MinecraftClient.getInstance().execute(() -> {
			try {
				Config.writeJsonFile(options, OPTIONS_FILE_NAME);
			} catch (IOException ex) {
				// ignore
			}
		});
	}
}
