package net.codingarea.challenges.plugin.challenges.implementation.challenge;

import net.anweisen.utilities.common.misc.StringUtils;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 1.0
 */
public class FoodOnceChallenge extends SettingModifier {

	public FoodOnceChallenge() {
		super(MenuType.CHALLENGES, 2);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.COOKED_BEEF, Message.forName("item-food-once-challenge"));
	}

	@Nonnull
	@Override
	public ItemBuilder createSettingsItem() {
		switch (getValue()) {
			case 1: return DefaultItem.create(Material.PLAYER_HEAD, "§6Player");
			case 2: return DefaultItem.create(Material.ENDER_CHEST, "§5Everyone");
			default: return super.createSettingsItem();
		}
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChangeChallengeValueTitle(this, getValue() == 1 ? "§6Player" : "§5Everyone");
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerItemConsume(@Nonnull PlayerItemConsumeEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;

		Material type = event.getItem().getType();
		if (hasEaten(event.getPlayer(), type)) {
			Message.forName("food-once-failed").broadcast(Prefix.CHALLENGES, NameHelper.getName(event.getPlayer()), StringUtils.getEnumName(type));
			kill(event.getPlayer(), 1);
		} else {
			addFood(event.getPlayer(), type);
			if (teamFoodsActivated()) {
				Message.forName("food-once-new-food-team").broadcast(Prefix.CHALLENGES, NameHelper.getName(event.getPlayer()), StringUtils.getEnumName(type));
			} else {
				Message.forName("food-once-new-food").send(event.getPlayer(), Prefix.CHALLENGES, NameHelper.getName(event.getPlayer()), StringUtils.getEnumName(type));

			}
		}

	}

	private void addFood(Player player, Material type) {
		if (teamFoodsActivated()) {
			addTeamFood(type);
		} else {
			addPlayerFood(player, type);
		}
	}

	private void addPlayerFood(@Nonnull Player player, @Nonnull Material material) {
		List<Material> foods = getPlayerData(player).getEnumList("foods", Material.class);
		foods.add(material);
		getPlayerData(player).set("foods", foods);
	}

	private boolean hasEaten(@Nonnull Player player, @Nonnull Material material) {
		if (teamFoodsActivated()) {
			return hasBeenEatenByTeam(material);
		}

		return getPlayerData(player).getEnumList("foods", Material.class).contains(material);
	}

	private void addTeamFood(@Nonnull Material material) {
		List<Material> foods = getGameStateData().getEnumList("foods", Material.class);
		foods.add(material);
		getGameStateData().set("foods", foods);
	}

	private boolean hasBeenEatenByTeam(@Nonnull Material material) {
		return getGameStateData().getEnumList("foods", Material.class).contains(material);
	}

	private boolean teamFoodsActivated() {
		return getValue() == 2;
	}

}