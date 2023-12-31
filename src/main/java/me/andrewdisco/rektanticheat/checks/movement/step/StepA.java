package me.andrewdisco.rektanticheat.checks.movement.step;

import java.util.ArrayList;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import me.andrewdisco.rektanticheat.utils.BlockUtil;
import me.andrewdisco.rektanticheat.utils.CheatUtil;
import me.andrewdisco.rektanticheat.utils.MathUtil;
import me.andrewdisco.rektanticheat.utils.PlayerUtil;
import me.andrewdisco.rektanticheat.utils.ServerUtil;

public class StepA extends Check {
	double stepHeight;

	public StepA(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("StepA", "Step", CheckType.Movement, true, true, false, true, false, 7, 1, 90000L, AntiCheat);
	}

	private boolean isOnGround(Player p) {
		if (PlayerUtil.isOnClimbable(p, 0)) {
			return false;
		}
		if (p.getVehicle() != null) {
			return false;
		}
		if (!ServerUtil.isBukkitVerison("1_8")
				&& !ServerUtil.isBukkitVerison("1_7")) {
			if (p.hasPotionEffect(PotionEffectType.getByName("LEVITATION"))) {
				return true;
			}
		}
		Material type = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
		if ((type != Material.AIR) && (type.isBlock()) && (type.isSolid()) && (type != Material.LADDER)
				&& (type != Material.VINE)) {
			return true;
		}
		Location a = p.getLocation().clone();
		a.setY(a.getY() - 0.5D);
		type = a.getBlock().getType();
		if ((type != Material.AIR) && (type.isBlock()) && (type.isSolid()) && (type != Material.LADDER)
				&& (type != Material.VINE)) {
			return true;
		}
		a = p.getLocation().clone();
		a.setY(a.getY() + 0.5D);
		type = a.getBlock().getRelative(BlockFace.DOWN).getType();
		if ((type != Material.AIR) && (type.isBlock()) && (type.isSolid()) && (type != Material.LADDER)
				&& (type != Material.VINE)) {
			return true;
		}
		if (CheatUtil.isBlock(p.getLocation().getBlock().getRelative(BlockFace.DOWN),
				new Material[] { Material.FENCE, Material.FENCE_GATE, Material.COBBLE_WALL, Material.LADDER })) {
			return true;
		}
		return false;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();

		if (!getAntiCheat().isEnabled()
				|| !isOnGround(p)
				|| p.getAllowFlight()
				|| p.hasPotionEffect(PotionEffectType.JUMP)
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()
				|| getAntiCheat().LastVelocity.containsKey(p.getUniqueId())
				|| PlayerUtil.isOnClimbable(p, 0)
				|| CheatUtil.slabsNear(p.getLocation())
				|| PlayerUtil.isNearSlime(p.getLocation())
				|| PlayerUtil.isNearSlime(e.getFrom())
				|| PlayerUtil.isNearSlime(e.getTo())
				|| p.getLocation().getBlock().getType().equals(Material.WATER)
				|| p.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER)) {
			return;
		}

		if (!ServerUtil.isBukkitVerison("1_8")
				&&!ServerUtil.isBukkitVerison("1_7")) {
			if (p.hasPotionEffect(PotionEffectType.getByName("LEVITATION"))) {
				return;
			}
		}
		final double yDist = e.getTo().getY() - e.getFrom().getY();
		if (yDist < 0) {
			return;
		}
		final double YSpeed = MathUtil.offset(MathUtil.getVerticalVector(e.getFrom().toVector()),
				MathUtil.getVerticalVector(e.getTo().toVector()));
		if (yDist > 0.95) {
			this.dumplog(p, "Logged for Step Type A; Height (Logged): " + yDist);
			this.getAntiCheat().logCheat(this, p, "[1] " + Math.round(yDist) + " blocks", "(Type: A)");
			return;
		}
		if (((YSpeed == 0.25D || (YSpeed >= 0.58D && YSpeed < 0.581D)) && yDist > 0.0D
				|| (YSpeed > 0.2457D && YSpeed < 0.24582D) || (YSpeed > 0.329 && YSpeed < 0.33))
				&& !p.getLocation().clone().subtract(0.0D, 0.1, 0.0D).getBlock().getType().equals(Material.SNOW)) {
			this.getAntiCheat().logCheat(this, p, "[2] Speed: " + YSpeed + " Block: " + p.getLocation().clone().subtract(0.0D, 0.1D, 0.0D).getBlock().getType().toString(), "(Type: A)");
			return;
		}
		final ArrayList<Block> blocks = BlockUtil.getBlocksAroundCenter(p.getLocation(), 1);
		for (final Block block : blocks) {
			if (block.getType().isSolid()) {
				if ((YSpeed >= 0.321 && YSpeed < 0.322)) {
					this.getAntiCheat().logCheat(this, p, "[3] Speed: " + YSpeed, "(Type: A)");
					return;
				}
			}
		}
	}
}