package com.nicknackgus.saveourselves.alarms;

import com.nicknackgus.saveourselves.Constants;
import com.nicknackgus.saveourselves.SaveOurSelvesClient;
import com.nicknackgus.saveourselves.options.Options;
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

public class LoopingAlarm implements ClientTickEvents.EndTick {

	public static final long LOOP_DURATION_MILLIS = 1000L;
	public static final long SECOND_NOTE_MILLIS = 300L;

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
			float maxHealth = player.getMaxHealth();
			float health = player.getHealth();
			if (maxHealth <= 0.0f ||
					100.0f * health / maxHealth <= SaveOurSelvesClient.options.selfLowHealthPercentage) {
				LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

				if (healthy) {
					loopStartTime = now;
				}
				healthy = false;

				long loopPartMillis = loopStartTime.until(now, ChronoUnit.MILLIS);
				if (loopPartMillis >= LOOP_DURATION_MILLIS) {
					lastLoopPartMillis = -1L;
					long loopsElapsed = loopPartMillis / LOOP_DURATION_MILLIS;
					loopStartTime = loopStartTime.plus(loopsElapsed * LOOP_DURATION_MILLIS,
							ChronoUnit.MILLIS);
					loopPartMillis %= LOOP_DURATION_MILLIS;
				}

				if (loopPartMillis >= SECOND_NOTE_MILLIS) {
					if (lastLoopPartMillis < SECOND_NOTE_MILLIS) {
						playNote(client, player, Constants.Note.A3);
						playNote(client, player, Constants.Note.AS3);
					}
				} else {
					if (lastLoopPartMillis < 0) {
						playNote(client, player, Constants.Note.G3);
					}
				}
				lastLoopPartMillis = loopPartMillis;
			} else {
				healthy = true;
			}
		}

		private void playNote(MinecraftClient client, PlayerEntity player, Constants.Note note) {
			float volume;
			if (isSelf) {
				volume = 0.01f * SaveOurSelvesClient.options.selfLowHealthVolume;
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

		if (options.selfLowHealthEnableLoop && client.player != null) {
			UUID uuid = client.player.getUuid();
			PlayerState playerState = playerStates.computeIfAbsent(uuid, PlayerState::new);
			playerState.isSelf = true;
			playerState.update(client, client.player);
		}
	}

}
