package me.andrewdisco.rektanticheat.checks.combat.killaura;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.packets.events.PacketUseEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.comphenix.protocol.wrappers.EnumWrappers;

import me.andrewdisco.rektanticheat.utils.CheatUtil;
import me.andrewdisco.rektanticheat.utils.TimeUtil;
public class KillAuraF extends Check {
	public static Map<UUID, Map.Entry<Integer, Long>> AuraTicks;
	public KillAuraF(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("KillAuraF", "KillAura",  CheckType.Combat, true, false, false, false, true, 20, 1, 600000L, AntiCheat);
		AuraTicks = new HashMap<>();
	}
	@EventHandler
	public void UseEntity(PacketUseEntityEvent e) {
		if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK
				|| !((e.getAttacked()) instanceof Player)) {
			return;
		}
		final Player damager = e.getAttacker();
		final Player player = (Player) e.getAttacked();
		if (damager == null) {
			return;
		}

		if (damager.getAllowFlight()
				|| player.getAllowFlight()) {
			return;
		}

		int Count = 0;
		long Time = System.currentTimeMillis();
		if (AuraTicks.containsKey(damager.getUniqueId())) {
			Count = AuraTicks.get(damager.getUniqueId()).getKey();
			Time = AuraTicks.get(damager.getUniqueId()).getValue();
		}
		final double OffsetXZ = CheatUtil.getAimbotoffset(damager.getLocation(), damager.getEyeHeight(),
				player);
		double LimitOffset = 200.0;
		if (damager.getVelocity().length() > 0.08
				|| this.getAntiCheat().LastVelocity.containsKey(damager.getUniqueId())) {
			LimitOffset += 200.0;
		}
		final int Ping = this.getAntiCheat().getLag().getPing(damager);
		if (Ping >= 100 && Ping < 200) {
			LimitOffset += 50.0;
		} else if (Ping >= 200 && Ping < 250) {
			LimitOffset += 75.0;
		} else if (Ping >= 250 && Ping < 300) {
			LimitOffset += 150.0;
		} else if (Ping >= 300 && Ping < 350) {
			LimitOffset += 300.0;
		} else if (Ping >= 350 && Ping < 400) {
			LimitOffset += 400.0;
		} else if (Ping > 400) {
			return;
		}
		if (OffsetXZ > LimitOffset * 4.0) {
			Count += 12;
		} else if (OffsetXZ > LimitOffset * 3.0) {
			Count += 10;
		} else if (OffsetXZ > LimitOffset * 2.0) {
			Count += 8;
		} else if (OffsetXZ > LimitOffset) {
			Count += 4;
		}
		if (AuraTicks.containsKey(damager.getUniqueId()) && TimeUtil.elapsed(Time, 60000L)) {
			Count = 0;
			Time = TimeUtil.nowlong();
		}
		if (Count >= 16) {
			dumplog(damager, "Offset: " + OffsetXZ + ", Ping: " + Ping + ", Max Offset: " + LimitOffset);
			dumplog(damager, "Logged. Count: " + Count + ", Ping: " + Ping);
			Count = 0;
			getAntiCheat().logCheat(this, damager, "Hit Miss Ratio", "(Type: F)");
		}
		AuraTicks.put(damager.getUniqueId(), new AbstractMap.SimpleEntry<>(Count, Time));
	}
}