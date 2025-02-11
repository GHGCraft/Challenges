package net.codingarea.challenges.plugin.spigot.command;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import org.bukkit.command.CommandSender;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class ResetCommand implements SenderCommand, Completer {

	private final boolean confirmReset, seedResetCommand;

	public ResetCommand() {
		Document pluginConfig = Challenges.getInstance().getConfigDocument();
		confirmReset = pluginConfig.getBoolean("confirm-reset");

		Document seedResetConfig = pluginConfig.getDocument("custom-seed");
		seedResetCommand = seedResetConfig.getBoolean("command");
	}

	@Override
	public void onCommand(@Nonnull CommandSender sender, @Nonnull String[] args) {

		if (!Challenges.getInstance().getWorldManager().isEnableFreshReset() && ChallengeAPI.isFresh()) {
			Message.forName("no-fresh-reset").send(sender, Prefix.CHALLENGES);
			SoundSample.BASS_OFF.playIfPlayer(sender);
			return;
		}

		if (confirmReset && (args.length < 1 || !args[0].equalsIgnoreCase("confirm")) || (args.length > 0 && !args[0].equalsIgnoreCase("confirm"))) {
			if (args.length > 0 && args[0].equalsIgnoreCase("settings")) {
				Challenges.getInstance().getChallengeManager().restoreDefaults();
				Message.forName("player-config-reset").broadcast(Prefix.CHALLENGES);
				return;
			} else if (args.length > 0 && args[0].equalsIgnoreCase("custom_challenges")) {
				Challenges.getInstance().getCustomChallengesLoader().resetChallenges();
				Message.forName("player-custom_challenges-reset").broadcast(Prefix.CHALLENGES);
				return;
			}
			if (confirmReset) {
				Message.forName("confirm-reset").send(sender, Prefix.CHALLENGES, "reset confirm");
				return;
			}
		}

		Long seed = null;

		if (seedResetCommand) {
			int index = confirmReset ? 1 : 0;
			if (args.length > index) {
				String seedInput = args[index];
				try {
					seed = Long.parseLong(seedInput);
				} catch (NumberFormatException exception) {
				}
			}
		}

		Challenges.getInstance().getWorldManager().prepareWorldReset(sender, seed);

	}

	@Override
	public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull String[] args) {
		if (confirmReset && args.length == 1) return Utils.filterRecommendations(
				args[0], "confirm", "settings", "custom_challenges");
		if (seedResetCommand && ((confirmReset && args.length == 2) || args.length == 1)) {
			return args.length == 1 ?
					Utils.filterRecommendations(args[args.length-1], "[seed]", "settings", "custom_challenges") :
					args[0].equalsIgnoreCase("confirm") ? Collections.singletonList("[seed]") : Lists
							.newLinkedList();
		}

		return Lists.newLinkedList();
	}

}
