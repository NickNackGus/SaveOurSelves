package com.nicknackgus.saveourselves.alarms;

import com.nicknackgus.saveourselves.Constants;
import com.nicknackgus.saveourselves.SaveOurSelvesClient;
import com.nicknackgus.saveourselves.options.Options;
import com.nicknackgus.saveourselves.utils.PlayerUtils;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
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
import net.minecraft.util.Identifier;

public class HeartbeatAlarm implements ClientTickEvents.EndTick {

	public static final SoundEvent SELF_HEARTBEAT_SOUND
			= new SoundEvent(new Identifier("minecraft:block.note_block.basedrum"));
	public static final SoundEvent PLAYER_HEARTBEAT_SOUND
			= new SoundEvent(new Identifier("minecraft:block.note_block.basedrum"));
	public static final double SECOND_HEARTBEAT_LOOP_PERCENT = 0.3;
	public static final double MAX_DISTANCE = 32.0;

	protected static class PlayerState {
		UUID playerId;
		public boolean isSelf = false;
		boolean healthy = true;
		LocalDateTime loopStartTime = LocalDateTime.now(ZoneOffset.UTC);
		long lastLoopPartMillis = -1L;

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
			if (healthPercent > healthThreshold) {
				healthy = true;
			} else {
				LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

				if (healthy) {
					loopStartTime = now;
				}
				healthy = false;

				int bpmLow;
				int bpmCritical;
				if (isSelf) {
					bpmLow = SaveOurSelvesClient.options.selfAlarmHeartbeatHealthLow;
					bpmCritical = SaveOurSelvesClient.options.selfAlarmHeartbeatHealthCritical;
				} else {
					bpmLow = SaveOurSelvesClient.options.playerAlarmHeartbeatHealthLow;
					bpmCritical = SaveOurSelvesClient.options.playerAlarmHeartbeatHealthCritical;
				}

				if (bpmLow == 0) {
					return;
				}

				float bpm = bpmLow + (bpmCritical - bpmLow) * 0.02F * (50.0F - healthPercent);
				if (bpm == 0.0F) {
					return;
				}
				long loopDurationMillis = (long) (60000.0F / bpm);
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

				long secondHeartbeatMillis = (long) (loopDurationMillis * SECOND_HEARTBEAT_LOOP_PERCENT);
				if (loopPartMillis >= secondHeartbeatMillis) {
					if (lastLoopPartMillis < secondHeartbeatMillis) {
						playNote(client, player, Constants.Note.A3);
						playNote(client, player, Constants.Note.AS3);
					}
				} else {
					if (lastLoopPartMillis < 0) {
						playNote(client, player, Constants.Note.G3);
					}
				}
				lastLoopPartMillis = loopPartMillis;
			}
		}

		private void playNote(MinecraftClient client, PlayerEntity player, Constants.Note note) {
			SoundEvent sound;
			float volume;
			if (isSelf) {
				sound = SELF_HEARTBEAT_SOUND;
				volume = 0.01f * SaveOurSelvesClient.options.selfLowHealthHeartbeatVolume;
			} else {
				sound = PLAYER_HEARTBEAT_SOUND;
				volume = 0.01f * SaveOurSelvesClient.options.playerLowHealthHeartbeatVolume;
			}

			client.getSoundManager().play(
					new EntityTrackingSoundInstance(sound,
							SoundCategory.MASTER,
							volume,
							note.pitch,
							player));
		}
	}

	static ConcurrentSkipListMap<UUID, PlayerState> playerStates = new ConcurrentSkipListMap<>();

	@Override
	public void onEndTick(MinecraftClient client) {
		Options options = SaveOurSelvesClient.options;

		PlayerEntity self = client.player;
		if (self == null) {
			return;
		}

		if (options.selfLowHealthEnableHeartbeat) {
			UUID uuid = self.getUuid();
			PlayerState playerState = playerStates.computeIfAbsent(uuid, PlayerState::new);
			playerState.isSelf = true;
			playerState.update(client, self);
		}

		Set<UUID> missingPlayers = new HashSet<>(playerStates.keySet());
		ClientWorld clientWorld = client.world;
		if (options.playerLowHealthEnableHeartbeat && clientWorld != null) {
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

			for (UUID uuid : missingPlayers) {
				playerStates.remove(uuid);
			}
		}
	}

}
