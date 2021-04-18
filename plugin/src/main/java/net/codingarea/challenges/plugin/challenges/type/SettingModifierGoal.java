package net.codingarea.challenges.plugin.challenges.type;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public abstract class SettingModifierGoal extends SettingModifier implements Goal {

	public SettingModifierGoal(@Nonnull MenuType menu) {
		super(menu);
	}

	public SettingModifierGoal(@Nonnull MenuType menu, int max) {
		super(menu, max);
	}

	public SettingModifierGoal(@Nonnull MenuType menu, int min, int max) {
		super(menu, min, max);
	}

	public SettingModifierGoal(@Nonnull MenuType menu, int min, int max, int defaultValue) {
		super(menu, min, max, defaultValue);
	}

	@Override
	public final void setEnabled(boolean enabled) {
		if (isEnabled() == enabled) return;
		GoalHelper.handleSetEnabled(this, enabled);
		super.setEnabled(enabled);
	}

	@Nonnull
	@Override
	public SoundSample getStartSound() {
		return SoundSample.DRAGON_BREATH;
	}

	@Nullable
	@Override
	public SoundSample getWinSound() {
		return SoundSample.WIN;
	}

	@Override
	public void handleClick(@Nonnull ChallengeMenuClickInfo info) {
		if (info.isUpperItemClick() && isEnabled()) {
			ChallengeHelper.handleModifierClick(info, this);
		} else {
			setEnabled(!isEnabled());
			SoundSample.playEnablingSound(info.getPlayer(), isEnabled());
			playStatusUpdateTitle();
		}
	}

}
