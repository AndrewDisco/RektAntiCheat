package me.andrewdisco.rektanticheat.checks.player;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.data.DataPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import me.andrewdisco.rektanticheat.utils.BlockUtil;
import me.andrewdisco.rektanticheat.utils.PlayerUtil;
import me.andrewdisco.rektanticheat.utils.ServerUtil;
import me.andrewdisco.rektanticheat.utils.TimerUtil;
import me.andrewdisco.rektanticheat.utils.VelocityUtil;

public class GroundSpoofA extends Check {
	public GroundSpoofA(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("GroundsSpoofA", "GroundSpoof", CheckType.Player, true, false, false, false, true, 20, 1, 600000L, AntiCheat);
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final DataPlayer data = AntiCheat.getInstance().getDataManager().getData(p);
		if (data != null) {
			if (e.getTo().getY() > e.getFrom().getY()
					|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
					|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
				return;
			}
			if (DataPlayer.lastNearSlime !=null) {
				if (DataPlayer.lastNearSlime.contains(p.getPlayer().getName().toString())) {
					return;
				}
			}
			if (data.isLastBlockPlaced_GroundSpoof()) {
				if (TimerUtil.elapsed(data.getLastBlockPlacedTicks(),500L)) {
					data.setLastBlockPlaced_GroundSpoof(false);
				}
				return;
			}
			if (ServerUtil.isBukkitVerison("1_13")) {
				return;
			}
			if (!ServerUtil.isBukkitVerison("1_8")
					&&!ServerUtil.isBukkitVerison("1_7")) {
				if (p.hasPotionEffect(PotionEffectType.getByName("LEVITATION"))) {
					return;
				}
			}
			final Location to = e.getTo();
			final Location from = e.getFrom();
			final double diff = to.toVector().distance(from.toVector());
			final int dist = PlayerUtil.getDistanceToGround(p);
			if (p.getLocation().add(0,-1.50,0).getBlock().getType() != Material.AIR) {
				data.setGroundSpoofVL(0);
				return;
			}
			if (e.getTo().getY() > e.getFrom().getY() || PlayerUtil.isOnGround4(p) || VelocityUtil.didTakeVelocity(p)) {
				data.setGroundSpoofVL(0);
				return;
			}
			if (!ServerUtil.isBukkitVerison("1_13") && !ServerUtil.isBukkitVerison("1_7") ) {
				if (p.isOnGround() && diff > 0.0 && !PlayerUtil.isOnTheGround(p) && dist >= 2 && e.getTo().getY() < e.getFrom().getY()) {
					if (data.getGroundSpoofVL() >= 4) {
						if (data.getAirTicks() >= 10) {
							getAntiCheat().logCheat(this, p, "[1] Spoofed On-Ground Packet.", "(Type: A)");
						} else {
							getAntiCheat().logCheat(this, p, "[2] Spoofed On-Ground Packet.", "(Type: A)");
						}
					} else {
						data.setGroundSpoofVL(data.getGroundSpoofVL()+1);
					}
				}
			}
			else {
				if (BlockUtil.isSolid(p.getLocation().getBlock())
						|| PlayerUtil.isNearSolid(p)) {
					return;
				}
				if (p.isOnGround() && diff > 0.0 && !PlayerUtil.isOnGround(e,p) && dist >= 2 && e.getTo().getY() < e.getFrom().getY()) {
					if (data.getGroundSpoofVL() >= 4) {
						if (data.getAirTicks() >= 10) {
							getAntiCheat().logCheat(this, p, "[1] Spoofed On-Ground Packet.", "(Type: A)");
						} else {
							getAntiCheat().logCheat(this, p, "[2] Spoofed On-Ground Packet.", "(Type: A)");
						}
					} else {
						data.setGroundSpoofVL(data.getGroundSpoofVL()+1);
					}
				}
			}
		}
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onBlockPlace(BlockPlaceEvent e) {
		final Player p = e.getPlayer();
		final DataPlayer data = AntiCheat.getInstance().getDataManager().getData(p);
		if (!ServerUtil.isBukkitVerison("1_8")
				&& !ServerUtil.isBukkitVerison("1_7")) {
			if (p.hasPotionEffect(PotionEffectType.getByName("LEVITATION"))) {
				return;
			}
		}
		if (data != null) {
			if (!data.isLastBlockPlaced_GroundSpoof()) {
				data.setLastBlockPlaced_GroundSpoof(true);
				data.setLastBlockPlacedTicks(TimerUtil.nowlong());
			}
		}
	}
}