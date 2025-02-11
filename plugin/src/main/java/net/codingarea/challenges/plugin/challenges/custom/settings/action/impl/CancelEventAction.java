package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import java.util.Map;
import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeExecutionData;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.AbstractChallengeAction;
import org.bukkit.Material;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public class CancelEventAction extends AbstractChallengeAction {

  public static boolean inCanceling;

  public CancelEventAction(String name) {
    super(name);
  }


  @Override
  public Material getMaterial() {
    return Material.BARRIER;
  }

  @Override
  public void execute(ChallengeExecutionData executionData, Map<String, String[]> subActions) {
    inCanceling = true;
  }

  public static void onPreTrigger() {
    inCanceling = false;
  }

  public static boolean shouldCancel() {
    if (inCanceling) {
      inCanceling = false;
      return true;
    }
    return false;
  }

}
