package me.drawethree.buildbattle.objects.bbobjects.rewards;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.bbobjects.BBTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class BBCommandRewards extends BBReward<List> {

    public BBCommandRewards(BuildBattle parent) {
        super(parent, "Command", List.class);
    }

    @Override
    public void giveReward(BBTeam team, int placement) {
        super.giveReward(team, placement);

        if (team == null || this.rewardsForPlacements.get(placement) == null || team.getPlayers() == null) {
            return;
        }

        for (Player p : team.getPlayers()) {
            for (String s : (List<String>) this.rewardsForPlacements.get(placement)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", p.getName()));
            }
        }
    }
}