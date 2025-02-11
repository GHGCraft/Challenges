package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import java.util.Map;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.AbstractChallengeTargetAction;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public class PotionEffectAction extends AbstractChallengeTargetAction {

  public PotionEffectAction(String name) {
    super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(true).addChild(
        SubSettingsHelper.createPotionSettingsBuilder(true, true)));
  }

  @Override
  public void executeFor(Entity entity, Map<String, String[]> subActions) {
    if (entity instanceof LivingEntity) {
      LivingEntity livingEntity = (LivingEntity) entity;
      try {

        PotionEffectType effectType = PotionEffectType.getByName(subActions.get("potion_type")[0]);
        if (effectType == null) return;
        PotionEffect effect = effectType.createEffect(Integer.parseInt(subActions.get("length")[0]) * 20 + 1,
            Integer.parseInt(subActions.get("amplifier")[0]));

        livingEntity.addPotionEffect(effect);
      } catch (Exception exception) {
        Challenges.getInstance().getLogger().severe("Error while adding potion effect to player");
        exception.printStackTrace();
      }
    }
  }

  @Override
  public Material getMaterial() {
    return Material.POTION;
  }

}
