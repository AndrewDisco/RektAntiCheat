package me.andrewdisco.rektanticheat.checks.combat.killaura;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.packets.events.PacketUseEntityEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.comphenix.protocol.wrappers.EnumWrappers;

import me.andrewdisco.rektanticheat.utils.TimeUtil;

public class KillAuraG extends Check {
	public static Map<UUID, Map.Entry<Integer, Long>> AimbotTicks;
	public static Map<UUID, Double> Differences;
	public static Map<UUID, Location> LastLocation;
	public KillAuraG(final me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("KillAuraG", "KillAura",  CheckType.Combat, true, false, false, false, true, 14, 1, 600000L, AntiCheat);
		AimbotTicks = new HashMap<>();
		Differences = new HashMap<>();
		LastLocation = new HashMap<>();
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void UseEntity(PacketUseEntityEvent e) {
		if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK
				|| !((e.getAttacked()) instanceof Player)) {
			return;
		}
		final Player damager = e.getAttacker();
		if (damager == null) {
			return;
		}
		if (damager.getAllowFlight()
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(damager) > getAntiCheat().getPingCancel()) {
			return;
		}
		Location from = null;
		final Location to = damager.getLocation();
		if (LastLocation.containsKey(damager.getUniqueId())) {
			from = LastLocation.get(damager.getUniqueId());
		}
		LastLocation.put(damager.getUniqueId(), damager.getLocation());
		double Count = 0;
		long Time = System.currentTimeMillis();
		double LastDifference = -111111.0;
		if (Differences.containsKey(damager.getUniqueId())) {
			LastDifference = Differences.get(damager.getUniqueId());
		}
		if (AimbotTicks.containsKey(damager.getUniqueId())) {
			Count = AimbotTicks.get(damager.getUniqueId()).getKey();
			Time = AimbotTicks.get(damager.getUniqueId()).getValue();
		}
		if (from == null || (to.getX() == from.getX() && to.getZ() == from.getZ())) {
			return;
		}
		final double Difference = Math.abs(to.getYaw() - from.getYaw());
		if (Difference == 0.0) {
			return;
		}
		if (Difference > 2.4) {
			this.dumplog(damager, "Logged for KillAura Type G; Difference: " + Difference);
			final double diff = Math.abs(LastDifference - Difference);
			if (e.getAttacked().getVelocity().length() < 0.1) {
				if (diff < 1.4) {
					Count += 1;
				} else {
					Count = 0;
				}
			} else {
				if (diff < 1.8) {
					Count += 1;
				} else {
					Count = 0;
				}
			}
		}
		Differences.put(damager.getUniqueId(), Difference);
		if (AimbotTicks.containsKey(damager.getUniqueId()) && TimeUtil.elapsed(Time, 5000L)) {
			dumplog(damager, "Logged for KillAura Type G; Count Reset");
			Count = 0;
			Time = TimeUtil.nowlong();
		}
		if (Count > 5) {
			Count = 0;
			dumplog(damager,
					"Logged for KillAura Type G; Last Difference: " + Math.abs(to.getYaw() - from.getYaw()) + ", Count: " + Count);
			getAntiCheat().logCheat(this, damager, "Aimbot", "(Type: G)");
		}
		AimbotTicks.put(damager.getUniqueId(),
				new AbstractMap.SimpleEntry<>((int) Math.round(Count), Time));
	}
}