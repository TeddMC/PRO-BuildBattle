package me.drawe.buildbattle.commands.subcommands;

import me.drawe.buildbattle.commands.BBCommand;
import me.drawe.buildbattle.managers.ArenaManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BBDelPlotSubCommand extends BBSubCommand {

    public BBDelPlotSubCommand() {
        super("delplot", "Deletes plot at your current location", "buildbattlepro.create",true);
    }

    @Override
    public boolean execute(BBCommand cmd, CommandSender sender, String[] args) {
        if (sender.hasPermission(getPermissionRequired())) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 1) {
                    BBArena a = ArenaManager.getInstance().getArena(args[0]);
                    if (a != null) {
                        int lastIndex = a.getBuildPlots().size() - 1;
                        if (lastIndex < 0) {
                            p.sendMessage("§e§lBuildBattle Setup §8| §cArena §e" + a.getName() + " §chas no build plots ! Create some !");
                        } else {
                            a.getBuildPlots().remove(lastIndex);
                            a.saveIntoConfig();
                            ArenaManager.getInstance().refreshArenaItem(a);
                            p.sendMessage("§e§lBuildBattle Setup §8| §aYou have successfully removed plot §e" + (lastIndex + 1) + " §afrom arena §e" + a.getName() + " §a!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage().replaceAll("%arena%", args[0]));
                    }
                } else {
                    sender.sendMessage("§cUsage >> /" + cmd.getName() + " delplot <arena> §8| §7Deletes plot at your current location ");
                }
            }
        } else {
            sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
        }
        return false;
    }
}
