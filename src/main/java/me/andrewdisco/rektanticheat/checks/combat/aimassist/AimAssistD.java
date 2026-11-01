package me.andrewdisco.rektanticheat.checks.combat.aimassist;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Mouse-step check. A real mouse turns the view in whole counts, so every yaw change is a
 * multiple of one step set by the sensitivity (0.15 deg at the default). An aimbot that writes
 * the exact angle to the target has no common step, which AimAssistA (whole degrees) can't see.
 * Over the last WINDOW turns, look for the step: the largest (smallest turn)/k, refitted on the
 * whole window, that every turn is a whole multiple of. None for STREAK turns in a row = flag.
 */
public class AimAssistD extends Check {
	private static final int WINDOW = 12;
	private static final int STREAK = 20;
	private static final double TOL = 0.002;     // deg; float error on a yaw near 1000 deg is ~1e-4
	private static final double MIN_STEP = 0.009; // deg; the lowest sensitivity turns ~0.0096 per count
	private static final double MIN_TURN = 0.001;
	private static final double MAX_TURN = 45.0;

	private final Map<UUID, ArrayDeque<Double>> turns = new HashMap<UUID, ArrayDeque<Double>>();
	private final Map<UUID, Integer> streak = new HashMap<UUID, Integer>();

	public AimAssistD(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("AimAssistD", "AimAssist", CheckType.Combat, true, false, false, false, true, 20, 1, 600000L, AntiCheat);
	}

	/** The common step of the turns in the window, or 0 if there is none. */
	private static double step(ArrayDeque<Double> win) {
		double min = Double.MAX_VALUE;
		double sum = 0;
		for (double d : win) {
			min = Math.min(min, d);
			sum += d;
		}
		for (int k = 1; min / k >= MIN_STEP; k++) {
			final double s0 = min / k;
			long counts = 0;
			for (double d : win) {
				counts += Math.round(d / s0);
			}
			final double s = sum / counts; // refit, so big turns don't multiply the smallest turn's float error
			if (s < MIN_STEP) {
				continue;
			}
			boolean fits = true;
			for (double d : win) {
				if (Math.abs(d / s - Math.round(d / s)) * s >= TOL) {
					fits = false;
					break;
				}
			}
			if (fits) {
				return s;
			}
		}
		return 0;
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		final UUID id = e.getPlayer().getUniqueId();
		this.turns.remove(id);
		this.streak.remove(id);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		if (p == null) {
			return;
		}
		final double d = Math.abs(e.getFrom().getYaw() - e.getTo().getYaw());
		if (d <= MIN_TURN || d >= MAX_TURN) {
			return;
		}
		final UUID id = p.getUniqueId();
		ArrayDeque<Double> win = this.turns.get(id);
		if (win == null) {
			win = new ArrayDeque<Double>();
			this.turns.put(id, win);
		}
		win.addLast(d);
		if (win.size() > WINDOW) {
			win.removeFirst();
		}
		if (win.size() < WINDOW) {
			return;
		}
		if (step(win) != 0) {
			this.streak.put(id, 0);
			return;
		}
		final int n = (this.streak.containsKey(id) ? this.streak.get(id) : 0) + 1;
		if (n < STREAK) {
			this.streak.put(id, n);
			return;
		}
		this.streak.put(id, 0);
		if (getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
			return;
		}
		getAntiCheat().logCheat(this, p, "(Aimbot)", "(Type: D, no mouse step)");
	}
}
