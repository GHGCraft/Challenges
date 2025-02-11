package net.codingarea.challenges.plugin.challenges.custom.settings.sub.builder;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.management.menu.generator.MenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.custom.IParentCustomGenerator;
import org.bukkit.entity.Player;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public class EmptySubSettingsBuilder extends SubSettingsBuilder {

  public EmptySubSettingsBuilder() {
    super("none");
  }

  @Override
  public MenuGenerator getGenerator(Player player, IParentCustomGenerator parentGenerator, String title) {
    return null;
  }

  @Override
  public List<String> getDisplay(Map<String, String[]> activated) {
    return Lists.newLinkedList();
  }

  @Override
  public boolean hasSettings() {
    return false;
  }

}
