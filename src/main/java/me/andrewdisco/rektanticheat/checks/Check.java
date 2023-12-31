package me.andrewdisco.rektanticheat.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.data.DataManager;
import me.andrewdisco.rektanticheat.other.LagCore;
import me.andrewdisco.rektanticheat.packets.PacketCore;
import me.andrewdisco.rektanticheat.update.Updater;
import me.andrewdisco.rektanticheat.utils.TxtFile;

public class Check implements Listener {
	private final String Identifier;
	private final String Name;
	protected CheckType Type;
	private final AntiCheat AntiCheat;
	public Set<UUID> hasAlertsOn;
	public int maxMove = 10;
	public ExecutorService service;
	public static ArrayList<Player> getOnlinePlayers() {
		final ArrayList<Player> list = new ArrayList<>();
		for (final Player player : Bukkit.getOnlinePlayers()) {
			list.add(player);
		}
		return list;
	}
	public static Map<Player, Long> PACKET_USAGE = new ConcurrentHashMap<>();
	public static Set<String> PACKET_NAMES = new HashSet<>(Arrays.asList("MC|BSign", "MC|BEdit", "REGISTER"));
	private final Logger logger = null;
	private DataManager dataManager;
	public static long MS_PluginLoad;
	public static String coreVersion;
	public static AntiCheat Instance;
	public static Plugin plugin = Instance;
	public String PREFIX;
	public Updater updater;
	public PacketCore packet;
	public LagCore lag;
	public static List<Check> Checks;
	public static Map<UUID, Map<Check, Integer>> Violations;
	public static Map<UUID, Map<Check, Long>> ViolationReset;
	public static List<Player> AlertsOn;
	public static Map<Player, Map.Entry<Check, Long>> AutoBan;
	public static Map<String, Check> NamesBanned;
	static Random rand;
	public TxtFile autobanMessages;
	public static Map<UUID, Long> LastVelocity;
	public ArrayList<UUID> hasInvOpen = new ArrayList<>();
	private boolean Enabled = true;
	private boolean BanTimer = false;
	private boolean Bannable = false;
	private boolean Kickable = true;
	private boolean JudgementDay = false;
	private Integer MaxViolations = Integer.valueOf(5);
	private Integer ViolationsToNotify = Integer.valueOf(1);
	private Long ViolationResetTime = Long.valueOf(600000L);
	public Map<String, List<String>> DumpLogs = new HashMap<>();
	public Check(String Identifier, String Name, CheckType Type, boolean Enabled, boolean Bannable, boolean JudgementDay, boolean BanTimer, boolean Kickable, Integer MaxViolations, Integer ViolationsToNotify, long ViolationResetTime, AntiCheat AntiCheat) {
		this.Name = Name;
		this.AntiCheat = AntiCheat;
		this.Identifier = Identifier;
		this.Type = Type;

		this.Enabled = Enabled;
		this.setEnabled(Enabled);

		this.Bannable = Bannable;
		this.setBannable(Bannable);

		this.Kickable = Kickable;
		this.setKickable(Kickable);

		this.JudgementDay = JudgementDay;
		this.setJudgementDay(JudgementDay);

		this.BanTimer = BanTimer;
		this.setAutobanTimer(BanTimer);

		this.MaxViolations = MaxViolations;
		this.setMaxViolations(MaxViolations);

		this.ViolationsToNotify = ViolationsToNotify;
		this.setViolationsToNotify(ViolationsToNotify);

		this.ViolationResetTime = ViolationResetTime;
		this.setViolationResetTime(ViolationResetTime);
	}


	public void dumplog(Player player, String log) {
		if (!this.DumpLogs.containsKey(player.getName())) {
			final List<String> logs = new ArrayList<>();
			logs.add(log);
			this.DumpLogs.put(player.getName(), logs);
		} else {
			this.DumpLogs.get(player.getName()).add(log);
		}
	}

	public void onEnable() {
	}

	public void onDisable() {
	}

	public boolean isEnabled() {
		return this.Enabled;
	}

	public boolean isBannable() {
		return this.Bannable;
	}
	public boolean isKickable() {
		return this.Kickable;
	}

	public boolean hasBanTimer() {
		return this.BanTimer;
	}

	public boolean isJudgmentDay() {
		return this.JudgementDay;
	}

	public AntiCheat getAntiCheat() {
		return this.AntiCheat;
	}

	public boolean hasDump(Player player) {
		return DumpLogs.containsKey(player.getName());
	}

	public void clearDump(Player player) {
		DumpLogs.remove(player.getName());
	}

	public void clearDumps() {
		DumpLogs.clear();
	}

	public Integer getMaxViolations() {
		return this.MaxViolations;
	}

	public Integer getViolationsToNotify() {
		return this.ViolationsToNotify;
	}

	public Long getViolationResetTime() {
		return this.ViolationResetTime;
	}

