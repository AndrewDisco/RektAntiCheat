package me.andrewdisco.rektanticheat.checks.other;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.packets.events.PacketBlockPlacementEvent;
import me.andrewdisco.rektanticheat.packets.events.PacketHeldItemChangeEvent;
import me.andrewdisco.rektanticheat.packets.events.PacketSwingArmEvent;
import me.andrewdisco.rektanticheat.utils.TimeUtil;

public class CrashA extends Check {
	public static Map<UUID, Map.Entry<Integer, Long>> crashTicks;
	public static Map<UUID, Map.Entry<Integer, Long>> crash2Ticks;
	public static Map<UUID, Map.Entry<Integer, Long>> crash3Ticks;
	private final List<UUID> crashs;

	public CrashA(AntiCheat AntiCheat) {
		super("CrashA", "Crash", CheckType.Other, true, true, false, true, false, 5, 1, 600000L, AntiCheat);
		crashTicks = new HashMap<>();
		crash2Ticks = new HashMap<>();
		crash3Ticks = new HashMap<>();
		crashs = new ArrayList<>();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void Swing(final PacketSwingArmEvent e) {
		final Player p = e.getPlayer();
		final UUID u = p.getUniqueId();
		if (getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
			return;
		}
		if (this.crashs.contains(u)) {
			e.getPacketEvent().setCancelled(true);
			return;
		}
		int Count = 0;
		long Time = System.currentTimeMillis();
		if (crashTicks.containsKey(u)) {
			Count = crashTicks.get(u).getKey();
			Time = crashTicks.get(u).getValue();
		}
		++Count;
		if (crashTicks.containsKey(u) && TimeUtil.elapsed(Time, 100L)) {
			Count = 0;
			Time = TimeUtil.nowlong();
		}
		if (Count > 2000) {
			this.getAntiCheat().logCheat(this, p, "[1]", "(Type: A)");
			this.crashs.add(u);
		}
		crashTicks.put(u, new AbstractMap.SimpleEntry<>(Count, Time));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void Switch(final PacketHeldItemChangeEvent e) {
		final Player p = e.getPlayer();
		final UUID u = p.getUniqueId();
		if (this.crashs.contains(u)) {
			e.getPacketEvent().setCancelled(true);
			return;
		}
		if (getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
			return;
		}
		int Count = 0;
		long Time = System.currentTimeMillis();
		if (crash2Ticks.containsKey(u)) {
			Count = crash2Ticks.get(u).getKey();
			Time = crash2Ticks.get(u).getValue();
		}
		++Count;
		if (crash2Ticks.containsKey(u) && TimeUtil.elapsed(Time, 100L)) {
			Count = 0;
			Time = TimeUtil.nowlong();
		}
		if (Count > 2000) {
			this.getAntiCheat().logCheat(this, p, "[2]", "(Type: A)");
			this.crashs.add(u);
		}
		crash2Ticks.put(u, new AbstractMap.SimpleEntry<>(Count, Time));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void BlockPlace(final PacketBlockPlacementEvent e) {
		final Player p = e.getPlayer();
		final UUID u = p.getUniqueId();
		if (this.crashs.contains(u)) {
			e.getPacketEvent().setCancelled(true);
			return;
		}
		int Count = 0;
		long Time = System.currentTimeMillis();
		if (crash3Ticks.containsKey(u)) {
			Count = crash3Ticks.get(u).getKey();
			Time = crash3Ticks.get(u).getValue();
		}
		++Count;
		if (crash3Ticks.containsKey(u) && TimeUtil.elapsed(Time, 100L)) {
			Count = 0;
			Time = TimeUtil.nowlong();
		}
		if (Count > 2000) {
			if (getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
					|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
				return;
			}
			this.getAntiCheat().logCheat(this, p, "[3]", "(Type: A)");
			this.crashs.add(u);
		}
		crash3Ticks.put(u, new AbstractMap.SimpleEntry<>(Count, Time));
	}
}