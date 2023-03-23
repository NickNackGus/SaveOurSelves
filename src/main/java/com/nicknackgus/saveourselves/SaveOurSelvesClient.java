package com.nicknackgus.saveourselves;

import ch.njol.minecraft.config.Config;
import com.google.gson.JsonParseException;
import com.nicknackgus.saveourselves.options.Options;
import com.nicknackgus.saveourselves.alarms.HeartbeatAlarm;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.MapColor;
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

		try (FileWriter writer = new FileWriter((FabricLoader.getInstance().getConfigDir().resolve("exported_map_colors.csv").toFile()))) {
			writer.write("Render Color Byte\tMap Color\tBrightness\tAlpha\tRed\tGreen\tBlue\n");
			for (int i = 0; i < 65536; i++) {
				MapColor mapColor;
				try {
					mapColor = MapColor.get(i);
				} catch (IndexOutOfBoundsException unused) {
					break;
				}
				if (i != 0 && MapColor.CLEAR.equals(mapColor)) {
					break;
				}
				for (MapColor.Brightness brightness : MapColor.Brightness.values()) {
					int renderColor = mapColor.getRenderColor(brightness);
					int renderColorByte = (mapColor.id << 2) | (brightness.id & 0x3);
					int a = (renderColor >> 24) & 0xff;
					int r = (renderColor >> 16) & 0xff;
					int g = (renderColor >> 8) & 0xff;
					int b = renderColor & 0xff;

					writer.write("" + renderColorByte +
							'\t' + mapColor.id +
							'\t' + brightness.id +
							'\t' + a +
							'\t' + r +
							'\t' + g +
							'\t' + b + '\n');
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