	public void setEnabled(boolean Enabled) {
		if (AntiCheat.getConfig().getBoolean("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".enabled") != Enabled
				&& AntiCheat.getConfig().get("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".enabled") != null) {
			this.Enabled = AntiCheat.getConfig().getBoolean("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".enabled");
			return;
		}
		if (Enabled) {
			if (!isEnabled()) {
				this.AntiCheat.RegisterListener(this);
			}
		} else if (isEnabled()) {
			HandlerList.unregisterAll(this);
		}
		this.Enabled = Enabled;
	}



	public void checkValues() {
		if (AntiCheat.getConfig().getBoolean("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".enabled") == true) {
			this.setEnabled(true);
		} else {
			this.setEnabled(false);
		}
		if (AntiCheat.getConfig().getBoolean("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".bannable") == true) {
			this.setBannable(true);
		} else {
			this.setBannable(false);
		}
		if (AntiCheat.getConfig().getBoolean("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".judgementDay") == true) {
			this.setJudgementDay(true);
		} else {
			this.setJudgementDay(false);
		}
		if (AntiCheat.getConfig().getBoolean("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".banTimer") == true) {
			this.setAutobanTimer(true);
		} else {
			this.setAutobanTimer(false);
		}
		if (AntiCheat.getConfig().getInt("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".violationsToNotify") != 0) {
			this.setViolationsToNotify((AntiCheat.getConfig().getInt("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".violationsToNotify")));
		} else {
			this.setViolationsToNotify(0);
		}
		if (AntiCheat.getConfig().getInt("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".maxViolations") != 0) {
			this.setMaxViolations((AntiCheat.getConfig().getInt("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".maxViolations")));
		} else {
			this.setMaxViolations(0);
		}
		if (AntiCheat.getConfig().getLong("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".violationResetTime") != 0) {
			this.setViolationResetTime((AntiCheat.getConfig().getInt("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".violationResetTime")));
		} else {
			this.setViolationResetTime(0);
		}
	}

	public void setBannable(boolean Bannable) {
		if (AntiCheat.getConfig().getBoolean("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".bannable") != Bannable
				&& AntiCheat.getConfig().get("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".bannable") != null) {
			this.Bannable = AntiCheat.getConfig().getBoolean("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".bannable");
			return;
		}
		this.Bannable = Bannable;
	}
	public void setKickable(boolean Kickable) {
		if (AntiCheat.getConfig().getBoolean("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".kickable") != Bannable
				&& AntiCheat.getConfig().get("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".kannable") != null) {
			this.Bannable = AntiCheat.getConfig().getBoolean("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".kickable");
			return;
		}
		this.Kickable = Kickable;
	}

	public void setAutobanTimer(boolean BanTimer) {
		if ((AntiCheat.getConfig().getBoolean("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".banTimer") != BanTimer
				&& AntiCheat.getConfig().get("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".banTimer") != null)) {
			this.BanTimer = AntiCheat.getConfig().getBoolean("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".banTimer");
			return;
		}
		this.BanTimer = BanTimer;
	}

	public void setMaxViolations(int MaxViolations) {
		if (AntiCheat.getConfig().getInt("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".maxViolations") != MaxViolations
				&& AntiCheat.getConfig().get("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".maxViolations") != null) {
			this.MaxViolations = AntiCheat.getConfig().getInt("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".maxViolations");
			return;
		}
		this.MaxViolations = MaxViolations;
	}

	public void setViolationsToNotify(int ViolationsToNotify) {
		if (AntiCheat.getConfig().getInt("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".violationsToNotify") != ViolationsToNotify
				&& AntiCheat.getConfig().get("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".violationsToNotify") != null) {
			this.ViolationsToNotify = AntiCheat.getConfig().getInt("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".violationsToNotify");
			return;
		}
		this.ViolationsToNotify = ViolationsToNotify;
	}

	public void setViolationResetTime(long ViolationResetTime) {
		if (AntiCheat.getConfig().getInt("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".violationResetTime") != ViolationResetTime
				&& AntiCheat.getConfig().get("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".violationResetTime") != null) {
			this.ViolationResetTime = AntiCheat.getConfig().getLong("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".violationResetTime");
			return;
		}
		this.ViolationResetTime = ViolationResetTime;
	}

	public void setJudgementDay(boolean JudgementDay) {
		if (AntiCheat.getConfig().getBoolean("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".judgementDay") != JudgementDay
				&& AntiCheat.getConfig().get("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".judgementDay") != null) {
			this.JudgementDay = AntiCheat.getConfig().getBoolean("checks." + this.getType() + "." + this.getName() + "." + this.getIdentifier() + ".judgementDay");
			return;
		}
		this.JudgementDay = JudgementDay;
	}
	public CheckType getType() {
		return this.Type;
	}

	public String getName() {
		return this.Name;
	}

	public String getIdentifier() {
		return this.Identifier;
	}

	public List<String> getDump(Player player) {
		return this.DumpLogs.get(player.getName());
	}

	public String dump(String player) {
		if (!this.DumpLogs.containsKey(player)) {
			return null;
		}
		final TxtFile file = new TxtFile(this.getAntiCheat(), "/Dumps", player + "_" + this.getType() + "." + this.getName() + "." + this.getIdentifier());
		file.clear();
		for (final String Line : this.DumpLogs.get(player)) {
			file.addLine(Line);
		}
		file.write();
		return file.getName();
	}
}