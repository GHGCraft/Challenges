package net.codingarea.challenges.plugin.management.server;

import net.anweisen.utilities.commons.config.Document;
import net.anweisen.utilities.commons.config.document.wrapper.FileDocumentWrapper;
import net.anweisen.utilities.commons.misc.FileUtils;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.language.Message;
import net.codingarea.challenges.plugin.utils.logging.Logger;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public final class WorldManager {

	public static class WorldSettings {

		private boolean placeBlocks = false;
		private boolean destroyBlocks = false;
		private boolean dropItems = false;
		private boolean pickupItems = false;

		public void setDestroyBlocks(boolean destroyBlocks) {
			this.destroyBlocks = destroyBlocks;
		}

		public void setPlaceBlocks(boolean placeBlocks) {
			this.placeBlocks = placeBlocks;
		}

		public void setDropItems(boolean dropItems) {
			this.dropItems = dropItems;
		}

		public void setPickupItems(boolean pickupItems) {
			this.pickupItems = pickupItems;
		}

		public boolean isDestroyBlocks() {
			return destroyBlocks;
		}

		public boolean isPlaceBlocks() {
			return placeBlocks;
		}

		public boolean isDropItems() {
			return dropItems;
		}

		public boolean isPickupItems() {
			return pickupItems;
		}

	}

	private static final String customSeedWorldPrefix = "pregenerated_";

	private boolean shutdownBecauseOfReset = false;

	private final boolean restartOnReset;
	private final boolean enableFreshReset;
	private final boolean useCustomSeed;
	private final long customSeed;
	private final String levelName;
	private final String[] worlds;

	private WorldSettings settings = new WorldSettings();
	private World world;
	private boolean worldIsInUse;

	public WorldManager() {
		Document pluginConfig = Challenges.getInstance().getConfigDocument();
		restartOnReset = pluginConfig.getBoolean("restart-on-reset");
		enableFreshReset = pluginConfig.getBoolean("enable-fresh-reset");

		Document seedConfig = pluginConfig.getDocument("custom-seed");
		useCustomSeed = seedConfig.getBoolean("enabled");
		customSeed = seedConfig.getLong("seed");

		Document sessionConfig = Challenges.getInstance().getConfigManager().getSessionConfig();
		levelName = sessionConfig.getString("level-name", "world");
		worlds = new String[] {
			levelName,
			levelName + "_nether",
			levelName + "_the_end"
		};
	}

	public void load()  {
		executeWorldResetIfNecessary();
	}

	public void enable() {
		loadExtraWorld();
	}

	public void prepareWorldReset(@Nullable CommandSender requestedBy) {

		shutdownBecauseOfReset = true;
		ChallengeAPI.pauseTimer(false);

		// Stop all tasks to prevent them from overwriting configs
		Challenges.getInstance().getScheduler().stop();

		resetConfigs();

		String requester = requestedBy instanceof Player ? NameHelper.getName((Player) requestedBy) : "§4§lConsole";
		String kickMessage = Message.forName("server-reset").asString(requester);
		Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(kickMessage));

		if (useCustomSeed) generateCustomSeedWorlds();

		Bukkit.getScheduler().runTaskLater(Challenges.getInstance(), this::stopServerNow, 3);

	}

	private void generateCustomSeedWorlds() {

		Logger.debug("Generating custom seed worlds with seed " + customSeed);
		for (String name : worlds) {

			World world = Bukkit.getWorld(name);
			if (world == null) {
				Logger.error("Could not find world {}", name);
				continue;
			}

			String newWorldName = customSeedWorldPrefix + name;
			File folder = new File(newWorldName);
			if (folder.exists()) FileUtils.deleteWorldFolder(folder);

			WorldCreator creator = new WorldCreator(newWorldName).seed(customSeed).environment(world.getEnvironment());
			creator.createWorld();

			Logger.debug("Created custom seed world {}", newWorldName);

		}

	}

	private void resetConfigs() {

		FileDocumentWrapper sessionConfig = Challenges.getInstance().getConfigManager().getSessionConfig();
		sessionConfig.clear();
		sessionConfig.set("reset", true);
		try {
			sessionConfig.set("level-name", Bukkit.getWorlds().get(0).getName());
		} catch (Exception ex) {
		}
		sessionConfig.save();

		FileDocumentWrapper gamestateConfig = Challenges.getInstance().getConfigManager().getGameStateConfig();
		gamestateConfig.clear();
		gamestateConfig.save();

	}

	private void loadExtraWorld() {
		if (!Challenges.getInstance().isReload())
			deleteWorld("challenges-extra");

		world = new WorldCreator("challenges-extra").type(WorldType.FLAT).generateStructures(false).createWorld();
		if (world == null) return;
		world.setSpawnFlags(false, false);
		setGameRule("doMobSpawning", false);
		setGameRule("doTraderSpawning", false);
		setGameRule("doWeatherCycle", false);
		setGameRule("doDaylightCycle", false);
		setGameRule("disableRaids", false);
		setGameRule("mobGriefing", false);

		teleportPlayersOutOfExtraWorld();
	}

	private void teleportPlayersOutOfExtraWorld() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld() != world) continue;

			Location location = player.getBedSpawnLocation();
			if (location == null) location = Bukkit.getWorld(levelName).getSpawnLocation();

			player.teleport(location);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void setGameRule(@Nonnull String name, @Nonnull T value) {
		GameRule<T> gamerule = (GameRule<T>) GameRule.getByName(name);
		if (gamerule == null) return;
		world.setGameRule(gamerule, value);
	}

	private void executeWorldResetIfNecessary() {
		if (Challenges.getInstance().getConfigManager().getSessionConfig().getBoolean("reset"))
			executeWorldReset();
	}

	public void executeWorldReset() {

		Logger.info("Deleting worlds..");

		for (String world : worlds) {
			deleteWorld(world);
			if (useCustomSeed)
				copyPreGeneratedWorld(world);
		}

		FileDocumentWrapper sessionConfig = Challenges.getInstance().getConfigManager().getSessionConfig();
		sessionConfig.set("reset", false);
		sessionConfig.save();

	}

	private void deleteWorld(@Nonnull String name) {
		File folder = new File(name);
		FileUtils.deleteWorldFolder(folder);
		Logger.info("Deleted world {}", name);
	}

	private void copyPreGeneratedWorld(@Nonnull String name) {
		File source = new File(customSeedWorldPrefix + name);
		if (!source.exists() || !source.isDirectory()) {
			Logger.warn("Custom seed world '{}' does not exist!", name);
			return;
		}

		File target = new File(name);
		try {
			copy(source, target);
			Logger.debug("Copied pre generated custom seed world {}", name);
		} catch (IOException ex) {
			Logger.error("Unable to copy pre generated custom seed world {}", name, ex);
		}
	}

	public void copy(@Nonnull File source, @Nonnull File target) throws IOException {
		if (source.isDirectory()) {
			copyDirectory(source, target);
		} else {
			copyFile(source, target);
		}
	}

	private void copyDirectory(@Nonnull File source, @Nonnull File target) throws IOException {
		if (!target.exists())
			target.mkdir();
		for (String child : source.list()) {
			if ("session.lock".equals(child)) continue;
			copy(new File(source, child), new File(target, child));
		}
	}

	private void copyFile(@Nonnull File source, @Nonnull File target) throws IOException {
		try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(target)) {
			byte[] buf = new byte[1024];
			int length;
			while ((length = in.read(buf)) > 0)
				out.write(buf, 0, length);
		}
	}

	private void stopServerNow() {
		if (!restartOnReset) {
			Bukkit.shutdown();
			return;
		}

		try {
			Bukkit.spigot().restart();
		} catch (NoSuchMethodError ex) {
			Bukkit.shutdown();
		}
	}

	public boolean isEnableFreshReset() {
		return enableFreshReset;
	}

	public boolean isShutdownBecauseOfReset() {
		return shutdownBecauseOfReset;
	}

	public boolean isWorldInUse() {
		return worldIsInUse;
	}

	public void setWorldIsInUse(boolean worldIsInUse) {
		if (!worldIsInUse) settings = new WorldSettings();
		this.worldIsInUse = worldIsInUse;
	}

	@Nonnull
	public World getExtraWorld() {
		return world;
	}

	@Nonnull
	public WorldSettings getSettings() {
		return settings;
	}

}
