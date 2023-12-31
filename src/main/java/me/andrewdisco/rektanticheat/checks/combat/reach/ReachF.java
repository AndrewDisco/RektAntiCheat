package me.andrewdisco.rektanticheat.checks.combat.reach;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;

public class ReachF extends Check {
	public ReachF(AntiCheat AntiCheat) {
		super("ReachF", "Reach",  CheckType.Combat, true, false, false, false, false, 20, 1, 600000L, AntiCheat);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(final PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		if (p == null) {
			return;
		}
		if (e.getClickedBlock() != null) {
			int ping = getAntiCheat().getLag().getPing(p);
			final double tps = getAntiCheat().getLag().getTPS();
			if (ping > 200) {
				ping = 200;
			}
			if (ping < 10) {
				ping = 10;
			}
			final double maxReach = 5.4;
			final double diff = e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5).distance(p.getLocation().add(0.0, 1.6, 0.0));
			final String dist = Double.toString(diff).substring(0, 3);
			if (p.getGameMode() != GameMode.CREATIVE) {
				if (diff > maxReach + 0.1 + ping / 40) {
					getAntiCheat().logCheat(this, p, "Interact too far away; distance: " + dist + "; Ping: " + ping + "; TPS: " + tps, "(Type: F)");
				} else if (diff > maxReach) {
					getAntiCheat().logCheat(this, p, "Interact too far away; distance: " + dist + "; Ping: " + ping + "; TPS: " + tps, "(Type: F)");
				}
			} else if (diff > maxReach + 0.6 + ping / 40) {
				getAntiCheat().logCheat(this, p, "Interact too far away; distance: " + dist + "; Ping: " + ping + "; TPS: " + tps, "(Type: F)");
			} else if (diff > maxReach + 0.5) {
				getAntiCheat().logCheat(this, p, "Interact too far away; distance: " + dist + "; Ping: " + ping + "; TPS: " + tps, "(Type: F)");
			}
		}
	}
}