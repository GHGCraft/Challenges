package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.AbstractChallengeTrigger;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLevelChangeEvent;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public class LevelUpTrigger extends AbstractChallengeTrigger {

  public LevelUpTrigger(String name) {
    super(name);
  }

  @Override
  public Material getMaterial() {
    return Material.ENCHANTING_TABLE;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onLevelUp(PlayerLevelChangeEvent event) {
    if (event.getNewLevel() > event.getOldLevel()) {
      createData()
          .entity(event.getPlayer())
          .execute();
    }
  }

}
