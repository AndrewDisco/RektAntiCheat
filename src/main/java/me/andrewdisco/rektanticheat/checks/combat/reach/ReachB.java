package me.andrewdisco.rektanticheat.checks.combat.reach;

import java.util.AbstractMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.other.Latency;
import me.andrewdisco.rektanticheat.utils.MathUtil;
import me.andrewdisco.rektanticheat.utils.PlayerUtil;
import me.andrewdisco.rektanticheat.utils.TimeUtil;

public class ReachB extends Check {

	public static Map<Player, Integer> count;
	public static Map<Player, Map.Entry<Double, Double>> offsets;

	public ReachB(AntiCheat AntiCheat) {
		super("ReachB", "Reach",  CheckType.Combat, true, true, false, true, false, 7, 1, 30000L, AntiCheat);

		offsets = new WeakHashMap<>();
		count = new WeakHashMap<>();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onMove(PlayerMoveEvent e) {
		if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) {
			return;
		}
		final double OffsetXZ = MathUtil.offset(MathUtil.getHorizontalVector(e.getFrom().toVector()),
				MathUtil.getHorizontalVector(e.getTo().toVector()));
		final double horizontal = Math.sqrt(Math.pow(e.getTo().getX() - e.getFrom().getX(), 2.0)
				+ Math.pow(e.getTo().getZ() - e.getFrom().getZ(), 2.0));
		offsets.put(e.getPlayer(),
				new AbstractMap.SimpleEntry<>(Double.valueOf(OffsetXZ), Double.valueOf(horizontal)));
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onDamage(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)
				|| !(e.getEntity() instanceof Player)
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()) {
			return;
		}
		final Player d = (Player) e.getDamager();
		if (d == null) {
			return;
		}
		final Player p = (Player) e.getEntity();
		if (d.getAllowFlight()
				|| p.getAllowFlight()
				|| p.getGameMode().equals(GameMode.CREATIVE)
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(d) > getAntiCheat().getPingCancel()) {
			return;
		}
		final double Reach = MathUtil.trim(2, PlayerUtil.getEyeLocation(d).distance(p.getEyeLocation()) - 0.32);
		final double Reach2 = MathUtil.trim(2, PlayerUtil.getEyeLocation(d).distance(p.getEyeLocation()) - 0.32);
		double Difference;

		if (!count.containsKey(d)) {
			count.put(d, 0);
		}

		final int Count = count.get(d);
		long Time = System.currentTimeMillis();
		double maxReach = 3.1;
		final double YawDifference = Math.abs(d.getEyeLocation().getYaw() - p.getEyeLocation().getYaw());
		double speedToVelocityDif = 0;
		double offsets = 0.0D;

		double lastHorizontal = 0.0D;
		if (ReachB.offsets.containsKey(d)) {
			offsets = (ReachB.offsets.get(d)).getKey().doubleValue();
			lastHorizontal = (ReachB.offsets.get(d)).getValue().doubleValue();
		}
		if (Latency.getLag(d) > 92 || Latency.getLag(p) > 92) {
			return;
		}
		speedToVelocityDif = Math.abs(offsets - p.getVelocity().length());
		maxReach += (YawDifference * 0.001);
		maxReach += lastHorizontal * 1.5;
		maxReach += speedToVelocityDif * 0.08;
		if (d.getLocation().getY() > p.getLocation().getY()) {
			Difference = d.getLocation().getY() - p.getLocation().getY();
			maxReach += Difference / 2.5;
		} else if (p.getLocation().getY() > d.getLocation().getY()) {
			Difference = p.getLocation().getY() - d.getLocation().getY();
			maxReach += Difference / 2.5;
		}
		maxReach += d.getWalkSpeed() <= 0.2 ? 0 : d.getWalkSpeed() - 0.2;

		final int PingD = this.getAntiCheat().getLag().getPing(d);
		final int PingP = this.getAntiCheat().getLag().getPing(p);
		maxReach += ((PingD + PingP) / 2) * 0.0024;
		if(PingD > 400) {
			maxReach += 1.0D;
		}
		if (TimeUtil.elapsed(Time, 10000)) {
			count.remove(d);
			Time = System.currentTimeMillis();
		}
		if (Reach > maxReach) {
			this.dumplog(d,
					"Logged for Reach Type B; Count Increase (+1); Reach: " + Reach2 + ", maxReach: " + maxReach + ", Damager Velocity: "
							+ d.getVelocity().length() + ", " + "Player Velocity: "
							+ p.getVelocity().length() + "; New Count: " + Count);
			count.put(d, Count + 1);
		} else {
			if (Count >= -2) {
				count.put(d, Count - 1);
			}
		}
		if (Reach2 > 6) {
			e.setCancelled(true);
		}
		if (Count >= 2 && Reach > maxReach && Reach < 20.0) {
			count.remove(d);
			if (Latency.getLag(p) < 115) {
				getAntiCheat().logCheat(this, d, Reach + " > " + maxReach + " MS: " + PingD + " Velocity Difference: " + speedToVelocityDif, "(Type: B)");

			}
			dumplog(d, "Logged for Reach Type B; Reach: " + Reach2 + " > " + "Max reach:" + maxReach);
			return;
		}
	}
}