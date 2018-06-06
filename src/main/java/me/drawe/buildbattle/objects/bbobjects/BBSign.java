package me.drawe.buildbattle.objects.bbobjects;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class BBSign {

    private BBArena arena;
    private Location location;
    private Sign sign;
    private Block blockBehind;

    public BBSign(BBArena arena, Location loc) {
        this.arena = arena;
        this.location = loc;
        try {
            this.sign = (Sign) location.getBlock().getState();
            this.blockBehind = LocationUtil.getAttachedBlock(getSign().getBlock());
            getBlockBehind().setType(Material.STAINED_CLAY);
            addIntoArenaSigns();
            update();
        } catch (Exception e) {
            BuildBattle.warning("§cThere is no sign for arena §e" + arena.getName() + "§c in location §e" + LocationUtil.getStringFromLocationXYZ(location) + " §c! Removing from signs.yml...");
            removeSign(null);
        }
    }

    public BBArena getArena() {
        return arena;
    }

    public Location getLocation() {
        return location;
    }

    public void update() {
        if(Bukkit.getPluginManager().isPluginEnabled(BuildBattle.getInstance())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(BuildBattle.getInstance(), () -> {
                getSign().setLine(0, Message.SIGN_JOIN_FIRST_LINE.getMessage().replaceAll("%teamsize%", String.valueOf(getArena().getTeamSize())).replaceAll("%arena%", getArena().getName()).replaceAll("%gamestate%", getArena().getBBArenaState().getPrefix()).replaceAll("%players%", getArena().getTotalPlayers()).replaceAll("%mode%", getArena().getGameType().getName()));
                getSign().setLine(1, Message.SIGN_JOIN_SECOND_LINE.getMessage().replaceAll("%teamsize%", String.valueOf(getArena().getTeamSize())).replaceAll("%arena%", getArena().getName()).replaceAll("%gamestate%", getArena().getBBArenaState().getPrefix()).replaceAll("%players%", getArena().getTotalPlayers()));
                getSign().setLine(2, Message.SIGN_JOIN_THIRD_LINE.getMessage().replaceAll("%teamsize%", String.valueOf(getArena().getTeamSize())).replaceAll("%arena%", getArena().getName()).replaceAll("%gamestate%", getArena().getBBArenaState().getPrefix()).replaceAll("%players%", getArena().getTotalPlayers()));
                getSign().setLine(3, Message.SIGN_JOIN_FOURTH_LINE.getMessage().replaceAll("%teamsize%", String.valueOf(getArena().getTeamSize())).replaceAll("%arena%", getArena().getName()).replaceAll("%gamestate%", getArena().getBBArenaState().getPrefix()).replaceAll("%players%", getArena().getTotalPlayers()));
                getSign().update(true);
                if(GameManager.isReplaceBlockBehindSigns()) {
                    getBlockBehind().setData(getArena().getBBArenaState().getDataValue());
                }
            }, 20L);
        }
    }

    public void addIntoArenaSigns() {
        getArena().getArenaSigns().add(this);
    }

    public Sign getSign() {
        return sign;
    }

    public void removeSign(Player p) {
        BuildBattle.getFileManager().getConfig("signs.yml").get().set(arena.getName() + "." + LocationUtil.getStringFromLocationXYZ(location), null);
        BuildBattle.getFileManager().getConfig("signs.yml").save();
        getArena().getArenaSigns().remove(this);
        getBlockBehind().setType(Material.AIR);
        if(p != null) {
            p.sendMessage(GameManager.getPrefix() + " §aSign for arena §e" + getArena().getName() + "§a successfully removed!");
        }
    }

    public Block getBlockBehind() {
        return blockBehind;
    }
}