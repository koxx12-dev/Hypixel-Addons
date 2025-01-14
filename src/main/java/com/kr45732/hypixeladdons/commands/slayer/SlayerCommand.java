/*
 * Hypixel Addons - A quality of life mod for Hypixel
 * Copyright (c) 2021-2021 kr45732
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kr45732.hypixeladdons.commands.slayer;

import static com.kr45732.hypixeladdons.utils.Utils.roundAndFormat;

import com.kr45732.hypixeladdons.utils.*;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class SlayerCommand extends CommandBase {

	public static SlayerCommand INSTANCE = new SlayerCommand();

	public static IChatComponent getSlayerString(String[] args) {
		if (args.length != 1 && args.length != 2) {
			return Utils.getUsage(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return Utils.invalidKey();
		}

		Player player = args.length == 1 ? new Player(args[0]) : new Player(args[1], args[1]);
		if (!player.isValid()) {
			return Utils.getFailCause(player);
		}

		int svenOneKills = player.getSlayerBossKills("wolf", 0);
		int svenTwoKills = player.getSlayerBossKills("wolf", 1);
		int svenThreeKills = player.getSlayerBossKills("wolf", 2);
		int svenFourKills = player.getSlayerBossKills("wolf", 3);

		int revOneKills = player.getSlayerBossKills("zombie", 0);
		int revTwoKills = player.getSlayerBossKills("zombie", 1);
		int revThreeKills = player.getSlayerBossKills("zombie", 2);
		int revFourKills = player.getSlayerBossKills("zombie", 3);
		int revFiveKills = player.getSlayerBossKills("zombie", 4);

		int taraOneKills = player.getSlayerBossKills("spider", 0);
		int taraTwoKills = player.getSlayerBossKills("spider", 1);
		int taraThreeKills = player.getSlayerBossKills("spider", 2);
		int taraFourKills = player.getSlayerBossKills("spider", 3);

		int endermanOneKills = player.getSlayerBossKills("enderman", 0);
		int endermanTwoKills = player.getSlayerBossKills("enderman", 1);
		int endermanThreeKills = player.getSlayerBossKills("enderman", 2);
		int endermanFourKills = player.getSlayerBossKills("enderman", 3);

		long coinsSpentOnSlayers =
			100L *
			(svenOneKills + revOneKills + taraOneKills) +
			2000L *
			(svenTwoKills + revTwoKills + taraTwoKills) +
			10000L *
			(svenThreeKills + revThreeKills + taraThreeKills) +
			50000L *
			(svenFourKills + revFourKills + taraFourKills) +
			100000L *
			revFiveKills +
			2000L *
			endermanOneKills +
			7500L *
			endermanTwoKills +
			20000L *
			endermanThreeKills +
			50000L *
			endermanFourKills;

		IChatComponent output = Utils.empty().appendSibling(player.getLink());
		output.appendText(
			"\n\n" +
			Utils.labelWithDesc("Total slayer", Utils.formatNumber(player.getTotalSlayer()) + " XP") +
			"\n" +
			Utils.labelWithDesc("Total coins spent", Utils.simplifyNumber(coinsSpentOnSlayers) + "\n")
		);

		for (Map.Entry<String, String> slayerName : Constants.SLAYER_NAMES_MAP.entrySet()) {
			StringBuilder curSlayerKills = new StringBuilder();
			int maxTier = slayerName.getValue().equals("zombie") ? 5 : 4;
			for (int i = 1; i <= maxTier; i++) {
				curSlayerKills
					.append(Utils.labelWithDesc("Tier " + i, "" + player.getSlayerBossKills(slayerName.getValue(), i - 1)))
					.append(i != maxTier ? "\n" : "");
			}

			output.appendSibling(
				new ChatText(
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc(
						Utils.capitalizeString(slayerName.getValue()) + " (" + player.getSlayerLevel(slayerName.getKey()) + ")",
						Utils.simplifyNumber(player.getSlayer(slayerName.getKey())) + " XP"
					)
				)
					.setHoverEvent(Utils.capitalizeString(slayerName.getValue()) + " - Boss Kills", curSlayerKills.toString())
					.build()
			);
		}
		return Utils.wrapText(output);
	}

	public static String getSlayerChat(String[] args) {
		if (args.length != 1 && args.length != 2) {
			return Utils.getUsageChat(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return Utils.invalidKeyChat();
		}

		Player player = args.length == 1 ? new Player(args[0]) : new Player(args[1], args[1]);
		if (!player.isValid()) {
			return Utils.getFailCauseChat(player);
		}

		StringBuilder output = new StringBuilder(player.getUsername() + " has " + Utils.formatNumber(player.getTotalSlayer()) + " XP");

		for (Map.Entry<String, String> slayerName : Constants.SLAYER_NAMES_MAP.entrySet()) {
			output
				.append(", ")
				.append(Utils.capitalizeString(slayerName.getValue()))
				.append(" ")
				.append(roundAndFormat(player.getSlayerLevel(slayerName.getKey()))); // TODO: with progress
		}

		return output.toString();
	}

	@Override
	public String getCommandName() {
		return "hpa:slayer";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:slayers");
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <player> [profile]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Utils.executor.submit(() -> sender.addChatMessage(getSlayerString(args)));
	}
}
