package com.nicknackgus.saveourselves.alarms;

import com.nicknackgus.saveourselves.Constants;
import com.nicknackgus.saveourselves.SaveOurSelvesClient;
import com.nicknackgus.saveourselves.options.Options;
import com.nicknackgus.saveourselves.utils.PlayerUtils;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class HeartbeatAlarm implements ClientTickEvents.EndTick {

	public static final double SECOND_HEARTBEAT_LOOP_PERCENT = 0.3;

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
			if (healthPercent > SaveOurSelvesClient.options.selfLowHealthPercentage) {
				healthy = true;
			} else {
				LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

				if (healthy) {
					loopStartTime = now;
				}
				healthy = false;

				int bpmLow = SaveOurSelvesClient.options.selfAlarmHeartbeatHealthLow;
				int bpmCritical = SaveOurSelvesClient.options.selfAlarmHeartbeatHealthCritical;

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
			float volume;
			if (isSelf) {
				volume = 0.01f * SaveOurSelvesClient.options.selfLowHealthHeartbeatVolume;
			} else {
				volume = 0.0f;
			}

			Vec3d eyePos = player.getEyePos();

			client.getSoundManager().play(
					new PositionedSoundInstance(new Identifier("minecraft:block.note_block.basedrum"),
							SoundCategory.MASTER,
							volume,
							note.pitch,
							false,
							0,
							SoundInstance.AttenuationType.NONE,
							isSelf ? 0.0F : eyePos.getX(),
							isSelf ? 0.0F : eyePos.getY(),
							isSelf ? 0.0F : eyePos.getZ(),
							isSelf));
		}
	}

	static ConcurrentSkipListMap<UUID, PlayerState> playerStates = new ConcurrentSkipListMap<>();

	@Override
	public void onEndTick(MinecraftClient client) {
		Options options = SaveOurSelvesClient.options;

		if (options.selfLowHealthEnableHeartbeat && client.player != null) {
			UUID uuid = client.player.getUuid();
			PlayerState playerState = playerStates.computeIfAbsent(uuid, PlayerState::new);
			playerState.isSelf = true;
			playerState.update(client, client.player);
		}
	}

}
