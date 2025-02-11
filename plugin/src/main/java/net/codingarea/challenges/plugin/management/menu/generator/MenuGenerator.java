package net.codingarea.challenges.plugin.management.menu.generator;

import java.util.List;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import net.anweisen.utilities.bukkit.utils.menu.MenuPosition;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.position.GeneratorMenuPosition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public abstract class MenuGenerator {

	private MenuType menuType;

	public abstract void generateInventories();
	public abstract List<Inventory> getInventories();
	public abstract MenuPosition getMenuPosition(@Nonnegative int page);

	public boolean hasInventoryOpen(Player player) {
		MenuPosition menuPosition = MenuPosition.get(player);
		return menuPosition instanceof GeneratorMenuPosition && player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING && ((GeneratorMenuPosition) menuPosition).getGenerator() == this;
	}

	public int getPage(Player player) {
		MenuPosition menuPosition = MenuPosition.get(player);
		if (menuPosition instanceof GeneratorMenuPosition) return ((GeneratorMenuPosition) menuPosition).getPage();
		return 0;
	}

	public void reopenInventoryForPlayers() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (hasInventoryOpen(player)) {
				open(player, getPage(player));
			}
		}
	}

	public void open(@Nonnull Player player, @Nonnegative int page) {
		List<Inventory> inventories = getInventories();
		if (inventories == null || inventories.isEmpty()) generateInventories();
		if (inventories == null || inventories.isEmpty()) return;
		if (page >= inventories.size()) page = inventories.size() - 1;
		Inventory inventory = inventories.get(page);
		MenuPosition.set(player, getMenuPosition(page));
		player.openInventory(inventory);
	}

	public MenuType getMenuType() {
		return menuType;
	}

	// ONLY MODIFY IF YOU KNOW WHAT YOU ARE DOING
	public void setMenuType(MenuType menuType) {
		this.menuType = menuType;
	}

}
