package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.leaderboards.Leaderboard;
import me.drawe.buildbattle.leaderboards.LeaderboardType;
import me.drawe.buildbattle.managers.BBSettings;
import me.drawe.buildbattle.managers.LeaderboardManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBLeaderBoardSubCommand extends BBSubCommand {

    public BBLeaderBoardSubCommand() {
        super("leaderboard", "Command to manage leaderboards.", "buildbattlepro.setup",true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (BuildBattle.getInstance().isUseHolographicDisplays()) {
                if (args.length > 0) {
                    String subCommand = args[0].toLowerCase();
                    switch (subCommand) {
                        case "refresh":
                            LeaderboardManager.getInstance().refreshAllLeaderBoards();
                            sender.sendMessage(BBSettings.getPrefix() + " §aLeaderboards refreshed !");
                            return true;
                        case "create":
                            if (args.length == 2) {
                                if (sender instanceof Player) {
                                    Player p = (Player) sender;
                                    Location loc = p.getLocation();
                                    try {
                                        LeaderboardType type = LeaderboardType.valueOf(args[1].toUpperCase());
                                        LeaderboardManager.getInstance().createLeaderboard(p, loc, type);
                                        return true;
                                    } catch (Exception e) {
                                        p.sendMessage("§cInvalid type ! Available types :  §e§lWINS, PLAYED, BLOCKS_PLACED, PARTICLES_PLACED");
                                    }
                                }
                            } else {
                                sender.sendMessage("§cUsage >> /" + cmd.getName() + " lb create <type> §8| §7Create leaderboard with specified type");
                            }
                            break;
                        case "select":
                            if (sender instanceof Player) {
                                Player p = (Player) sender;
                                LeaderboardManager.getInstance().selectLeaderboard(p);
                                return true;
                            }
                            break;
                        case "delete":
                            if (sender instanceof Player) {
                                Player p = (Player) sender;
                                Leaderboard selected = LeaderboardManager.getSelectedLeaderboards().get(p);
                                if (selected != null) {
                                    selected.delete();
                                    p.sendMessage(BBSettings.getPrefix() + " §aHologram at location §e" + LocationUtil.getStringFromLocation(selected.getLocation()) + " §asuccessfully removed!");
                                    return true;
                                } else {
                                    p.sendMessage(BBSettings.getPrefix() + " §cPlease select a hologram near you by §e/" + cmd.getName() + " lb select §c!");
                                }
                            }
                            break;
                        case "teleport":
                            if (sender instanceof Player) {
                                Player p = (Player) sender;
                                Leaderboard selected = LeaderboardManager.getSelectedLeaderboards().get(p);
                                if (selected != null) {
                                    selected.teleport(p.getLocation());
                                    return true;
                                } else {
                                    p.sendMessage(BBSettings.getPrefix() + " §cPlease select a hologram near you by §e/" + cmd.getName() + " lb select §c!");
                                }
                            }
                            break;
                        default:
                            sender.sendMessage("§cUsages >> §e/" + cmd.getName() + " lb create <type> §8| §7Create leaderboard with specified type");
                            sender.sendMessage("         §c>> §e/" + cmd.getName() + " lb select §8| §7Select leaderboard closest to you");
                            sender.sendMessage("         §c>> §e/" + cmd.getName() + " lb delete §8| §7Deletes selected leaderboard");
                            sender.sendMessage("         §c>> §e/" + cmd.getName() + " lb teleport §8| §7Teleports selected leaderboard to your position");
                            sender.sendMessage("         §c>> §e/" + cmd.getName() + " lb refresh §8| §7Refresh all leaderboards");
                            break;
                    }
                } else {
                    sender.sendMessage("§cUsages >> §e/" + cmd.getName() + " lb create <type> §8| §7Create leaderboard with specified type");
                    sender.sendMessage("         §c>> §e/" + cmd.getName() + " lb select §8| §7Select leaderboard closest to you");
                    sender.sendMessage("         §c>> §e/" + cmd.getName() + " lb delete §8| §7Deletes selected leaderboard");
                    sender.sendMessage("         §c>> §e/" + cmd.getName() + " lb teleport §8| §7Teleports selected leaderboard to your position");
                    sender.sendMessage("         §c>> §e/" + cmd.getName() + " lb refresh §8| §7Refresh all leaderboards");
                }
            } else {
                sender.sendMessage(BBSettings.getPrefix() + " §cHolographicDisplays plugin is required to manage leaderboards !");
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }
}
