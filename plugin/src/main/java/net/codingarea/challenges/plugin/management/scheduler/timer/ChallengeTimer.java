package net.codingarea.challenges.plugin.management.scheduler.timer;

import javax.annotation.Nonnull;
import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.config.FileDocument;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.TimerMenuGenerator;
import net.codingarea.challenges.plugin.management.scheduler.policy.PlayerCountPolicy;
import net.codingarea.challenges.plugin.management.scheduler.policy.TimerPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public final class ChallengeTimer {

	private long time = 0;
	private boolean countingUp = true;
	private boolean paused = true;
	private boolean hidden = false;
	private boolean sentEmpty;

	private final TimerFormat format;
	private final String stoppedMessage, upMessage, downMessage;
	private final boolean specificStartSounds, defaultStartSound;

	public ChallengeTimer() {

		Document pluginConfig = Challenges.getInstance().getConfigDocument();
		specificStartSounds = pluginConfig.getBoolean("enable-specific-start-sounds");
		defaultStartSound = pluginConfig.getBoolean("enable-default-start-sounds");

		// Load format + messages
		Document timerConfig = pluginConfig.getDocument("timer");
		stoppedMessage = timerConfig.getString("stopped-message", "");
		upMessage = timerConfig.getString("count-up-message", "");
		downMessage = timerConfig.getString("count-down-message", "");

		Document formatConfig = timerConfig.getDocument("format");
		format = new TimerFormat(formatConfig);

		Challenges.getInstance().getScheduler().register(this);
	}

	public void enable() {
		updateTimeRule();
	}

	private void updateTimeRule() {
		for (World world : ChallengeAPI.getGameWorlds()) {
			world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, !paused);
		}
	}

	@ScheduledTask(ticks = 20, async = false, timerPolicy = TimerPolicy.ALWAYS, playerPolicy = PlayerCountPolicy.ALWAYS)
	public void onTimerSecond() {

		if (!paused) {
			if (countingUp) time++;
			else time--;

			if (time <= 0) {
				time = 0;
				countingUp = true;
				handleHitZero();
			}
		}

		updateActionbar();

	}

	@ScheduledTask(ticks = 20, timerPolicy = TimerPolicy.PAUSED)
	public void playPausedParticles() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getGameMode() == GameMode.SPECTATOR) continue;
			Location location = player.getLocation();
			if (location.getWorld() == null) continue;
			location.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 1);
		}
	}

	private void handleHitZero() {
		ChallengeAPI.endChallenge(ChallengeEndCause.TIMER_HIT_ZERO);
	}

	public void resume() {
		if (!paused) return;
		paused = false;

		updateActionbar();
		updateTimeRule();

		Challenges.getInstance().getScheduler().fireTimerStatusChange();
		Challenges.getInstance().getTitleManager().sendTimerStatusTitle(Message.forName("title-timer-started"));
		Challenges.getInstance().getServerManager().setNotFresh();
		Message.forName("timer-was-started").broadcast(Prefix.TIMER);

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getGameMode() != GameMode.CREATIVE)
				player.setGameMode(GameMode.SURVIVAL);
		}

		IGoal currentGoal = Challenges.getInstance().getChallengeManager().getCurrentGoal();
		if (currentGoal != null && specificStartSounds) {
			currentGoal.getStartSound().broadcast();
		} else if (defaultStartSound) {
			SoundSample.DRAGON_BREATH.broadcast();
		}

	}

	public void pause(boolean byPlayer) {
		if (paused) return;
		paused = true;

		updateActionbar();
		updateTimeRule();

		Challenges.getInstance().getScheduler().fireTimerStatusChange();
		if (byPlayer) {
			Challenges.getInstance().getTitleManager().sendTimerStatusTitle(Message.forName("title-timer-paused"));
			Message.forName("timer-was-paused").broadcast(Prefix.TIMER);
			SoundSample.BASS_OFF.broadcast();
		}
	}

	public void reset() {
		if (!countingUp) pause(true);
		time = 0;
		countingUp = true;
		updateActionbar();
	}

	public void updateActionbar() {
		if (sentEmpty && hidden) return;
		if (hidden) sentEmpty = true;
		String actionbar = hidden ? "" : getActionbar();

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
		}
	}

	@Nonnull
	private String getActionbar() {
		String message = !paused || (!countingUp && time > 0) ? (countingUp ? upMessage : downMessage) : stoppedMessage;
		String time = getFormattedTime();
		return message.replace("{time}", time);
	}

	public synchronized void loadSession() {
		FileDocument config = Challenges.getInstance().getConfigManager().getSessionConfig();
		time = config.getInt("timer.seconds");
		countingUp = config.getBoolean("timer.countingUp", true);
		hidden = config.getBoolean("timer.hidden", false);
	}

	public synchronized void saveSession(boolean async) {
		FileDocument config = Challenges.getInstance().getConfigManager().getSessionConfig();
		config.set("timer.seconds", time);
		config.set("timer.countingUp", countingUp);
		config.set("timer.hidden", hidden);
		config.save(async);
	}

	public void addSeconds(int amount) {
		time += amount;
		if (time < 0)
			time = 0;
		updateActionbar();
	}

	public void setSeconds(long seconds) {
		this.time = seconds;
		updateActionbar();
	}

	public void setCountingUp(boolean countingUp) {
		if (this.countingUp == countingUp) return;

		this.countingUp = countingUp;
		updateActionbar();
		TimerMenuGenerator menuGenerator = (TimerMenuGenerator) MenuType.TIMER.getMenuGenerator();
		menuGenerator.updateFirstPage();
		Message.forName("timer-mode-set-" + (countingUp ? "up" : "down")).broadcast(Prefix.TIMER);
		SoundSample.BASS_ON.broadcast();
	}

	public void setHidden(boolean hide) {
		this.sentEmpty = false;
		this.hidden = hide;
		updateActionbar();
	}

	@Nonnull
	public String getFormattedTime() {
		return format.format(time);
	}

	@Nonnull
	public TimerFormat getFormat() {
		return format;
	}

	public long getTime() {
		return time;
	}

	@Nonnull
	public TimerStatus getStatus() {
		return paused ? TimerStatus.PAUSED : TimerStatus.RUNNING;
	}

	public boolean isPaused() {
		return paused;
	}

	public boolean isStarted() {
		return !paused;
	}

	public boolean isCountingUp() {
		return countingUp;
	}

}
