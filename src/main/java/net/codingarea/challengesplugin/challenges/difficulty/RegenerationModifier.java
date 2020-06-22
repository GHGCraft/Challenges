package net.codingarea.challengesplugin.challenges.difficulty;

import net.codingarea.challengesplugin.Challenges;
import net.codingarea.challengesplugin.challengetypes.Modifier;
import net.codingarea.challengesplugin.manager.events.ChallengeEditEvent;
import net.codingarea.challengesplugin.manager.lang.ItemTranslation;
import net.codingarea.challengesplugin.manager.menu.MenuType;
import net.codingarea.challengesplugin.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

/**
 * @author anweisen & Dominik
 * Challenges developed on 05-30-2020
 * https://github.com/anweisen
 * https://github.com/Traolian
 */

public class RegenerationModifier extends Modifier implements Listener {

    @SuppressWarnings("depraction")
    public RegenerationModifier() {
        this.menu = MenuType.DIFFICULTY;
        this.value = 2;
        this.maxValue = 3;
        this.minValue = 1;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemBuilder(Material.POTION, ItemTranslation.REGENERATION).hideAttributes().getItem();
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onMenuClick(ChallengeEditEvent event) { }

    @Override
    public ItemStack getActivationItem() {
        if (this.value == 1) {
            return Challenges.getInstance().getItemManager().getNotActivatedItem();
        } else if (this.value == 2) {
            return new ItemBuilder(Material.LIME_DYE, "§aActivated").getItem();
        } else {
            return new ItemBuilder(Material.ORANGE_DYE, "§6Not natural").getItem();
        }
    }

    @EventHandler
    public void handle(EntityRegainHealthEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (value == 1) {
            event.setAmount(0);
            event.setCancelled(true);
            return;
        }

        if (value == 3) {
            if (event.getRegainReason() == RegainReason.SATIATED || event.getRegainReason() == RegainReason.REGEN) {
                event.setAmount(0);
                event.setCancelled(true);
            }
        }

    }

}
