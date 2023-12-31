package me.andrewdisco.rektanticheat.checks.movement.noslowdown;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.utils.MathUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.andrewdisco.rektanticheat.utils.Color;

public class NoSlowdownA extends Check {

	public static Map<UUID, Map.Entry<Integer, Long>> speedTicks;

	public NoSlowdownA(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("NoSlowdownA", "NoSlowdown", CheckType.Movement, true, false, false, false, true, 10, 1, 600000L, AntiCheat);
		speedTicks = new HashMap<>();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void BowShoot(EntityShootBowEvent e) {
		if (!this.isEnabled()) {
			return;
		}
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		final Player p = (Player) e.getEntity();
		if (p.isInsideVehicle()
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
			return;
		}
		if (p.isSprinting()) {
			getAntiCheat().logCheat(this, p, "Sprinting while bowing.", "(Type: A)");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		if (e.getTo().getX() == e.getFrom().getX() && e.getFrom().getY() == e.getTo().getY()
				&& e.getTo().getZ() == e.getFrom().getZ()) {
			return;
		}
		final Player p = e.getPlayer();
		final double OffsetXZ = MathUtil.offset(MathUtil.getHorizontalVector(e.getFrom().toVector()),
				MathUtil.getHorizontalVector(e.getTo().toVector()));

		if (!p.getLocation().getBlock().getType().equals(Material.WEB)
				|| (OffsetXZ < 0.2)
				|| p.getAllowFlight()
				|| p.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		getAntiCheat().logCheat(this, p, "Offset: " + OffsetXZ, "(Type: A)");
	}

	@SuppressWarnings("unlikely-arg-type")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onInteract(PlayerInteractEvent e) {
		if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& e.getItem() != null) {
			if (e.getItem().equals(Material.EXP_BOTTLE) || e.getItem().getType().equals(Material.GLASS_BOTTLE)
					|| e.getItem().getType().equals(Material.POTION)) {
				return;
			}
			final Player p = e.getPlayer();

			long Time = System.currentTimeMillis();
			int level = 0;
			if (speedTicks.containsKey(p.getUniqueId())) {
				level = speedTicks.get(p.getUniqueId()).getKey().intValue();
				Time = speedTicks.get(p.getUniqueId()).getValue().longValue();
			}
			final double diff = System.currentTimeMillis() - Time;
			level = diff >= 2.0
					? (diff <= 51.0 ? (level += 2)
							: (diff <= 100.0 ? (level += 0) : (diff <= 500.0 ? (level -= 6) : (level -= 12))))
							: ++level;
					final int max = 50;
					if (level > max * 0.9D && diff <= 100.0D) {
						if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
							return;
						}
						getAntiCheat().logCheat(this, p, Color.Red + "Expermintal! " + "Level: " + level + " Ping: " + getAntiCheat().lag.getPing(p), "(Type: A)");
						if (level > max) {
							level = max / 4;
						}
					} else if (level < 0) {
						level = 0;
					}
					speedTicks.put(p.getUniqueId(),
							new AbstractMap.SimpleEntry<>(level, System.currentTimeMillis()));
		}
	}
}