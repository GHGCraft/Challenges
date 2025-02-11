package net.codingarea.challenges.plugin.management.menu.generator.implementation.custom;

import java.util.LinkedHashMap;
import net.codingarea.challenges.plugin.management.menu.generator.ChooseItemGenerator;
import net.codingarea.challenges.plugin.utils.misc.MapUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public class SubSettingChooseMenuGenerator extends ChooseItemGenerator {

	private final IParentCustomGenerator parent;
	private final String key;
	private final String title;

	public SubSettingChooseMenuGenerator(String key, IParentCustomGenerator parent, LinkedHashMap<String, ItemStack> map, String title) {
		super(map);
		this.key = key;
		this.title = title;
		this.parent = parent;
	}

	@Override
	public String[] getSubTitles(int page) {
		return new String[]{ title };
	}

	@Override
	public void onItemClick(Player player, String itemKey) {
		parent.accept(player, null, MapUtils.createStringArrayMap(key, itemKey));
	}

	@Override
	public void onBackToMenuItemClick(Player player) {
		parent.decline(player);
	}

}
