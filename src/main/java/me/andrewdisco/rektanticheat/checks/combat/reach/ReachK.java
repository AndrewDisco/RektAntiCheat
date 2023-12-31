package me.andrewdisco.rektanticheat.checks.combat.reach;

import org.bukkit.GameMode;
import org.bukkit.Location;
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

public class ReachK extends Check {
	public ReachK(AntiCheat AntiCheat) {
		super("ReachK", "Reach",  CheckType.Combat, true, false, false, false, false, 7, 1, 30000L, AntiCheat);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			if (event.getEntity().getType() == EntityType.PLAYER) {
				final Player player = (Player) event.getDamager();
				if (player == null) {
					return;
				}
				final Player target = (Player) event.getEntity();
				if (player.getGameMode() != GameMode.CREATIVE) {
					final Location entityLoc = target.getLocation().add(0.0D, target.getEyeHeight(), 0.0D);
					final Location playerLoc = player.getLocation().add(0.0D, player.getEyeHeight(), 0.0D);
					double distance = MathUtil.getDistance3D(entityLoc, playerLoc);
					final int ping = getAntiCheat().getLag().getPing(player);
					double maxReach = 4.3;
					if(ping > 100) {
						distance+=0.2;
					} else if(ping > 200) {
						distance+=0.3;
					} else if(ping > 300) {
						distance+=0.4;
					} else if(ping > 400) {
						distance+=0.5;
					} else if(ping > 500) {
						distance+=0.6;
					}
					if (event.getEntity() instanceof Player) {
						final Player p = (Player) event.getEntity();
						if (p.getAllowFlight()) {
							maxReach += 0.7;
						}
						if (getAntiCheat().getLag().getPing(p) > 0) {
							maxReach += 0.00528169 * getAntiCheat().getLag().getPing(p);
						}
					}
					final double tps = getAntiCheat().getLag().getTPS();
					final String dist = Double.toString(distance).substring(0, 3);
					if (distance > maxReach) {
						if (player.hasPotionEffect(PotionEffectType.SPEED)) {
							if (distance > maxReach + 1.0) {
								if (player != null) {
									getAntiCheat().logCheat(this, player, "Too high hit range; distance: " + dist + "; MaxReach: " + maxReach + "; Ping: " + ping + "; TPS: " + tps, "(Type: K)");
								}
							}
						} else {
							getAntiCheat().logCheat(this, player, "Too high hit range; distance: " + dist + "; MaxReach: " + maxReach + "; Ping: " + ping + "; TPS: " + tps, "(Type: K)");
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
}