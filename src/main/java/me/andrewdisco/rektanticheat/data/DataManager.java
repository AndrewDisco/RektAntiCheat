package me.andrewdisco.rektanticheat.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import me.andrewdisco.rektanticheat.checks.Check;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DataManager {

	private final List<DataPlayer> dataObjects;
	private final List<Check> checks;
	private final Map<Player, Map<Check, Integer>> violations;
	private final List<DataPlayer> players;

	private final Set<DataPlayer> dataSet = new HashSet<>();

	public DataManager() {
		this.dataObjects = new ArrayList<>();
		Bukkit.getOnlinePlayers().forEach(this::add);
		checks = new ArrayList<>();
		violations = new WeakHashMap<>();
		players = new ArrayList<>();
	}
	public void createDataObject(final Player player) {
		this.dataObjects.add(new DataPlayer(player));
	}
	public List<DataPlayer> getDataObjects() {
		return this.dataObjects;
	}
	public void removeDataObject(final DataPlayer playerData) {
		this.dataObjects.remove(playerData);
	}
	public DataPlayer getPlayerData(final Player player) {
		for (final DataPlayer playerData : this.dataObjects) {
			if (playerData.player == player) {
				return playerData;
			}
		}
		return null;
	}
	public DataPlayer getDataPlayer(Player p) {
		return dataSet.stream().filter(dataPlayer -> dataPlayer.player == p).findFirst().orElse(null);
	}

	public void add(Player p) {
		dataSet.add(new DataPlayer(p));
	}

	public void remove(Player p) {
		dataSet.removeIf(dataPlayer -> dataPlayer.player == p);
	}

	public void removeCheck(Check c) {
		if(checks.contains(c)) {
			checks.remove(c);
		}
	}

	public boolean isCheck(Check c) {
		return checks.contains(c);
	}

	public Check getCheckAyName(String cn) {
		for(final Check checkLoop : Collections.synchronizedList(checks)) {
			if(checkLoop.getName().equalsIgnoreCase(cn)) {
				return checkLoop;
			}
		}

		return null;
	}

	public Map<Player, Map<Check, Integer>> getViolationsMap() {
		return violations;
	}

	public int getViolatonsPlayer(Player p, Check c) {
		if(violations.containsKey(p)) {
			final Map<Check, Integer> vlMap = violations.get(p);

			return vlMap.getOrDefault(c, 0);
		}
		return 0;
	}

	public void addViolation(Player p, Check c) {
		if (violations.containsKey(p)) {
			final Map<Check, Integer> vlMap = violations.get(p);

			vlMap.put(c, vlMap.getOrDefault(c, 0) + 1);
			violations.put(p, vlMap);
		} else {
			final Map<Check, Integer> vlMap = new HashMap<>();

			vlMap.put(c, 1);

			violations.put(p, vlMap);
		}
	}

	public void addPlayerData(Player p) {
		players.add(new DataPlayer(p));
	}

	public DataPlayer getData(Player p) {
		for(final DataPlayer dataLoop : Collections.synchronizedList(players)) {
			if(dataLoop.getPlayer() == p) {
				return dataLoop;
			}
		}
		return null;
	}

	public void removePlayerData(Player p) {
		for(final DataPlayer dataLoop : Collections.synchronizedList(players)) {
			if(dataLoop.getPlayer() == p) {
				players.remove(dataLoop);
				break;
			}
		}
	}

	public void addCheck(Check c) {
		if(!checks.contains(c)) {
			checks.add(c);
		}
	}
	public List<Check> getChecks() {
		return checks;
	}
}