package me.andrewdisco.rektanticheat.checks.combat.fastbow;

import java.util.HashMap;
import java.util.Map;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class FastBowA extends Check {
	public static Map<Player, Long> bowPull;
	public static Map<Player, Integer> count;
	public FastBowA(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("FastBowA", "FastBow", CheckType.Combat, true, true, false, true, false, 7, 1, 600000L, AntiCheat);
		bowPull = new HashMap<>();
		count = new HashMap<>();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	private void Interact(final PlayerInteractEvent e) {
		final Player Player = e.getPlayer();
		if (Player == null) {
			return;
		}
		if (Player.getItemInHand() != null && Player.getItemInHand().getType().equals(Material.BOW)) {
			bowPull.put(Player, System.currentTimeMillis());
		}
	}

	@EventHandler
	private void onShoot(final ProjectileLaunchEvent e) {
		if (!this.isEnabled()) {
			return;
		}
		if (e.getEntity() instanceof Arrow) {
			final Arrow arrow = (Arrow) e.getEntity();
			if (arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
				final Player player = (Player) arrow.getShooter();
				if (bowPull.containsKey(player)) {
					final Long time = System.currentTimeMillis() - bowPull.get(player);
					final double power = arrow.getVelocity().length();
					final Long timeLimit = 300L;
					int Count = 0;
					if (count.containsKey(player)) {
						Count = count.get(player);
					}
					if (power > 2.5 && time < timeLimit) {
						count.put(player, Count + 1);
					} else {
						count.put(player, Count > 0 ? Count - 1 : Count);
					}
					if (Count > 5) {
						getAntiCheat().logCheat(this, player, "Count: > 5 in " + time + " ms", "(Type: A)");
					}
					if (Count > 8) {
						getAntiCheat().logCheat(this, player, "Count: > 8 in " + time + " ms", "(Type: A)");
						count.remove(player);
					}
				}
			}
		}
	}
}