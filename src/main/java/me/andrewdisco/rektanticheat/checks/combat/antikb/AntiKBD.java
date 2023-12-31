package me.andrewdisco.rektanticheat.checks.combat.antikb;

import java.util.HashMap;
import java.util.Map;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.utils.BlockUtil;
import me.andrewdisco.rektanticheat.utils.Color;
import me.andrewdisco.rektanticheat.utils.ServerUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public class AntiKBD extends Check {
	public static Map<Player, Long> lastVelocity = new HashMap<>();
	public static Map<Player, Integer> awaitingVelocity = new HashMap<>();
	public static Map<Player, Double> totalMoved = new HashMap<>();

	public AntiKBD(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("AntiKBD", "AntiKB",  CheckType.Combat, true, false, false, false, true, 30, 3, 250000L, AntiCheat);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		double zLoc;
		double xLoc;
		final Player p = e.getPlayer();
		if (p == null) {
			return;
		}
		if (ServerUtil.isOnBlock(p, 0, new Material[]{Material.WEB})
				|| ServerUtil.isOnBlock(p, 1, new Material[]{Material.WEB})
				|| ServerUtil.isHoveringOverWater(p, 1)
				|| ServerUtil.isHoveringOverWater(p, 0)
				|| p.getAllowFlight()
				|| BlockUtil.isSolid(BlockUtil.getBlockBehindPlayer(p))
				|| p.isDead()
				|| p.getGameMode().equals(GameMode.CREATIVE)
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
			return;
		}
		int awaitingVelocity = 0;
		if (AntiKBD.awaitingVelocity.containsKey(p)) {
			awaitingVelocity = AntiKBD.awaitingVelocity.get(p);
		}
		long lastVelocity = 0;
		if (AntiKBD.lastVelocity.containsKey(p)) {
			lastVelocity = AntiKBD.lastVelocity.get(p);
		}
		if (p.getLastDamageCause() == null || p.getLastDamageCause().getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && p.getLastDamageCause().getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
			awaitingVelocity = 0;
		}
		if (System.currentTimeMillis() - lastVelocity > 2000 && awaitingVelocity > 0) {
			--awaitingVelocity;
		}
		double totalMoved = 0.0;
		if (AntiKBD.totalMoved.containsKey(p)) {
			totalMoved = AntiKBD.totalMoved.get(p);
		}
		zLoc = Math.abs(e.getTo().getZ() - e.getFrom().getZ());
		xLoc = Math.abs(e.getTo().getX() - e.getFrom().getX());
		if (xLoc > 0.0
				|| zLoc > 0.0) {
			totalMoved += zLoc + xLoc;
		}
		int awaitingVelocity2 = 0;
		final int awaitingVelocity3 = 1;
		if (awaitingVelocity > 0) {
			if (totalMoved < 0.3) {
				awaitingVelocity2 += 9;
			} else {
				awaitingVelocity2 = 0;
				totalMoved = 0.0;
				--awaitingVelocity;
			}
			if (ServerUtil.isOnGround(p, -1) || ServerUtil.isOnGround(p, -2) || ServerUtil.isOnGround(p, -3)) {
				awaitingVelocity2 -= 9;
			}
		}
		if (awaitingVelocity2 > awaitingVelocity3) {
			if (totalMoved == 0.0) {
				getAntiCheat().logCheat(this, p, Color.Red + "[1] horizontal", "(Type: D)");
			} else {
				if (getAntiCheat().getLag().getPing(p) > 220) {
					return;
				}
				getAntiCheat().logCheat(this, p, Color.Red + "[2] horizontal", "(Type: D)");
			}
			awaitingVelocity2 = 0;
			totalMoved = 0.0;
			--awaitingVelocity;
		}
		AntiKBD.awaitingVelocity.put(p, awaitingVelocity);
		AntiKBD.totalMoved.put(p, totalMoved);
	}

	@SuppressWarnings( "unused" )
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void Velocity(PlayerVelocityEvent e) {
		double vio;
		long lastVelocity;
		final Player p = e.getPlayer();
		if (ServerUtil.isOnBlock(p, 0, new Material[]{Material.WEB}) || ServerUtil.isOnBlock(p, 1, new Material[]{Material.WEB})) {
			return;
		}
		if (ServerUtil.isHoveringOverWater(p, 1) || ServerUtil.isHoveringOverWater(p, 0)) {
			return;
		}
		if (ServerUtil.isOnGround(p, -1) || ServerUtil.isOnGround(p, -2) || ServerUtil.isOnGround(p, -3)) {
			return;
		}
		if (p.getAllowFlight()) {
			return;
		}
		if (AntiKBD.lastVelocity.containsKey(p) && (lastVelocity = System.currentTimeMillis() - AntiKBD.lastVelocity.get(p)) < 500) {
			return;
		}
		final Vector vector = e.getVelocity();
		final double zLoc = Math.abs(vector.getZ());
		final double xLoc = Math.abs(vector.getX());
		if (zLoc > 0.0 && (vio = ((int)(Math.pow(zLoc + 2.0, 2.0) * 5.0))) > 20.0) {
			if (xLoc > 0.0 && (vio = ((int)(Math.pow(xLoc + 2.0, 2.0) * 5.0))) > 20.0) {
				int awaitingVelocity = 0;
				if (AntiKBD.awaitingVelocity.containsKey(p)) {
					awaitingVelocity = AntiKBD.awaitingVelocity.get(p);
				}
				AntiKBD.awaitingVelocity.put(p, ++awaitingVelocity);
				AntiKBD.lastVelocity.put(p, System.currentTimeMillis());
			}
		}
	}
}