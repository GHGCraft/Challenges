package net.codingarea.challenges.plugin.challenges.custom.settings.trigger;

import java.util.LinkedHashMap;
import java.util.function.Supplier;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.AbstractChallengeSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public abstract class AbstractChallengeTrigger extends AbstractChallengeSetting implements
    IChallengeTrigger {

  public AbstractChallengeTrigger(String name,
      SubSettingsBuilder subSettingsBuilder) {
    super(name, subSettingsBuilder);
  }

  public AbstractChallengeTrigger(String name) {
    super(name);
  }

  public AbstractChallengeTrigger(String name, Supplier<SubSettingsBuilder> builderSupplier) {
    super(name, builderSupplier);
  }

  @Override
  public final String getMessage() {
    return "item-custom-trigger-" + getMessageSuffix();
  }

  public static LinkedHashMap<String, ItemStack> getMenuItems() {
    LinkedHashMap<String, ItemStack> map = new LinkedHashMap<>();

    for (AbstractChallengeTrigger value : Challenges.getInstance().getCustomSettingsLoader().getTriggers().values()) {
      map.put(value.getName(), new ItemBuilder(value.getMaterial(), Message.forName(value.getMessage())).hideAttributes().build());
    }

    return map;
  }

}
