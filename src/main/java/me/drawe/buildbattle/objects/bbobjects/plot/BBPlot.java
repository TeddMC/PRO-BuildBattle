package me.drawe.buildbattle.objects.bbobjects.plot;

import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.managers.PlayerManager;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.PlotBiome;
import me.drawe.buildbattle.objects.Votes;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import me.drawe.buildbattle.objects.bbobjects.BBTeam;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.kangarko.compatbridge.model.CompMaterial;
import me.kangarko.compatbridge.model.CompatBridge;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BBPlot implements Comparable<BBPlot> {

    private BBTeam team;
    private Location minPoint;
    private Location maxPoint;
    private BBArena arena;
    private BBPlotOptions options;
    private int votePoints;
    private HashMap<Player, Integer> votedPlayers;
    private List<BBPlotParticle> particles;
    private List<Location> blocksInPlot;
    private List<Chunk> chunksInPlot;
    private UUID reportedBy;

    public BBPlot(BBArena arena, Location minPoint, Location maxPoint) {
        this.arena = arena;
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.votePoints = 0;
        this.options = new BBPlotOptions(this);
        this.votedPlayers = new HashMap<>();
        this.particles = new ArrayList<>();
        this.reportedBy = null;
        this.team = null;
        setBlocksInPlot();
        setChunksInPlot();
    }

    @Override
    public int compareTo(BBPlot bbPlot) {
        if(getVotePoints() == bbPlot.getVotePoints()) {
            return 0;
        } else if(getVotePoints() > bbPlot.getVotePoints()) {
            return 1;
        } else if(getVotePoints() < bbPlot.getVotePoints()) {
            return -1;
        }
        return 0;
    }

    public int getVotePoints() {
        return votePoints;
    }

    public void setVotePoints(int votePoints) {
        this.votePoints = votePoints;
    }

    public void setFinalPoints() {
        for(Integer i : getVotedPlayers().values()) {
            setVotePoints(getVotePoints() + i);
        }
    }

    public void restoreBBPlot() {
        setTeam(null);
        setVotePoints(0);
        removeAllBlocks();
        removeAllParticles();
        setParticles(new ArrayList<>());
        setReportedBy(null);
        setVotedPlayers(new HashMap<>());
        changeFloor(GameManager.getDefaultFloorMaterial());
        setOptions(new BBPlotOptions(this));
        getOptions().setCurrentBiome(PlotBiome.PLAINS, false);
    }

    private void removeAllBlocks() {
        int minX = Math.min(getMinPoint().getBlockX(), getMaxPoint().getBlockX());
        int maxX = Math.max(getMinPoint().getBlockX(), getMaxPoint().getBlockX());
        int minZ = Math.min(getMinPoint().getBlockZ(), getMaxPoint().getBlockZ());
        int maxZ = Math.max(getMinPoint().getBlockZ(), getMaxPoint().getBlockZ());
        int minY = Math.min(getMinPoint().getBlockY(), getMaxPoint().getBlockY());
        int maxY = Math.max(getMinPoint().getBlockY(), getMaxPoint().getBlockY());
        for (int x = minX; x <= maxX; x += 1) {
            for (int y = minY + 1; y <= maxY; y += 1) {
                for (int z = minZ; z <= maxZ; z += 1) {
                    Location tmpblock = new Location(getWorld(), x, y, z);
                    if(tmpblock.getBlock().getType() != CompMaterial.AIR.getMaterial()) {
                        tmpblock.getBlock().setType(CompMaterial.AIR.getMaterial());
                    }
                    for (Entity e : tmpblock.getWorld().getNearbyEntities(tmpblock, 3, 3, 3)) {
                        if (e.getType() != EntityType.PLAYER) {
                            if(!e.hasMetadata("NPC")) {
                                e.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isLocationInPlot(Location location) {
        boolean trueOrNot = false;

        int minX = Math.min(getMinPoint().getBlockX(), getMaxPoint().getBlockX());
        int maxX = Math.max(getMinPoint().getBlockX(), getMaxPoint().getBlockX());
        int minZ = Math.min(getMinPoint().getBlockZ(), getMaxPoint().getBlockZ());
        int maxZ = Math.max(getMinPoint().getBlockZ(), getMaxPoint().getBlockZ());
        int minY = Math.min(getMinPoint().getBlockY(), getMaxPoint().getBlockY());
        int maxY = Math.max(getMinPoint().getBlockY(), getMaxPoint().getBlockY());

        if (location.getWorld().equals(getMinPoint().getWorld())) {
            if ((location.getBlockX() >= minX) && (location.getBlockX() <= maxX)) {
                if ((location.getBlockY() >= minY) && (location.getBlockY() <= maxY)) {
                    if ((location.getBlockZ() >= minZ) && (location.getBlockZ() <= maxZ)) {
                        trueOrNot = true;
                    }
                }
            }
        }
        return trueOrNot;
    }

    public void setVotedPlayers(HashMap<Player, Integer> votedPlayers) {
        this.votedPlayers = votedPlayers;
    }

    /*public void vote(Player p, Votes item) {
        if(votedPlayers.containsKey(p)) {
            if(item.getWeight() != votedPlayers.get(p)) {
                votedPlayers.put(p, item.getWeight());
                p.sendMessage(Message.VOTE_CHANGED.getChatMessage().replaceAll("%vote%", item.getPrefix()));
                if(GameManager.isShowVoteInSubtitle()) {
                    p.sendTitle("", item.getPrefix());
                }
                p.playSound(p.getLocation(), item.getSound(), 1L,item.getPitch());
            }
        } else {
            votedPlayers.put(p, item.getWeight());
            p.sendMessage(Message.VOTED.getChatMessage().replaceAll("%vote%", item.getPrefix()));
            if(GameManager.isShowVoteInSubtitle()) {
                p.sendTitle("", item.getPrefix());
            }
            p.playSound(p.getLocation(), item.getSound(), 1L,item.getPitch());
        }
        if (GameManager.isScoreboardEnabled()) {
            getArena().updateAllScoreboards(0);
        }
    }
    */

    public HashMap<Player, Integer> getVotedPlayers() {
        return votedPlayers;
    }

    public World getWorld() {
        return getMinPoint().getWorld();
    }

    public BBArena getArena() {
        return arena;
    }

    public void addIntoArenaPlots(){
        getArena().getBuildPlots().add(this);
    }

    public String getPlayerVoteString(Player p) {
        if(getVotedPlayers().containsKey(p)) {
            return Votes.getVoteItemByPoints(getVotedPlayers().get(p)).getPrefix();
        } else {
            return Votes.NONE.getPrefix();
        }
    }
    
    public Location getCenter(){
        double x,y,z = 0;
        if(getMinPoint().getX() > getMaxPoint().getX()){
            x = getMaxPoint().getX() + ((getMinPoint().getX()-getMaxPoint().getX())/2);
        }else{
            x = getMinPoint().getX() + ((getMaxPoint().getX()-getMinPoint().getX())/2);
        }
        if(getMinPoint().getY() > getMaxPoint().getY()){
            y = getMaxPoint().getY() + ((getMinPoint().getY()-getMaxPoint().getY())/2);
        }else{
            y = getMinPoint().getY() + ((getMaxPoint().getY()-getMinPoint().getY())/2);
        }
        if(getMinPoint().getZ() > getMaxPoint().getZ()){
            z = getMaxPoint().getZ() + ((getMinPoint().getZ()-getMaxPoint().getZ())/2);
        }else{
            z = getMinPoint().getZ() + ((getMaxPoint().getZ()-getMinPoint().getZ())/2);
        }
        return new Location(getMinPoint().getWorld(),x,y,z);

    }
    public BBPlotOptions getOptions() {
        return options;
    }

    public void setOptions(BBPlotOptions options) {
        this.options = options;
    }

    public Location getTeleportExactCenterLocation() {
        Location tpLoc = getCenter();
        tpLoc.clone().setY(getMinPoint().getY() + 1);
        return tpLoc;
    }

    public Location getTeleportLocation() {
        Location tploc = getCenter();
        while(tploc.getBlock().getType() != CompMaterial.AIR.getMaterial() || tploc.clone().add(0,1,0).getBlock().getType() != CompMaterial.AIR.getMaterial()) tploc = tploc.add(0,1,0);
        boolean enclosed = false;
        int counter = 0;
        Location location = tploc.clone();
        while (counter != 10){
            if(!(location.getBlock().getType() == CompMaterial.BARRIER.getMaterial() || location.getBlock().getType() == CompMaterial.AIR.getMaterial())){
                enclosed = true;
                tploc = location;
                counter = 9;
            }
            location.add(0,1,0);
            counter++;
        }
        if(enclosed) {
            while (tploc.getBlock().getType() != CompMaterial.AIR.getMaterial() || tploc.add(0, 1, 0).getBlock().getType() != CompMaterial.AIR.getMaterial()) {
                tploc = tploc.add(0, 1, 0);
            }
        }
        return tploc;
    }

    public void changeFloor(CompMaterial material) {
        if (material == CompMaterial.WATER_BUCKET) material = CompMaterial.WATER;
        if (material == CompMaterial.LAVA_BUCKET) material = CompMaterial.LAVA;
        int minX = Math.min(getMinPoint().getBlockX(), getMaxPoint().getBlockX());
        int maxX = Math.max(getMinPoint().getBlockX(), getMaxPoint().getBlockX());
        int minZ = Math.min(getMinPoint().getBlockZ(), getMaxPoint().getBlockZ());
        int maxZ = Math.max(getMinPoint().getBlockZ(), getMaxPoint().getBlockZ());
        int minY = Math.min(getMinPoint().getBlockY(), getMaxPoint().getBlockY());
        for (int x = minX; x <= maxX; x += 1) {
            for (int z = minZ; z <= maxZ; z += 1) {
                Location tmpblock = new Location(getWorld(), x, minY, z);
                CompatBridge.setTypeAndData(tmpblock.getBlock(),material,(byte) material.getData());
            }
        }
    }

    public void changeFloor(ItemStack item){
        CompMaterial m = CompMaterial.fromItemStack(item);
        changeFloor(m);
    }

    public boolean isInPlotRange(Location location, int added) {
        boolean trueOrNot = false;
        if (location.getWorld().equals(getMinPoint().getWorld()) && location.getWorld().equals(getMaxPoint().getWorld())) {
            if (location.getBlockX() >= getMinPoint().getBlockX()-added && location.getBlockX() <= getMaxPoint().getBlockX()+added){
                if (location.getBlockY() >= getMinPoint().getBlockY()-added&& location.getBlockY() <= getMaxPoint().getBlockY()+added) {
                    if (location.getBlockZ() >= getMinPoint().getBlockZ()-added && location.getBlockZ() <= getMaxPoint().getBlockZ()+added) {
                        trueOrNot = true;
                    }
                }
            }
        }
        return trueOrNot;
    }

    public Location getMaxPoint() {
        return maxPoint;
    }

    public Location getMinPoint() {
        return minPoint;
    }

    public CompMaterial getFloorMaterial() {
        return CompMaterial.fromMaterial(getMinPoint().getBlock().getType());
    }

    public List<BBPlotParticle> getParticles() {
        return particles;
    }

    public void setParticles(List<BBPlotParticle> particles) {
        this.particles = particles;
    }

    public void addActiveParticle(Player placer,BBPlotParticle BBPlotParticle) {
        if(getParticles().size() != GameManager.getMaxParticlesPerPlayer()) {
            getParticles().add(BBPlotParticle);
            BBPlotParticle.start();
            BBPlayerStats stats = PlayerManager.getInstance().getPlayerStats(placer);
            if(stats != null) {
                stats.setParticlesPlaced(stats.getParticlesPlaced() + 1);
            }
        } else {
            placer.sendMessage(Message.MAX_PARTICLES.getChatMessage().replaceAll("%amount%", String.valueOf(GameManager.getMaxParticlesPerPlayer())));
        }
    }

    public void removeActiveParticle(BBPlotParticle BBPlotParticle) {
        getParticles().remove(BBPlotParticle);
        BBPlotParticle.stop();
        getTeam().getCaptain().sendMessage(Message.PARTICLE_REMOVED.getChatMessage());
    }

    public void removeAllParticles() {
        Iterator it = getParticles().iterator();
        while(it.hasNext()) {
            BBPlotParticle particle = (BBPlotParticle) it.next();
            particle.stop();
            it.remove();
        }
    }

    public void resetPlotFromGame() {
        removeAllBlocks();
        removeAllParticles();
        ItemStack item = GameManager.getDefaultFloorMaterial().toItem();
        getOptions().setCurrentFloorItem(item);
        getOptions().setCurrentWeather(WeatherType.CLEAR, false);
        getOptions().setCurrentTime(BBPlotTime.NOON, false);
        getOptions().setCurrentBiome(PlotBiome.PLAINS, false);
        getTeam().getCaptain().sendMessage(Message.PLOT_CLEARED.getChatMessage());
    }

    public void setBlocksInPlot() {
        List<Location> locations = new ArrayList<>();
        int minX = Math.min(getMinPoint().getBlockX(), getMaxPoint().getBlockX());
        int maxX = Math.max(getMinPoint().getBlockX(), getMaxPoint().getBlockX());
        int minZ = Math.min(getMinPoint().getBlockZ(), getMaxPoint().getBlockZ());
        int maxZ = Math.max(getMinPoint().getBlockZ(), getMaxPoint().getBlockZ());
        int minY = Math.min(getMinPoint().getBlockY(), getMaxPoint().getBlockY());
        int maxY = Math.max(getMinPoint().getBlockY(), getMaxPoint().getBlockY());
        for (int x = minX; x <= maxX; x += 1) {
            for (int y = minY; y <= maxY; y += 1) {
                for (int z = minZ; z <= maxZ; z += 1) {
                    Location loc = new Location(getWorld(), x, y, z);
                    locations.add(loc);
                }
            }
        }
        this.blocksInPlot = locations;
    }
    public List<Location> getBlocksInPlot() {
        return blocksInPlot;
    }

    public BBTeam getTeam() {
        return team;
    }
    public void setTeam(BBTeam team) {
        this.team = team;
    }

    public void teleportTeamToPlot(BBTeam team) {
        for(Player p : team.getPlayers()) {
            p.teleport(getTeleportExactCenterLocation());
        }
    }

    public List<Chunk> getChunksInPlot() {
        return chunksInPlot;
    }

    public void setChunksInPlot() {
        List<Chunk> chunks = new ArrayList<>();
        for(Location l : getBlocksInPlot()) {
            Chunk c = l.getChunk();
            if(!chunks.contains(c)) {
                chunks.add(c);
            }
        }
        this.chunksInPlot = chunks;
    }

    public UUID getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(UUID reportedBy) {
        this.reportedBy = reportedBy;
    }
}