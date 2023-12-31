package me.andrewdisco.rektanticheat.checks.combat.hitbox;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.other.Latency;
import me.andrewdisco.rektanticheat.utils.CheatUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;

public class HitBoxA extends Check {
	public HitBoxA(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("HitBoxA", "HitBox", CheckType.Combat, true, false, false, false, true, 10, 1, 600000L, AntiCheat);
	}

	public static Map<UUID, Integer> count = new HashMap<>();
	public static Map<UUID, Player> lastHit = new HashMap<>();
	public static Map<UUID, Double> yawDif = new HashMap<>();

	@SuppressWarnings("static-access")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onMove(PlayerMoveEvent e) {
		final double yawDif = Math.abs(e.getFrom().getYaw() - e.getTo().getYaw());
		this.yawDif.put(e.getPlayer().getUniqueId(), yawDif);
	}

	@SuppressWarnings("static-access")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onUse(EntityDamageByEntityEvent e) {
		if (e.getCause() != DamageCause.ENTITY_ATTACK) {
			return;
		}
		if (!(e.getEntity() instanceof Player)
				|| !(e.getDamager() instanceof Player)) {
			return;
		}

		final Player player = (Player) e.getDamager();
		if (player == null) {
			return;
		}
		final Player attacked = (Player) e.getEntity();
		if (player.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}

		int Count = 0;
		double yawDif = 0;
		Player lastPlayer = attacked;

		if (lastHit.containsKey(player.getUniqueId())) {
			lastPlayer = lastHit.get(player.getUniqueId());
		}

		if (count.containsKey(player.getUniqueId())) {
			Count = count.get(player.getUniqueId());
		}
		if (this.yawDif.containsKey(player.getUniqueId())) {
			yawDif = this.yawDif.get(player.getUniqueId());
		}

		if (lastPlayer != attacked) {
			lastHit.put(player.getUniqueId(), attacked);
			return;
		}

		final double offset = CheatUtil.getOffsetOffCursor(player, attacked);
		double Limit = 108D;
		final double distance = CheatUtil.getHorizontalDistance(player.getLocation(), attacked.getLocation());
		Limit += distance * 57;
		Limit += (attacked.getVelocity().length() + player.getVelocity().length()) * 64;
		Limit += yawDif * 6;

		if (Latency.getLag(player) > 80 || Latency.getLag(attacked) > 80) {
			return;
		}

		if (offset > Limit) {
			Count++;
		} else {
			Count = Count > 0 ? Count - 1 : Count;
		}

		if (Count > 8) {
			getAntiCheat().logCheat(this, player, "Offset: " + offset + " > " + "Limit: " + Limit, "(Type: A)");
			Count = 0;
		}

		count.put(player.getUniqueId(), Count);
		lastHit.put(player.getUniqueId(), attacked);
	}

}