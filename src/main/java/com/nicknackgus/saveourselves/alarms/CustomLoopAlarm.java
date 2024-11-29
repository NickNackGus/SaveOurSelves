package com.nicknackgus.saveourselves.alarms;

import com.nicknackgus.saveourselves.SaveOurSelvesClient;
import com.nicknackgus.saveourselves.options.Options;
import com.nicknackgus.saveourselves.utils.PlayerUtils;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CustomLoopAlarm implements ClientTickEvents.EndTick {

	public static final double MAX_DISTANCE = 32.0;

	protected static class PlayerState {
		UUID playerId;
		public boolean isSelf = false;
		boolean healthy = true;
		LocalDateTime loopStartTime = LocalDateTime.now(ZoneOffset.UTC);
		long lastLoopPartMillis = -1L;
		Random random = new Random();

		public PlayerState(UUID playerId) {
			this.playerId = playerId;
		}

		public void update(MinecraftClient client, PlayerEntity player) {
			float healthPercent = PlayerUtils.getHealthPercent(player);
			float healthThreshold;
			if (isSelf) {
				healthThreshold = SaveOurSelvesClient.options.selfLowHealthPercentage;
			} else {
				healthThreshold = SaveOurSelvesClient.options.playerLowHealthPercentage;
			}
			if (healthThreshold <= 0.0f) {
				return;
			}
			if (healthPercent > healthThreshold) {
				healthy = true;
			} else {
				LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

				if (healthy) {
					loopStartTime = now;
				}
				healthy = false;

				float msLow;
				float msCritical;
				if (isSelf) {
					msLow = SaveOurSelvesClient.options.selfLowHealthCustomSoundMillisLow;
					msCritical = SaveOurSelvesClient.options.selfLowHealthCustomSoundMillisCritical;
				} else {
					msLow = SaveOurSelvesClient.options.playerLowHealthCustomSoundMillisLow;
					msCritical = SaveOurSelvesClient.options.playerLowHealthCustomSoundMillisCritical;
				}

				if (msLow < 50 || msCritical < 50) {
					return;
				}

				float msFloat = msLow + (msCritical - msLow) * (healthThreshold - healthPercent) / healthThreshold;
				if (msFloat <= 50.0F) {
					return;
				}
				long loopDurationMillis = (long) msFloat;
				if (loopDurationMillis == 0L) {
					return;
				}
				long loopPartMillis = loopStartTime.until(now, ChronoUnit.MILLIS);
				if (loopPartMillis >= loopDurationMillis) {
					lastLoopPartMillis = -1L;
					long loopsElapsed = loopPartMillis / loopDurationMillis;
					loopStartTime = loopStartTime.plus(loopsElapsed * loopDurationMillis,
							ChronoUnit.MILLIS);
					loopPartMillis %= loopDurationMillis;
				}

				if (lastLoopPartMillis < 0) {
					playSound(client, player, healthPercent, healthThreshold);
				}
				lastLoopPartMillis = loopPartMillis;
			}
		}

		private void playSound(
				MinecraftClient client,
				PlayerEntity player,
				float healthPercent,
				float healthThreshold
		) {
			if (isSelf) {
				if (!SaveOurSelvesClient.options.selfLowHealthEnableCustomSound) {
					return;
				}
			} else {
				if (!SaveOurSelvesClient.options.playerLowHealthEnableCustomSound) {
					return;
				}
			}

			SoundEvent sound;
			float volumeLow;
			float volumeCritical;
			float pitchLow;
			float pitchCritical;
			if (isSelf) {
				sound = SoundEvent.of(Identifier.of(SaveOurSelvesClient.options.selfLowHealthCustomSound));
				volumeLow = 0.01f * SaveOurSelvesClient.options.selfLowHealthCustomSoundVolumeLow;
				volumeCritical = 0.01f * SaveOurSelvesClient.options.selfLowHealthCustomSoundVolumeCritical;
				pitchLow = SaveOurSelvesClient.options.selfLowHealthCustomSoundPitchLow;
				pitchCritical = SaveOurSelvesClient.options.selfLowHealthCustomSoundPitchCritical;
			} else {
				sound = SoundEvent.of(Identifier.of(SaveOurSelvesClient.options.playerLowHealthCustomSound));
				volumeLow = 0.01f * SaveOurSelvesClient.options.playerLowHealthCustomSoundVolumeLow;
				volumeCritical = 0.01f * SaveOurSelvesClient.options.playerLowHealthCustomSoundVolumeCritical;
				pitchLow = SaveOurSelvesClient.options.playerLowHealthCustomSoundPitchLow;
				pitchCritical = SaveOurSelvesClient.options.playerLowHealthCustomSoundPitchCritical;
			}

			float volume = volumeLow + (volumeCritical - volumeLow) * (healthThreshold - healthPercent) / healthThreshold;
			float pitch = pitchLow + (pitchCritical - pitchLow) * (healthThreshold - healthPercent) / healthThreshold;

			volume = Float.max(0.0f, Float.min(1.0f, volume));
			pitch = Float.max(0.5f, Float.min(2.0f, pitch));

			client.getSoundManager().play(
					new EntityTrackingSoundInstance(sound,
							SoundCategory.MASTER,
							volume,
							pitch,
							player,
							random.nextLong()));
		}
	}

	static ConcurrentSkipListMap<UUID, PlayerState> playerStates = new ConcurrentSkipListMap<>();

	@Override
	public void onEndTick(MinecraftClient client) {
		ClientWorld clientWorld = client.world;
		if (clientWorld == null || (client.isInSingleplayer() && client.isPaused())) {
			return;
		}

		Options options = SaveOurSelvesClient.options;

		PlayerEntity self = client.player;
		if (self == null) {
			return;
		}

		if (options.selfLowHealthEnableCustomSound) {
			UUID uuid = self.getUuid();
			PlayerState playerState = playerStates.computeIfAbsent(uuid, PlayerState::new);
			playerState.isSelf = true;
			playerState.update(client, self);
		}

		Set<UUID> missingPlayers = new HashSet<>(playerStates.keySet());
		missingPlayers.remove(self.getUuid());
		if (options.playerLowHealthEnableCustomSound) {
			for (PlayerEntity player : clientWorld.getPlayers()) {
				UUID uuid = player.getUuid();
				missingPlayers.remove(uuid);
				if (uuid.equals(self.getUuid())) {
					continue;
				}
				if (self.distanceTo(player) > MAX_DISTANCE) {
					continue;
				}
				PlayerState playerState = playerStates.computeIfAbsent(uuid, PlayerState::new);
				playerState.update(client, player);
			}
		}

		for (UUID uuid : missingPlayers) {
			playerStates.remove(uuid);
		}
	}

}
