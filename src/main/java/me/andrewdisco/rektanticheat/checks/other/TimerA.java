package me.andrewdisco.rektanticheat.checks.other;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.packets.PacketCore;
import me.andrewdisco.rektanticheat.packets.events.PacketPlayerEvent;
import me.andrewdisco.rektanticheat.utils.TimeUtil;

public class TimerA extends Check {
	public static Map<UUID, Map.Entry<Integer, Long>> packets;
	public static Map<UUID, Integer> verbose;
	public static Map<UUID, Long> lastPacket;
	public static List<Player> toCancel;

	public TimerA(AntiCheat AntiCheat) {
		super("TimerA", "Timer", CheckType.Other, true, false, false, false, true, 7, 1, 600000L, AntiCheat);
		packets = new HashMap<>();
		verbose = new HashMap<>();
		toCancel = new ArrayList<>();
		lastPacket = new HashMap<>();
	}

	@EventHandler
	private void PacketPlayer(PacketPlayerEvent e) {
		final Player p = e.getPlayer();
		final UUID u = p.getUniqueId();
		if (!this.getAntiCheat().isEnabled()
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
			return;
		}

		if (getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()) {
			return;
		}

		final long lastPacket = TimerA.lastPacket.getOrDefault(p.getUniqueId(), 0L);
		int packets = 0;
		long Time = System.currentTimeMillis();
		int verbose = TimerA.verbose.getOrDefault(p.getUniqueId(), 0);

		if (TimerA.packets.containsKey(p.getUniqueId())) {
			packets = TimerA.packets.get(p.getUniqueId()).getKey();
			Time = TimerA.packets.get(p.getUniqueId()).getValue();
		}

		if(System.currentTimeMillis() - lastPacket < 5) {
			TimerA.lastPacket.put(u, System.currentTimeMillis());
			return;
		}
		final double threshold = 21;
		if(TimeUtil.elapsed(Time, 1000L)) {
			if(toCancel.remove(p) && packets <= 13) {
				return;
			}
			if(packets > threshold + PacketCore.movePackets.getOrDefault(u, 0) && PacketCore.movePackets.getOrDefault(u, 0) < 5) {
				verbose = (packets - threshold) > 10 ? verbose + 2 : verbose + 1;
			} else {
				verbose = 0;
			}

			if(verbose > 2) {
				getAntiCheat().logCheat(this, p, "Packets: " + packets, "(Type: A)");
			}
			packets = 0;
			Time = TimeUtil.nowlong();
			PacketCore.movePackets.remove(u);
		}
		packets++;

		TimerA.lastPacket.put(u, System.currentTimeMillis());
		TimerA.packets.put(u, new SimpleEntry<>(packets, Time));
		TimerA.verbose.put(u, verbose);
	}
}