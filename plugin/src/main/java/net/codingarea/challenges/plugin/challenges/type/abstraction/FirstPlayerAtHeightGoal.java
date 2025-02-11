package net.codingarea.challenges.plugin.challenges.type.abstraction;

import java.util.Collections;
import java.util.List;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.server.ChallengeEndCause;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public abstract class FirstPlayerAtHeightGoal extends SettingGoal {

  private int heightToGetTo;

  @Override
  protected void onEnable() {
    bossbar.setContent((bar, player) -> {
      bar.setTitle(Message.forName("bossbar-first-at-height-goal").asString(getHeightToGetTo()));
    });
    bossbar.show();
  }

  @Override
  protected void onDisable() {
    bossbar.hide();
  }

  @Override
  public void getWinnersOnEnd(@NotNull List<Player> winners) { }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onMove(PlayerMoveEvent event) {
    if (event.getTo() == null) return;
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getTo().getBlockY() == event.getFrom().getBlockY()) return;
    if (event.getTo().getBlockY() == heightToGetTo) {
      Message.forName("height-reached").broadcast(Prefix.CHALLENGES, NameHelper.getName(event.getPlayer()), getHeightToGetTo());
      ChallengeAPI.endChallenge(ChallengeEndCause.GOAL_REACHED, () -> Collections.singletonList(event.getPlayer()));
    }
  }

  public int getHeightToGetTo() {
    return heightToGetTo;
  }

  protected void setHeightToGetTo(int heightToGetTo) {
    this.heightToGetTo = heightToGetTo;
  }

}
