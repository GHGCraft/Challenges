package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeExecutionData;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.AbstractChallengePlayerTargetAction;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.1
 */
public class WinChallengeAction extends AbstractChallengePlayerTargetAction {

	private final List<Player> winner = Lists.newLinkedList();

	public WinChallengeAction(String name) {
		super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(false, true));
	}

	@Override
	public Material getMaterial() {
		return Material.GOLDEN_HELMET;
	}

	@Override
	public void execute(ChallengeExecutionData executionData, Map<String, String[]> subActions) {
		super.execute(executionData, subActions);
		ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED, () -> winner);
		winner.clear();
	}

	@Override
	public void executeForPlayer(Player player, Map<String, String[]> subActions) {
		winner.add(player);
	}

}
