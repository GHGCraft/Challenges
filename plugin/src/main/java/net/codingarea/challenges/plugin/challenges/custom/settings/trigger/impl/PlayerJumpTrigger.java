package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import javax.annotation.Nonnull;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.AbstractChallengeTrigger;
import net.codingarea.challenges.plugin.spigot.events.PlayerJumpEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public class PlayerJumpTrigger extends AbstractChallengeTrigger {

	public PlayerJumpTrigger(String name) {
		super(name);
	}

	@Override
	public Material getMaterial() {
		return Material.RABBIT_FOOT;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onJump(@Nonnull PlayerJumpEvent event) {
		createData().entity(event.getPlayer()).execute();
	}

}
