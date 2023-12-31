package me.andrewdisco.rektanticheat.checks.other;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.utils.PlayerUtil;
import me.andrewdisco.rektanticheat.utils.VelocityUtil;

public class BlockInteractD extends Check {
	public BlockInteractD(AntiCheat AntiCheat) {
		super("BlockInteractD", "BI", CheckType.Other, true, true, false, true, false, 20, 4, 1000L, AntiCheat);
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onPlaceBlock(BlockPlaceEvent e) {
		final Player p = e.getPlayer();
		if (p.getAllowFlight()
				|| p.getVehicle() != null
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()
				|| !getAntiCheat().isEnabled()
				|| p.getGameMode().equals(GameMode.CREATIVE)
				|| VelocityUtil.didTakeVelocity(p)) {
			return;
		}

		final double x = PlayerUtil.getEff(p);
		if (e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().subtract(0.0, 1.0, 0.0)).getType() == Material.AIR) {

			if (!p.isSneaking() && !p.isFlying() && groundAround(p.getLocation()) && e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation().subtract(0.0, 1.0, 0.0)).getType() == Material.AIR && p.getWorld().getBlockAt(p.getLocation().subtract(0.0, 1.0, 0.0)).equals(e.getBlock())) {
				if (x != 0) {
					getAntiCheat().logCheat(this, p, null, "(Type: D)");
				}
			}
		}
	}
	private static boolean groundAround(Location loc) {
		for (int radius = 2, x = -radius; x < radius; ++x) {
			for (int y = -radius; y < radius; ++y) {
				for (int z = -radius; z < radius; ++z) {
					final Material mat = loc.getWorld().getBlockAt(loc.add(x, y, z)).getType();
					if (mat.isSolid() || mat == Material.WATER || mat == Material.STATIONARY_WATER || mat == Material.LAVA || mat == Material.STATIONARY_LAVA) {
						loc.subtract(x, y, z);
						return true;
					}
					loc.subtract(x, y, z);
				}
			}
		}
		return false;
	}
}