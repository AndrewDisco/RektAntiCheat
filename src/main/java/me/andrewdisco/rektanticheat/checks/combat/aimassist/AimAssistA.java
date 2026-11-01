package me.andrewdisco.rektanticheat.checks.combat.aimassist;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AimAssistA extends Check {
	// one counter per player: this check is a single instance shared by the whole server
	private final Map<UUID, Integer> aimAssist = new HashMap<UUID, Integer>();

	public AimAssistA(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("AimAssistA", "AimAssist",  CheckType.Combat, true, false, false, false, true, 20, 1, 600000L, AntiCheat);
	}

	private static double getFrac(double d) {
		return d % 1.0;
	}

	private void setAimAssest(UUID id, int n) {
		this.aimAssist.put(id, Math.max(n, 0));
	}

	private int getAimAssist(UUID id) {
		final Integer n = this.aimAssist.get(id);
		return n == null ? 0 : n;
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		this.aimAssist.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		final Location location = e.getFrom().clone();
		final Location location2 = e.getTo().clone();
		final Player p = e.getPlayer();
		if (p == null) {
			return;
		}
		final UUID id = p.getUniqueId();
		final double d = Math.abs(location.getYaw() - location2.getYaw());
		if (d > 0.0 && d < 360.0) {
			if (AimAssistA.getFrac(d) == 0.0) {
				this.setAimAssest(id, this.getAimAssist(id) + 100);
				if (this.getAimAssist(id) > 2000) {

					if (getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
							|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
						return;
					}
					getAntiCheat().logCheat(this, p, "(Aimbot)", "(Type: A)");
					this.setAimAssest(id, 0);
				}
			} else {
				this.setAimAssest(id, this.getAimAssist(id) - 21);
			}
		}
	}
}