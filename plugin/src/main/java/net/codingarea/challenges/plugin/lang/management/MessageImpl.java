package net.codingarea.challenges.plugin.lang.management;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.lang.ItemDescription;
import net.codingarea.challenges.plugin.lang.Message;
import net.codingarea.challenges.plugin.lang.Prefix;
import net.codingarea.challenges.plugin.utils.logging.Logger;
import net.codingarea.challenges.plugin.utils.misc.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public class MessageImpl implements Message {

	protected final String name;
	protected Object value;

	public MessageImpl(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	@Override
	public String asString(@Nonnull Object... args) {
		if (value == null)                      return Message.NULL;
		if (value instanceof String)            return StringUtils.format((String) value, args);
		if (value instanceof String[])          return StringUtils.getArrayAsString(StringUtils.format((String[]) value, args));
		if (value instanceof ItemDescription)   return ((ItemDescription)value).getName();
		Logger.severe("Message '" + name + "' has an illegal value " + value.getClass().getName());
		return Message.NULL;
	}

	@Nonnull
	@Override
	public String[] asArray(@Nonnull Object... args) {
		if (value == null)                      return new String[] { Message.NULL};
		if (value instanceof String[])          return StringUtils.format((String[]) value, args);
		if (value instanceof String)            return StringUtils.getStringAsArray(StringUtils.format((String) value, args));
		if (value instanceof ItemDescription)   return ((ItemDescription)value).getLore();
		Logger.severe("Message '" + name + "' has an illegal value " + value.getClass().getName());
		return new String[] { Message.NULL};
	}

	@Nonnull
	@Override
	public ItemDescription asItemDescription(@Nonnull Object... args) {
		if (value == null)                      return ItemDescription.empty();
		if (value instanceof ItemDescription)   return (ItemDescription) value;
		if (value instanceof String[])          return new ItemDescription((String[]) value);
		if (value instanceof String)            return new ItemDescription(asArray());
		Logger.severe("Message '" + name + "' has an illegal value " + value.getClass().getName());
		return ItemDescription.empty();
	}

	@Override
	public void send(@Nonnull CommandSender target, @Nonnull Prefix prefix, @Nonnull Object... args) {
		for (String line : asArray(args)) {
			if (line.trim().isEmpty()) target.sendMessage(line);
			else                       target.sendMessage(prefix + line);
		}
	}

	@Override
	public void broadcast(@Nonnull Prefix prefix, @Nonnull Object... args) {
		for (String line : asArray(args)) {
			if (line.trim().isEmpty()) Bukkit.broadcastMessage(line);
			else                       Bukkit.broadcastMessage(prefix + line);
		}
	}

	@Override
	public void broadcastTitle(@Nonnull Object... args) {
		String[] title = asArray(args);
		Bukkit.getOnlinePlayers().forEach(player -> sendTitle(player, title));
	}

	public void sendTitle(@Nonnull Player player, @Nonnull String[] title) {
		if (title.length == 0)      Challenges.getInstance().getTitleManager().sendTitle(player, "", "");
		else if (title.length == 1) Challenges.getInstance().getTitleManager().sendTitle(player, title[0], "");
		else                        Challenges.getInstance().getTitleManager().sendTitle(player, title[0], title[1]);
	}

	@Override
	public void setValue(@Nonnull String value) {
		this.value = value.startsWith("§") ? value : "§7" + value;
	}

	@Override
	public void setValue(@Nonnull String[] value) {
		for (int i = 0; i < value.length; i++) {
			if (value[i] != null && !value[i].startsWith("§") && !value[i].trim().isEmpty())
				value[i] = "§7" + value[i];
		}
		this.value = value;
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return asString();
	}

}
