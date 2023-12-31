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

public class ReachO extends Check {
	public ReachO(AntiCheat AntiCheat) {
		super("ReachO", "Reach",  CheckType.Combat, true, true, false, false, false, 7, 1, 30000L, AntiCheat);
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
					final double maxReach = 4.2;
					if (distance > maxReach) {
						if (player.hasPotionEffect(PotionEffectType.SPEED)) {
							if (distance > 4.2 + 1) {
								if (player != null) {
									getAntiCheat().logCheat(this, player, "Over max reach; distance: " + dist + "; Ping: " + ping + "; TPS: " + tps, "(Type: O)");
								}
							}
						} else {
							if (player != null) {
								getAntiCheat().logCheat(this, player, "Over max reach; distance: " + dist + "; Ping: " + ping + "; TPS: " + tps, "(Type: O)");
							}
						}
					}
				}
			}
		}

	}
}