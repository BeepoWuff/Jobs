
package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ToggleBarHandling;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class toggle implements Cmd {
    private static final Map<String, Supplier<Map<String, Boolean>>> toggleTypeMaps = new HashMap<>();
    static {
	toggleTypeMaps.put("bossbar", ToggleBarHandling::getBossBarToggle);
	toggleTypeMaps.put("actionbar", ToggleBarHandling::getActionBarToggle);
	toggleTypeMaps.put("blockprotection", ToggleBarHandling::getBlockProtectionToggle);
    }

    @Override
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (
	    args.length != 1 ||
	    !toggleTypeMaps.containsKey(args[0].toLowerCase(Locale.ROOT))
	) {
	    Jobs.getCommandManager().sendUsage(sender, "toggle");

	    return true;
	}

	String toggleType = args[0].toLowerCase(Locale.ROOT);
	String playerUUID = player.getUniqueId().toString();
	Map<String, Boolean> toggleTypeMap = toggleTypeMaps.get(toggleType).get();

	boolean existingValue = toggleTypeMap.getOrDefault(playerUUID, true);
	boolean newValue = !existingValue;

	toggleTypeMap.put(playerUUID, newValue);
	sender.sendMessage(Jobs.getLanguage().getMessage(String.format("command.toggle.output.%s", (newValue ? "on" : "off"))));

	if (toggleType.equals("bossbar")) {
	    JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player.getUniqueId());
	    if (jPlayer != null) {
		jPlayer.hideBossBars();
	    }
	}

	return true;
    }
}
