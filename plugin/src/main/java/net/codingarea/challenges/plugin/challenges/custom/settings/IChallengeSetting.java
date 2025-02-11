package net.codingarea.challenges.plugin.challenges.custom.settings;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import org.bukkit.Material;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public interface IChallengeSetting {

	SubSettingsBuilder getSubSettingsBuilder();
	String getName();
	Material getMaterial();
	String getMessage();

}
