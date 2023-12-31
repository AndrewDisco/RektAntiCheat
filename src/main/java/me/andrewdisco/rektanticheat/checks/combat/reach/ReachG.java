package me.andrewdisco.rektanticheat.checks.combat.reach;

import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.utils.MathUtil;

public class ReachG extends Check {
	public ReachG(AntiCheat AntiCheat) {
		super("ReachG", "Reach",  CheckType.Combat, true, true, false, false, false, 20, 1, 600000L, AntiCheat);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAttack(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			if (event.getEntity().getType() == EntityType.PLAYER) {
				final Player player = (Player) event.getDamager();
				if (player == null) {
					return;
				}
				if (player.getGameMode() != GameMode.CREATIVE) {
					final double distance = MathUtil.getDistance3D(player.getLocation(), event.getEntity().getLocation());
					final int ping = getAntiCheat().getLag().getPing(player);
					final double tps = getAntiCheat().getLag().getTPS();
					final String dist = Double.toString(distance).substring(0, 3);
					double maxReach = 3.9;
					MathUtil.Distance(player.getLocation(), event.getEntity().getLocation());
					if (event.getEntity() instanceof Player) {
						final Player p = (Player) event.getEntity();
						if (p.getAllowFlight() && p.isFlying()) {
							maxReach += 1.1;
						}
						if (getAntiCheat().getLag().getPing(p) > 0) {
							maxReach += 0.00528169 * getAntiCheat().getLag().getPing(p);
						}
					}
					if (player.hasPotionEffect(PotionEffectType.SPEED)) {
						if (MathUtil.getxDiff() > maxReach + 1 || MathUtil.getzDiff() > maxReach + 1) {
							if (player != null) {
								getAntiCheat().logCheat(this, player, "Interact too far away; distance: " + dist + "; MaxReach: " + maxReach + "; Ping: " + ping + "; TPS: " + tps, "(Type: G)");
							}
						}
					} else {
						if (MathUtil.getxDiff() > maxReach || MathUtil.getzDiff() > maxReach) {
							if (player != null) {
								event.setCancelled(true);
								getAntiCheat().logCheat(this, player, "Interact too far away; distance: " + dist + "; MaxReach: " + maxReach + "; Ping: " + ping + "; TPS: " + tps, "(Type: G)");
							}
						}
					}
				}

			}
		}
	}
}