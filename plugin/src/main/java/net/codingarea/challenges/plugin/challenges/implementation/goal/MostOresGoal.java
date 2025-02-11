package net.codingarea.challenges.plugin.challenges.implementation.goal;

import javax.annotation.Nonnull;
import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.PointsGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.1
 */
@Since("2.1.1")
public class MostOresGoal extends PointsGoal {

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.COAL_ORE, Message.forName("item-most-ores-goal"));
	}

	private int getPointsForOre(Material material) {
		switch (material) {
			case EMERALD_ORE:
			case DEEPSLATE_EMERALD_ORE:
				return 15;
			case 	DIAMOND_ORE:
			case DEEPSLATE_DIAMOND_ORE:
				return 10;
			case LAPIS_ORE:
			case DEEPSLATE_LAPIS_ORE:
				return 8;
			case GOLD_ORE:
			case DEEPSLATE_GOLD_ORE:
				return 6;
			case IRON_ORE:
			case DEEPSLATE_IRON_ORE:
				return 4;
			case COAL_ORE:
			case DEEPSLATE_COAL_ORE:
			case REDSTONE_ORE:
			case DEEPSLATE_REDSTONE_ORE:
				return 2;
		}
		return 0;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onUpdate(@Nonnull BlockBreakEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		int points = getPointsForOre(event.getBlock().getType());
		if (points > 0) {
			Message.forName("most-ores-points").send(event.getPlayer(), Prefix.CHALLENGES, "+" + points);
			SoundSample.PLING.play(event.getPlayer());
			addPoints(event.getPlayer().getUniqueId(), points);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onUpdate(@Nonnull BlockPlaceEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		int points = getPointsForOre(event.getBlock().getType());
		if (points > 0) {
			SoundSample.BASS_OFF.play(event.getPlayer());
			Message.forName("most-ores-points").send(event.getPlayer(), Prefix.CHALLENGES, "-" + points);
			removePoints(event.getPlayer().getUniqueId(), points);
		}
	}

}
