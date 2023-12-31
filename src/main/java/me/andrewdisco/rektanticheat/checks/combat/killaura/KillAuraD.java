package me.andrewdisco.rektanticheat.checks.combat.killaura;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.packets.PacketPlayerType;
import me.andrewdisco.rektanticheat.packets.events.PacketKillauraEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class KillAuraD extends Check {

	public static Map<UUID, Map.Entry<Double, Double>> packetTicks;

	public KillAuraD(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("KillAuraD", "KillAura",  CheckType.Combat, true, false, false, false, true, 20, 1, 600000L, AntiCheat);
		packetTicks = new HashMap<>();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void packet(PacketKillauraEvent e) {
		final Player p = e.getPlayer();
		if (p == null) {
			return;
		}
		if (!getAntiCheat().isEnabled()
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
			return;
		}

		double Count = 0;
		double Other = 0;
		if (packetTicks.containsKey(e.getPlayer().getUniqueId())) {
			Count = packetTicks.get(e.getPlayer().getUniqueId()).getKey();
			Other = packetTicks.get(e.getPlayer().getUniqueId()).getValue();
		}

		if (e.getType() == PacketPlayerType.ARM_SWING) {
			Other++;
		}

		if (e.getType() == PacketPlayerType.USE) {
			Count++;
		}

		if(Count > Other && Other >= 2) {
			if (Count > 3) {
				getAntiCheat().logCheat(this, p, "Packet" + " Count: " + Count + " Other: " + Other, "(Type: D)");
			}
		}
		if(Count > 3 || Other >= 3) {
			Count = 0;
			Other = 0;
		}
		packetTicks.put(e.getPlayer().getUniqueId(), new AbstractMap.SimpleEntry<>(Count, Other));
	}
}