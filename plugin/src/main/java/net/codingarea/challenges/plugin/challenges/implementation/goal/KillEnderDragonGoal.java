package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.challenges.type.KillEntityGoal;
import net.codingarea.challenges.plugin.language.Message;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 1.0
 */
public class KillEnderDragonGoal extends KillEntityGoal {

	public KillEnderDragonGoal() {
		super(EntityType.ENDER_DRAGON, Environment.THE_END, true);
		setOneWinner(false);
	}

	@Nullable
	@Override
	public SoundSample getWinSound() {
		return null;
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.DRAGON_EGG, Message.forName("item-dragon-goal"));
	}

}
