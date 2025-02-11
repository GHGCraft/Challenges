package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.anweisen.utilities.common.annotations.DeprecatedSince;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeBossBar;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeScoreboard;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.0
 */
public abstract class AbstractChallenge implements IChallenge, Listener {

	protected static final Challenges plugin = Challenges.getInstance();

	private static final Map<Class<? extends AbstractChallenge>, AbstractChallenge> firstInstanceByClass = new HashMap<>();
	private static final boolean ignoreCreativePlayers, ignoreSpectatorPlayers;

	protected final MenuType menu;
	protected final ChallengeBossBar bossbar = new ChallengeBossBar();
	protected final ChallengeScoreboard scoreboard = new ChallengeScoreboard();

	private String name;

	private ItemStack cachedDisplayItem;

	static {
		Document ignoreDocument = Challenges.getInstance().getConfigDocument().getDocument("ignore-players");
		ignoreCreativePlayers = ignoreDocument.getBoolean("creative");
		ignoreSpectatorPlayers = ignoreDocument.getBoolean("spectator");
	}

	public AbstractChallenge(@Nonnull MenuType menu) {
		this.menu = menu;
		firstInstanceByClass.put(this.getClass(), this);
	}

	@Nonnull
	@Override
	public final MenuType getType() {
		return menu;
	}

	protected final void updateItems() {
		ChallengeHelper.updateItems(this);
	}

	@Nonnull
	@Override
	public ItemStack getDisplayItem() {
		if (cachedDisplayItem != null) return cachedDisplayItem.clone();
		cachedDisplayItem = createDisplayItem().build();
		return cachedDisplayItem.clone();
	}

	@Nonnull
	@Override
	public ItemStack getSettingsItem() {
		ItemBuilder item = createSettingsItem();
		String[] description = getSettingsDescription();
		if (description != null && isEnabled()) {
			item.appendLore(" ");
			item.appendLore(description);
		}

		return item.build();
	}

	@Nullable
	protected String[] getSettingsDescription() {
		return null;
	}

	@Nonnull
	public abstract ItemBuilder createDisplayItem();

	@Nonnull
	public abstract ItemBuilder createSettingsItem();

	@Nonnull
	@Override
	public String getUniqueName() {
		return name != null ? name : (name = getClass().getSimpleName().toLowerCase()
				.replace("setting", "")
				.replace("challenge", "")
				.replace("modifier", "")
				.replace("goal", "")
		);
	}

	@Override
	public void handleShutdown() {
	}

	@Override
	public void writeGameState(@Nonnull Document document) {
	}

	@Override
	public void loadGameState(@Nonnull Document document) {
	}

	@Override
	public void writeSettings(@Nonnull Document document) {
	}

	@Override
	public void loadSettings(@Nonnull Document document) {
	}

	@CheckReturnValue
	protected boolean shouldExecuteEffect() {
		return isEnabled() && ChallengeAPI.isStarted() && !ChallengeAPI.isWorldInUse();
	}

	/**
	 * @deprecated Use {@link ChallengeHelper#kill(Player)}
	 */
	@Deprecated
	@DeprecatedSince("2.1.0")
	public void kill(@Nonnull Player player) {
		ChallengeHelper.kill(player);
	}

	/**
	 * @deprecated Use {@link ChallengeHelper#kill(Player, int)}
	 */
	@Deprecated
	@DeprecatedSince("2.1.0")
	public void kill(@Nonnull Player player, int delay) {
		ChallengeHelper.kill(player, delay);

	}

	@Nonnull
	protected final Document getGameStateData() {
		return plugin.getConfigManager().getGameStateConfig().getDocument(getUniqueName());
	}

	@Nonnull
	protected final Document getPlayerData(@Nonnull UUID player) {
		return getGameStateData().getDocument("player").getDocument(player.toString());
	}

	@Nonnull
	protected final Document getPlayerData(@Nonnull Player player) {
		return getPlayerData(player.getUniqueId());
	}

	@Nonnull
	public static <C extends AbstractChallenge> C getFirstInstance(@Nonnull Class<C> classOfChallenge) {
		return classOfChallenge.cast(firstInstanceByClass.get(classOfChallenge));
	}

	public static void broadcast(@Nonnull Consumer<? super Player> action) {
		Bukkit.getOnlinePlayers().forEach(action);
	}

	public static void broadcastFiltered(@Nonnull Consumer<? super Player> action) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (ignorePlayer(player)) continue;
			action.accept(player);
		}
	}

	public static void broadcastIgnored(@Nonnull Consumer<? super Player> action) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!ignorePlayer(player)) continue;
			action.accept(player);
		}
	}

	@CheckReturnValue
	public static boolean ignorePlayer(@Nonnull Player player) {
		return ignoreGameMode(player.getGameMode());
	}

	@CheckReturnValue
	public static boolean ignoreGameMode(@Nonnull GameMode gameMode) {
		return (isIgnoreSpectatorPlayers() && gameMode == GameMode.SPECTATOR) || (isIgnoreCreativePlayers() && gameMode == GameMode.CREATIVE);
	}

	public static boolean isIgnoreCreativePlayers() {
		return ignoreCreativePlayers;
	}

	public static boolean isIgnoreSpectatorPlayers() {
		return ignoreSpectatorPlayers;
	}

}
