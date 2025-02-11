package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.AbstractChallengeTrigger;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public class MoveCameraTrigger extends AbstractChallengeTrigger {

  public MoveCameraTrigger(String name) {
    super(name);
  }

  @Override
  public Material getMaterial() {
    return Material.COMPASS;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onMove(PlayerMoveEvent event) {
    if (event.getTo() == null) return;
    if (event.getFrom().getDirection().equals(event.getTo().getDirection())) return;
    createData()
        .entity(event.getPlayer())
        .event(event)
        .execute();
  }

}
