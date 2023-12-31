package me.andrewdisco.rektanticheat.checks.movement.ascension;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.utils.CheatUtil;
import me.andrewdisco.rektanticheat.utils.MathUtil;
import me.andrewdisco.rektanticheat.utils.ServerUtil;
import me.andrewdisco.rektanticheat.utils.TimeUtil;

public class AscensionA extends Check {

	public static Map<UUID, Map.Entry<Long, Double>> AscensionTicks;
	public static Map<UUID, Double> velocity;

	public AscensionA(AntiCheat AntiCheat) {
		super("AscensionA", "Ascension",  CheckType.Movement, true, true, false, true, false, 4, 1, 600000L, AntiCheat);

		AscensionTicks = new HashMap<>();
		velocity = new HashMap<>();
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		if (e.getFrom().getY() >= e.getTo().getY()
				|| !getAntiCheat().isEnabled()
				|| p.getAllowFlight()
				|| p.getVehicle() != null
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()
				|| !TimeUtil.elapsed(getAntiCheat().LastVelocity.getOrDefault(p.getUniqueId(), 0L), 4200L)) {
			return;
		}

		if (!ServerUtil.isBukkitVerison("1_8")
				&&!ServerUtil.isBukkitVerison("1_7")) {
			if (p.hasPotionEffect(PotionEffectType.getByName("LEVITATION"))) {
				return;
			}
		}
		long Time = System.currentTimeMillis();
		double TotalBlocks = 0.0D;
		if (AscensionTicks.containsKey(p.getUniqueId())) {
			Time = AscensionTicks.get(p.getUniqueId()).getKey().longValue();
			TotalBlocks = AscensionTicks.get(p.getUniqueId()).getValue().doubleValue();
		}
		final long MS = System.currentTimeMillis() - Time;
		final double OffsetY = MathUtil.offset(MathUtil.getVerticalVector(e.getFrom().toVector()),
				MathUtil.getVerticalVector(e.getTo().toVector()));
		if (OffsetY > 0.0D) {
			TotalBlocks += OffsetY;
		}
		final Location a = p.getLocation().subtract(0.0D, 1.0D, 0.0D);
		if (CheatUtil.blocksNear(a)) {
			TotalBlocks = 0.0D;
		}
		double Limit = 1.05D;
		if (p.hasPotionEffect(PotionEffectType.JUMP)) {
			for (final PotionEffect effect : p.getActivePotionEffects()) {
				if (effect.getType().equals(PotionEffectType.JUMP)) {
					final int level = effect.getAmplifier() + 1;
					Limit += (Math.pow(level + 4.2D, 2.0D) / 16.0D) + 0.3;
					break;
				}
			}
		}
		if (TotalBlocks > Limit) {
			if (MS > 250L) {
				if (velocity.containsKey(p.getUniqueId())) {
					getAntiCheat().logCheat(this, p, "Flew up " + MathUtil.trim(1, TotalBlocks) + " blocks", "(Type: A)");
				}
				Time = System.currentTimeMillis();
			}
		} else {
			Time = System.currentTimeMillis();
		}
		AscensionTicks.put(p.getUniqueId(),
				new AbstractMap.SimpleEntry<>(Time, TotalBlocks));
	}
}