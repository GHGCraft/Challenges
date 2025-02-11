package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.AbstractChallengeTrigger;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public class BreakBlockTrigger extends AbstractChallengeTrigger {

  public BreakBlockTrigger(String name) {
    super(name, SubSettingsHelper.createBlockSettingsBuilder());
  }

  @Override
  public Material getMaterial() {
    return Material.GOLDEN_PICKAXE;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    createData()
        .entity(event.getPlayer())
        .event(event)
        .block(event.getBlock().getType())
        .execute();
  }

}
