package me.andrewdisco.rektanticheat.checks.other;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.utils.PlayerUtil;
import me.andrewdisco.rektanticheat.utils.ServerUtil;

public class ChangeA extends Check {
	private final List<UUID> built = new ArrayList<>();
	public static List<UUID> falling = new ArrayList<>();

	public ChangeA(AntiCheat AntiCheat) {
		super("ChangeA", "Change", CheckType.Other, true, false, false, false, true, 10, 1, 600000L, AntiCheat);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		if (!this.isEnabled()) {
			return;
		}
		final Player p = e.getPlayer();
		final UUID u = p.getUniqueId();
		if (p.getAllowFlight()) {
			return;
		}
		if (p.isInsideVehicle()) {
			return;
		}
		if (!p.getNearbyEntities(1.0, 1.0, 1.0).isEmpty()) {
			return;
		}
		if (this.built.contains(u)) {
			return;
		}
		int n = 0;
		final int n2 = 5;
		if (!ServerUtil.isBukkitVerison("1_13") && !ServerUtil.isBukkitVerison("1_7")) {
			if (!(PlayerUtil.isOnTheGround(p) || ServerUtil.isOnBlock(p, 0, new Material[]{Material.CARPET}) || ServerUtil.isHoveringOverWater(p, 0) || p.getLocation().getBlock().getType() != Material.AIR)) {
				if (e.getFrom().getY() > e.getTo().getY()) {
					if (!ChangeA.falling.contains(u)) {
						ChangeA.falling.add(u);
					}
				} else {
					n = e.getTo().getY() > e.getFrom().getY() ? (ChangeA.falling.contains(u) ? ++n : --n) : --n;
				}
			} else {
				ChangeA.falling.remove(u);
			}
			if (n > n2) {
				if (getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
						|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
					return;
				}
				getAntiCheat().logCheat(this, p, "[1]", "(Type: A)");
				n = 0;
				ChangeA.falling.remove(u);
			}
		}
		else {

			if (!(PlayerUtil.isOnGround(e,p) || ServerUtil.isOnBlock(p, 0, new Material[]{Material.CARPET}) || ServerUtil.isHoveringOverWater(p, 0) || p.getLocation().getBlock().getType() != Material.AIR)) {
				if (e.getFrom().getY() > e.getTo().getY()) {
					if (!ChangeA.falling.contains(u)) {
						ChangeA.falling.add(u);
					}
				} else {
					n = e.getTo().getY() > e.getFrom().getY() ? (ChangeA.falling.contains(u) ? ++n : --n) : --n;
				}
			} else {
				ChangeA.falling.remove(u);
			}
			if (n > n2) {
				if (getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
						|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
					return;
				}
				getAntiCheat().logCheat(this, p, "[2]", "(Type: A)");
				n = 0;
				ChangeA.falling.remove(u);
			}
		}
	}

	@SuppressWarnings("unused")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onAttack(BlockPlaceEvent e) {
		if (e.getPlayer() instanceof Player) {
			final Player p = e.getPlayer();
			final UUID u = p.getUniqueId();
			this.built.add(u);
			Bukkit.getScheduler().runTaskLater(AntiCheat.Instance, () -> {
				final boolean bl = this.built.remove(u);
			}
			, 60);
		}
	}
}