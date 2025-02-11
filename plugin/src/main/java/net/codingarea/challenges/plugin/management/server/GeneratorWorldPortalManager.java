package net.codingarea.challenges.plugin.management.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.management.challenges.entities.GamestateSaveable;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Manages the portal locations of custom worlds
 *
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public class GeneratorWorldPortalManager implements GamestateSaveable {

  public Map<UUID, Location> lastWorldLocations;

  public GeneratorWorldPortalManager() {
    lastWorldLocations = new HashMap<>();
    Challenges.getInstance().getChallengeManager().registerGameStateSaver(this);
  }

  @Nullable
  public Location getAndRemoveLastWorld(@Nonnull Player player) {
    return lastWorldLocations.remove(player.getUniqueId());
  }

  public void setLastLocation(@Nonnull Player player, @Nonnull Location location) {
    lastWorldLocations.put(player.getUniqueId(), location);
  }

  public boolean isCustomWorld(@Nonnull String name) {
    return Challenges.getInstance().getGameWorldStorage().getCustomGeneratedGameWorlds().contains(name);
  }

  @Override
  public String getUniqueName() {
    return getClass().getSimpleName().toLowerCase();
  }

  @Override
  public void writeGameState(@NotNull Document document) {
    for (Entry<UUID, Location> entry : lastWorldLocations.entrySet()) {
      UUID uuid = entry.getKey();
      Location location = entry.getValue();
      document.set(uuid.toString(), location);
    }
  }

  @Override
  public void loadGameState(@NotNull Document document) {
    for (String key : document.keys()) {
      try {
        Location location = document.getInstance(key, Location.class);
        UUID uuid = UUID.fromString(key);
        lastWorldLocations.put(uuid, location);
      } catch (Exception exception) {
        Challenges.getInstance().getLogger().error("Couldn't load last location of: " + key);
        exception.printStackTrace();
      }
    }
  }

}
