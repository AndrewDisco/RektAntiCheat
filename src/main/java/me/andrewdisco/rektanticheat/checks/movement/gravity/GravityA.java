package me.andrewdisco.rektanticheat.checks.movement.gravity;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.data.DataPlayer;
import me.andrewdisco.rektanticheat.utils.BlockUtil;
import me.andrewdisco.rektanticheat.utils.MathUtil;
import me.andrewdisco.rektanticheat.utils.NewVelocityUtil;
import me.andrewdisco.rektanticheat.utils.PlayerUtil;
import me.andrewdisco.rektanticheat.utils.ServerUtil;
import me.andrewdisco.rektanticheat.utils.SetBackSystem;
import me.andrewdisco.rektanticheat.utils.VelocityUtil;

public class GravityA extends Check {
	public GravityA(AntiCheat AntiCheat) {
		super("GravityA", "Gravity",  CheckType.Movement, true, false, false, false, true, 20, 1, 600000L, AntiCheat);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final DataPlayer data = AntiCheat.getInstance().getDataManager().getData(p);
		if (data != null) {
			final double diff = MathUtil.getVerticalDistance(e.getFrom(), e.getTo());
			final double LastY = data.getLastY_Gravity();
			final double MaxG = 5;
			if (PlayerUtil.wasOnSlime(p)) {
				data.setGravity_VL(0);
				return;
			}
			if (e.getTo().getY() < e.getFrom().getY()) {
				return;
			}
			if (BlockUtil.isHalfBlock(p.getLocation().add(0, -1.50, 0).getBlock())
					|| BlockUtil.isStair(p.getLocation().add(0,1.50,0).getBlock())
					|| PlayerUtil.isNearHalfBlock(p)
					|| BlockUtil.isNearStair(p)
					|| p.getAllowFlight()
					|| DataPlayer.getWasFlying() > 0
					|| e.getPlayer().getVehicle() != null
					|| p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE
					|| PlayerUtil.isOnClimbable(p, 0)
					|| PlayerUtil.wasOnSlime(p)
					|| PlayerUtil.isNearSlime(p)
					|| BlockUtil.isNearLiquid(p)
					|| PlayerUtil.isOnSlime(p.getLocation())
					|| PlayerUtil.isOnClimbable(p, 1) || VelocityUtil.didTakeVelocity(p)
					|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
					|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()
					|| PlayerUtil.isNearSlime(e.getFrom())
					|| PlayerUtil.isNearSlime(e.getTo())
					|| p.getGameMode().equals(GameMode.CREATIVE)
					|| NewVelocityUtil.didTakeVel(p)
					|| PlayerUtil.wasOnSlime(p)) {
				data.setGravity_VL(0);
				return;
			}
			if (DataPlayer.lastNearSlime !=null) {
				if (DataPlayer.lastNearSlime.contains(p.getPlayer().getName().toString())) {
					data.setGravity_VL(0);
					return;
				}
			}
			if (!ServerUtil.isBukkitVerison("1_8")
					&&!ServerUtil.isBukkitVerison("1_7")) {
				if (p.hasPotionEffect(PotionEffectType.getByName("LEVITATION"))) {
					data.setGravity_VL(0);
					return;
				}
			}
			if (p.getLocation().getBlock().getType() != Material.CHEST &&
					p.getLocation().getBlock().getType() != Material.TRAPPED_CHEST && p.getLocation().getBlock().getType() != Material.ENDER_CHEST && data.getAboveBlockTicks() == 0) {
				if (!PlayerUtil.onGround2(p) && !PlayerUtil.isOnGround(e, p) && PlayerUtil.isFlying(e,p)) {
					if ((((ServerUtil.isBukkitVerison("1_7") || ServerUtil.isBukkitVerison("1_8")) && Math.abs(p.getVelocity().getY() - LastY) > 0.000001)
							|| (!ServerUtil.isBukkitVerison("1_7") && !ServerUtil.isBukkitVerison("1_8") && Math.abs(p.getVelocity().getY() - diff) > 0.000001))
							&& !PlayerUtil.onGround2(p)
							&& e.getFrom().getY() < e.getTo().getY()
							&& (p.getVelocity().getY() >= 0 || p.getVelocity().getY() < (-0.0784 * 5)) && !VelocityUtil.didTakeVelocity(p) && p.getNoDamageTicks() == 0.0) {
						if (data.getGravity_VL() >= MaxG) {
							getAntiCheat().logCheat(this, p, "Player's motion was changed to an unexpected value.", "(Type: A)");
							SetBackSystem.setBack(p);
						} else {
							data.setGravity_VL(data.getGravity_VL() + 1);
						}
					} else {
						data.setGravity_VL(0);
					}
				}
			}
			data.setLastY_Gravity(diff);
		}
	}
}