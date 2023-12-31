package me.andrewdisco.rektanticheat.checks.combat.autoclicker;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.packets.events.PacketUseEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.comphenix.protocol.wrappers.EnumWrappers;

import me.andrewdisco.rektanticheat.utils.TimeUtil;

public class AutoClickerA extends Check {
	public static Map<UUID, Long> LastMS;
	public static Map<UUID, List<Long>> Clicks;
	public static Map<UUID, Map.Entry<Integer, Long>> ClickTicks;

	public AutoClickerA(final me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("AutoClickerA", "AutoClicker",  CheckType.Combat, true, true, false, true, false, 10, 3, 250000L, AntiCheat);
		AutoClickerA.LastMS = new HashMap<>();
		AutoClickerA.Clicks = new HashMap<>();
		AutoClickerA.ClickTicks = new HashMap<>();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void UseEntity(PacketUseEntityEvent e) {
		if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK
				|| !(e.getAttacked() instanceof Player)) {
			return;
		}

		final Player p = e.getAttacker();
		if (p == null) {
			return;
		}
		final UUID u = p.getUniqueId();
		int Count = 0;
		long Time = System.currentTimeMillis();
		if (ClickTicks.containsKey(u)) {
			Count = ClickTicks.get(u).getKey();
			Time = ClickTicks.get(u).getValue();
		}
		if (LastMS.containsKey(u)) {
			final long MS = TimeUtil.nowlong() - LastMS.get(u);
			if (MS > 500L || MS < 5L) {
				LastMS.put(p.getUniqueId(), TimeUtil.nowlong());
				return;
			}
			if (Clicks.containsKey(u)) {
				final List<Long> Clicks = AutoClickerA.Clicks.get(u);
				if (Clicks.size() == 10) {
					AutoClickerA.Clicks.remove(u);
					Collections.sort(Clicks);
					final long Range = Clicks.get(Clicks.size() - 1) - Clicks.get(0);
					if (Range < 30L) {
						++Count;
						Time = System.currentTimeMillis();
						this.dumplog(p, "Logged for AutoClicker Type A; New Range: " + Range +"; New Count: " + Count);
					}
				} else {
					Clicks.add(MS);
					AutoClickerA.Clicks.put(u, Clicks);
				}
			} else {
				final List<Long> Clicks = new ArrayList<>();
				Clicks.add(MS);
				AutoClickerA.Clicks.put(p.getUniqueId(), Clicks);
			}
		}
		if (ClickTicks.containsKey(u) && TimeUtil.elapsed(Time, 5000L)) {
			Count = 0;
			Time = TimeUtil.nowlong();
		}
		if ((Count > 2 && this.getAntiCheat().getLag().getPing(p) < 100)
				|| (Count > 4 && this.getAntiCheat().getLag().getPing(p) <= 400)) {
			dumplog(p, "Logged. Count: " + Count);
			Count = 0;
			if (getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
					|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
				return;
			}
			getAntiCheat().logCheat(this, p, "Click Pattern", "(Type: A)");
			ClickTicks.remove(p.getUniqueId());
		} else if (this.getAntiCheat().getLag().getPing(p) > 400) {
			dumplog(p, "Would set off AutoClicker (Click Pattern) but latency is too high!");
		}
		LastMS.put(p.getUniqueId(), TimeUtil.nowlong());
		ClickTicks.put(p.getUniqueId(), new AbstractMap.SimpleEntry<>(Count, Time));
	}
}