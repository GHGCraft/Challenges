package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import java.util.Map;
import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.AbstractChallengeTargetAction;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.1
 */
public class SpawnEntityAction extends AbstractChallengeTargetAction {

	public SpawnEntityAction(String name) {
		super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(false).addChild(SubSettingsHelper.createEntityTypeSettingsBuilder(false, false)));
	}

	@Override
	public Material getMaterial() {
		return Material.ZOMBIE_SPAWN_EGG;
	}

	@Override
	public void executeFor(Entity entity, Map<String, String[]> subActions) {
		if (entity.getLocation().getWorld() == null) return;

		String[] args = subActions.get(SubSettingsHelper.ENTITY_TYPE);

		for (String arg : args) {

			try {
				EntityType type = EntityType.valueOf(arg);
				World world = entity.getLocation().getWorld();
				world.spawnEntity(entity.getLocation(), type);

			} catch (Exception exception) {
				Logger.error(exception);
			}

		}


	}

}
