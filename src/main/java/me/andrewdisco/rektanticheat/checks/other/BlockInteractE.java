package me.andrewdisco.rektanticheat.checks.other;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;

public class BlockInteractE extends Check {
	public static Map<UUID, Map.Entry<Integer, Long>> speedTicks;
	public BlockInteractE(AntiCheat AntiCheat) {
		super("BlockInteractE", "BI", CheckType.Other, true, false, false, false, true, 15, 1, 600000L, AntiCheat);
		speedTicks = new HashMap<>();
	}

	@SuppressWarnings("unlikely-arg-type")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onInteract(PlayerInteractEvent e) {
		if ((e.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& e.getItem() != null) {
			if (e.getItem().equals(Material.EXP_BOTTLE) || e.getItem().getType().equals(Material.GLASS_BOTTLE)
					|| e.getItem().getType().equals(Material.POTION)
					|| getAntiCheat().getLag().getPing(e.getPlayer()) > getAntiCheat().getPingCancel()
					|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()) {
				return;
			}
			final Player p = e.getPlayer();
			final UUID u = p.getUniqueId();
			if (getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
				return;
			}
			long Time = System.currentTimeMillis();
			int level = 0;
			if (speedTicks.containsKey(p.getUniqueId())) {
				level = speedTicks.get(p.getUniqueId()).getKey().intValue();
				Time = speedTicks.get(p.getUniqueId()).getValue().longValue();
			}
			final double diff = System.currentTimeMillis() - Time;
			level = diff >= 2.0
					? (diff <= 51.0 ? (level += 2)
							: (diff <= 100.0 ? (level += 0) : (diff <= 500.0 ? (level -= 6) : (level -= 12))))
							: ++level;
					final int max = 50;
					if (level > max * 0.9D && diff <= 100.0D) {
						getAntiCheat().logCheat(this, p, "BlockInteract: " + "Level: " + level + " Ping: " + getAntiCheat().lag.getPing(p), "(Type: E)");
						if (level > max) {
							level = max / 4;
						}
					} else if (level < 0) {
						level = 0;
					}
					speedTicks.put(u,
							new AbstractMap.SimpleEntry<>(level, System.currentTimeMillis()));
		}
	}
}