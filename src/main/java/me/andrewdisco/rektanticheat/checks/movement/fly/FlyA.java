package me.andrewdisco.rektanticheat.checks.movement.fly;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.data.DataPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.andrewdisco.rektanticheat.events.SharedEvents;
import me.andrewdisco.rektanticheat.utils.BlockUtil;
import me.andrewdisco.rektanticheat.utils.MathUtil;
import me.andrewdisco.rektanticheat.utils.NewVelocityUtil;
import me.andrewdisco.rektanticheat.utils.PlayerUtil;
import me.andrewdisco.rektanticheat.utils.ServerUtil;
import me.andrewdisco.rektanticheat.utils.VelocityUtil;

public class FlyA extends Check {
	public FlyA(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("FlyA", "Fly", CheckType.Movement, true, true, false, true, false, 25, 1, 600000L, AntiCheat);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		final Location from = e.getFrom();
		final Location to = e.getTo();
		final Player p = e.getPlayer();
		if (SharedEvents.placedBlock.containsKey(p)) {
			if (System.currentTimeMillis() - SharedEvents.placedBlock.get(p).longValue() < 2000) {
				return;
			}
		}
		if (p.getGameMode().equals(GameMode.CREATIVE)
				|| p.getAllowFlight()
				|| DataPlayer.getWasFlying() > 0
				|| e.getPlayer().getVehicle() != null
				|| p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE
				|| PlayerUtil.isOnClimbable(p, 0)
				|| PlayerUtil.wasOnSlime(p)
				|| PlayerUtil.isNearSlime(p)
				|| BlockUtil.isNearLiquid(p)
				|| PlayerUtil.isNearSlime(e.getFrom())
				|| PlayerUtil.isNearSlime(e.getTo())
				|| PlayerUtil.isOnSlime(p.getLocation())
				|| PlayerUtil.isOnClimbable(p, 1)
				|| VelocityUtil.didTakeVelocity(p)
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
			return;
		}
		if (!ServerUtil.isBukkitVerison("1_8")
				&&!ServerUtil.isBukkitVerison("1_7")) {
			if (p.hasPotionEffect(PotionEffectType.getByName("LEVITATION"))) {
				return;
			}
		}
		if (!ServerUtil.isBukkitVerison("1_13") && !ServerUtil.isBukkitVerison("1_7")) {

			if (PlayerUtil.isInLiquid(p)) {
				return;
			}
		}
		else {
			if (BlockUtil.isNearStair(p)) {
				return;
			}
		}

		final DataPlayer data = AntiCheat.getInstance().getDataManager().getData(p);

		if (data == null) {
			return;
		}
		if (!NewVelocityUtil.didTakeVel(p) && !PlayerUtil.wasOnSlime(p)) {
			final Vector vec = new Vector(to.getX(), to.getY(), to.getZ());
			final double Distance = vec.distance(new Vector(from.getX(), from.getY(), from.getZ()));
			if (p.getFallDistance() == 0.0f && p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR && p.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
				if (PlayerUtil.isNearSlime(from)
						|| PlayerUtil.isNearSlime(to)) {
					return;
				}
				if (!ServerUtil.isBukkitVerison("1_13") && !ServerUtil.isBukkitVerison("1_7")) {
					if (Distance > 3.5 && !PlayerUtil.isOnTheGround(p) && e.getTo().getY() > e.getFrom().getY() && e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()) {
						getAntiCheat().logCheat(this, p, "[1] Distance: " + Distance + " To: " + e.getTo().getY() + " From: " + e.getFrom().getY(),  "(Type: C)");
					}
				}
				else {
					if (Distance > 3.5 && !PlayerUtil.isOnGround(e, p) && e.getTo().getY() > e.getFrom().getY() && e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()) {
						getAntiCheat().logCheat(this, p, "[1] Distance: " + Distance + " To: " + e.getTo().getY() + " From: " + e.getFrom().getY(),  "(Type: C)");
					}
				}
			}
		}

		if (!ServerUtil.isBukkitVerison("1_13") && !ServerUtil.isBukkitVerison("1_7")) {
			if (!NewVelocityUtil.didTakeVel(p) && !PlayerUtil.wasOnSlime(p)) {
				if (e.getTo().getY() > e.getFrom().getY() && data.getAirTicks() > 2 && !VelocityUtil.didTakeVelocity(p)) {
					if (!PlayerUtil.isOnGround4(p) && !PlayerUtil.onGround2(p) && !PlayerUtil.isOnTheGround(p)) {
						if (PlayerUtil.getDistanceToGround(p) > 2) {
							if (data.getGoingUp_Blocks() >= 3 && data.getAirTicks() >= 10) {
							} else {
								data.setGoingUp_Blocks(data.getGoingUp_Blocks() + 1);
							}
						} else {
							data.setGoingUp_Blocks(0);
						}
					} else {
						data.setGoingUp_Blocks(0);
					}
				} else if (e.getTo().getY() < e.getFrom().getY()) {
					data.setGoingUp_Blocks(0);
				} else {
					data.setGoingUp_Blocks(0);
				}
			} else {
				data.setGoingUp_Blocks(0);
			}
		}
		else {
			if (!NewVelocityUtil.didTakeVel(p) && !PlayerUtil.wasOnSlime(p)) {
				if (e.getTo().getY() > e.getFrom().getY() && data.getAirTicks() > 2 && !VelocityUtil.didTakeVelocity(p)) {
					if (!PlayerUtil.isOnGround4(p) && !PlayerUtil.onGround2(p) && !PlayerUtil.isOnGround(e, p)) {
						if (PlayerUtil.getDistanceToGround(p) > 2) {
							if (data.getGoingUp_Blocks() >= 3 && data.getAirTicks() >= 10) {
							} else {
								data.setGoingUp_Blocks(data.getGoingUp_Blocks() + 1);
							}
						} else {
							data.setGoingUp_Blocks(0);
						}
					} else {
						data.setGoingUp_Blocks(0);
					}
				} else if (e.getTo().getY() < e.getFrom().getY()) {
					data.setGoingUp_Blocks(0);
				} else {
					data.setGoingUp_Blocks(0);
				}
			} else {
				data.setGoingUp_Blocks(0);
			}
		}
		if (!ServerUtil.isBukkitVerison("1_13") && !ServerUtil.isBukkitVerison("1_7")) {
			if(!PlayerUtil.isOnTheGround(p)) {
				final double distanceToGround = getDistanceToGround(p);
				final double yDiff = MathUtil.getVerticalDistance(e.getFrom(), e.getTo());
				int verbose = data.getFlyHoverVerbose();
				if (PlayerUtil.isNearWeb(p)) {
					return;
				}
				if(distanceToGround > 2) {
					verbose = yDiff == 0 ? verbose + 6 : yDiff < 0.06 ? verbose + 4 : 0;
				} else if(data.getAirTicks() > 7
						&& yDiff < 0.001) {
					verbose+= 2;
				} else {
					verbose = 0;
				}

				if(verbose > 20) {
					getAntiCheat().logCheat(this, p, "[2]", "(Type: A)");
					verbose = 0;
				}
				data.setFlyHoverVerbose(verbose);
			}
		}else {
			if(!PlayerUtil.isOnGround(e, p)) {
				final double distanceToGround = getDistanceToGround(p);
				final double yDiff = MathUtil.getVerticalDistance(e.getFrom(), e.getTo());
				int verbose = data.getFlyHoverVerbose();
				if (PlayerUtil.isNearWeb(p)
						|| p.isSneaking()
						|| BlockUtil.isSolid(p.getLocation().getBlock())
						|| PlayerUtil.isNearSolid(p)
						|| !PlayerUtil.isFlying(e, p)
						|| ServerUtil.isBukkitVerison("1_13")
						|| !PlayerUtil.isFlying2(e, p)) {
					return;
				}
				if(distanceToGround > 2) {
					verbose = yDiff == 0 ? verbose + 6 : yDiff < 0.06 ? verbose + 4 : 0;
				} else if(data.getAirTicks() > 7
						&& yDiff < 0.001) {
					verbose+= 2;
				} else {
					verbose = 0;
				}

				if(verbose > 20) {
					getAntiCheat().logCheat(this, p, "[2]", "(Type: A)");
					verbose = 0;
				}
				data.setFlyHoverVerbose(verbose);
			}
		}
		if (!ServerUtil.isBukkitVerison("1_13") && !ServerUtil.isBukkitVerison("1_7")) {
			if (PlayerUtil.getDistanceToGround(p) >  3) {
				final double OffSet = e.getFrom().getY() - e.getTo().getY();
				long Time = System.currentTimeMillis();
				if (OffSet <= 0.0 || OffSet > 0.16) {
					data.setGlideTicks(0);
					return;
				}
				if (data.getGlideTicks() != 0) {
					Time = data.getGlideTicks();
				}
				final long Millis = System.currentTimeMillis() - Time;
				if (Millis > 200L) {
					if (PlayerUtil.isInLiquid(p)
							|| PlayerUtil.isNearWeb(p)) {
						return;
					}
					if (!BlockUtil.isNearLiquid(p)) {
						getAntiCheat().logCheat(this, p, "[3]", "(Type: A)");

						data.setGlideTicks(0);
					}
				}
				data.setGlideTicks(Time);
			} else {
				data.setGlideTicks(0);
			}
		}
		else {
			if (PlayerUtil.getDistanceToGround(p) >  3) {
				final double OffSet = e.getFrom().getY() - e.getTo().getY();
				long Time = System.currentTimeMillis();
				if (OffSet <= 0.0 || OffSet > 0.16) {
					data.setGlideTicks(0);
					return;
				}
				if (data.getGlideTicks() != 0) {
					Time = data.getGlideTicks();
				}
				final long Millis = System.currentTimeMillis() - Time;
				if (Millis > 200L) {
					if (BlockUtil.isNearLiquid(p)
							|| PlayerUtil.isNearWeb(p)) {
						return;
					}
					if (!BlockUtil.isNearLiquid(p)) {
						getAntiCheat().logCheat(this, p, "[3]", "(Type: A)");

						data.setGlideTicks(0);
					}
				}
				data.setGlideTicks(Time);
			} else {
				data.setGlideTicks(0);
			}
		}
		final double diffY = Math.abs(from.getY() - to.getY());
		final double lastDiffY = data.getLastVelocityFlyY();
		int verboseC = data.getFlyVelocityVerbose();
		final double finalDifference = Math.abs(diffY - lastDiffY);
		if (!ServerUtil.isBukkitVerison("1_13") && !ServerUtil.isBukkitVerison("1_7")) {
			if(finalDifference < 0.08
					&& e.getFrom().getY() < e.getTo().getY()
					&& !PlayerUtil.isOnTheGround(p) && !p.getLocation().getBlock().isLiquid() && !BlockUtil.isNearLiquid(p)
					&& !NewVelocityUtil.didTakeVel(p) && !VelocityUtil.didTakeVelocity(p)) {
				if(++verboseC > 8) {
					if (!PlayerUtil.wasOnSlime(p)) {
						verboseC = 0;
					}
					else {
						getAntiCheat().logCheat(this, p, "[4]", "(Type: A)");
						verboseC = 0;
					}
				}
			} else {
				verboseC = verboseC > 0 ? verboseC - 1 : 0;
			}
			data.setLastVelocityFlyY(diffY);
			data.setFlyVelocityVerbose(verboseC);
		}
		else {
			if(finalDifference < 0.08
					&& e.getFrom().getY() < e.getTo().getY()
					&& !PlayerUtil.isOnGround(e, p) && !p.getLocation().getBlock().isLiquid() && !BlockUtil.isNearLiquid(p)
					&& !NewVelocityUtil.didTakeVel(p) && !VelocityUtil.didTakeVelocity(p)) {
				if(++verboseC > 8) {
					if (!PlayerUtil.wasOnSlime(p)) {
						verboseC = 0;
					}
					else {
						getAntiCheat().logCheat(this, p, "[4]", "(Type: A)");
						verboseC = 0;
					}
				}
			} else {
				verboseC = verboseC > 0 ? verboseC - 1 : 0;
			}
			data.setLastVelocityFlyY(diffY);
			data.setFlyVelocityVerbose(verboseC);
		}
	}

	private int getDistanceToGround(Player p){
		final Location loc = p.getLocation().clone();
		final double y = loc.getBlockY();
		int distance = 0;
		for (double i = y; i >= 0; i--){
			loc.setY(i);
			if(loc.getBlock().getType().isSolid() || loc.getBlock().isLiquid()) {
				break;
			}
			distance++;
		}
		return distance;
	}
}