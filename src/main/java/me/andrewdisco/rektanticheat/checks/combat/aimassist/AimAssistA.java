package me.andrewdisco.rektanticheat.checks.combat.aimassist;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class AimAssistA extends Check {
	private int aimAssist = 0;

	public AimAssistA(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("AimAssistA", "AimAssist",  CheckType.Combat, true, false, false, false, true, 20, 1, 600000L, AntiCheat);
	}

	private static double getFrac(double d) {
		return d % 1.0;
	}

	private void setAimAssest(int n) {
		this.aimAssist = n;
		if (this.aimAssist < 0) {
			this.aimAssist = 0;
		}
	}

	private int getAimAssist() {
		return this.aimAssist;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		final Location location = e.getFrom().clone();
		final Location location2 = e.getTo().clone();
		final Player p = e.getPlayer();
		if (p == null) {
			return;
		}
		final double d = Math.abs(location.getYaw() - location2.getYaw());
		if (d > 0.0 && d < 360.0) {
			if (AimAssistA.getFrac(d) == 0.0) {
				this.setAimAssest(this.getAimAssist() + 100);
				if (this.getAimAssist() > 2000) {

					if (getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
							|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
						return;
					}
					getAntiCheat().logCheat(this, p, "(Aimbot)", "(Type: A)");
					this.setAimAssest(0);
				}
			} else {
				this.setAimAssest(this.getAimAssist() - 21);
			}
		}
	}
}