package me.andrewdisco.rektanticheat.checks.movement.speed;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.data.DataPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.andrewdisco.rektanticheat.utils.BlockUtil;
import me.andrewdisco.rektanticheat.utils.MathUtil;
import me.andrewdisco.rektanticheat.utils.NewVelocityUtil;
import me.andrewdisco.rektanticheat.utils.PlayerUtil;
import me.andrewdisco.rektanticheat.utils.TimerUtil;
import me.andrewdisco.rektanticheat.utils.VelocityUtil;

public class SpeedA extends Check {

	public SpeedA(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("SpeedA", "Speed", CheckType.Movement, true, true, false, true, false, 15, 1, 600000L, AntiCheat);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final DataPlayer data = AntiCheat.getInstance().getDataManager().getData(p);
		final Location to = e.getTo();
		final Location from = e.getFrom();
		if (((to.getX() == from.getX() && to.getY() == from.getY() && to.getZ() == from.getZ()))
				|| p.getGameMode().equals(GameMode.CREATIVE)
				|| e.getPlayer().getVehicle() != null
				|| PlayerUtil.isNearIce(p)
				|| DataPlayer.getWasFlying() > 0
				|| PlayerUtil.isNearPiston(p)
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()
				|| PlayerUtil.isNearSlime(p)
				|| PlayerUtil.wasOnSlime(p)){
			return;
		}

		if (data != null) {

			if (data.isSpeed_PistonExpand_Set()) {
				if (TimerUtil.elapsed(data.getSpeed_PistonExpand_MS(), 9900L)) {
					data.setSpeed_PistonExpand_Set(false);
				}
			}
			final double speed = MathUtil.getHorizontalDistance(from, to);
			if(MathUtil.elapsed(data.getLastVelMS(), 3000)) {
				int verbose = data.getSpeedAVerbose();
				final double speedEffect = PlayerUtil.getPotionEffectLevel(p, PotionEffectType.SPEED);
				final double x = 0;
				final double depth = BlockUtil.isNearLiquid(p) ? 0.34 + (PlayerUtil.hasDepthStrider(p) * 0.02) : x;
				double speedAThreshold = (data.getAirTicks() > 0 ? data.getAirTicks() >= 6
						? data.getAirTicks() == 13 ? 0.466 : 0.35 : (0.345 * Math.pow(0.986938064, data.getAirTicks()))
								: data.getGroundTicks() > 5 ? 0.362 : data.getGroundTicks() == 3 ? 0.62 : 0.4)
						+ (data.getAirTicks() > 0 ? (-0.001 * data.getAirTicks() + 0.014) : (0.018 - (data.getGroundTicks() >= 6 ? 0 : data.getGroundTicks() * 0.001)) * speedEffect
								+ depth);
				speedAThreshold = data.getAboveBlockTicks() > 0 ? speedAThreshold + 0.25 : speedAThreshold;
				speedAThreshold = data.getIceTicks() > 0 ? speedAThreshold + 0.14 : speedAThreshold;
				speedAThreshold = data.getIceTicks() > 0 && data.getAboveBlockTicks() > 0 ? speedAThreshold + 0.24 : speedAThreshold;
				speedAThreshold += Math.abs((p.getWalkSpeed()));

				if (DataPlayer.lastNearSlime !=null) {
					if (DataPlayer.lastNearSlime.contains(p.getPlayer().getName().toString())) {
						speedAThreshold += 0.1;
					}
				}
				if(PlayerUtil.isOnStair(p.getLocation())
						|| PlayerUtil.isOnSlab(p.getLocation())) {
					speedAThreshold+= 0.12;
				}
				if (p.getAllowFlight()) {
					speedAThreshold += p.getFlySpeed();
				}

				if (speed > speedAThreshold + 0.0631) {
					verbose += 8;
				} else {
					verbose = verbose > 0 ? verbose - 1 : 0;
				}

				if (verbose > 40) {

					if (((to.getX() == from.getX() && to.getY() == from.getY() && to.getZ() == from.getZ()))
							|| p.getGameMode().equals(GameMode.CREATIVE)
							|| e.getPlayer().getVehicle() != null
							|| PlayerUtil.isNearIce(p)
							|| PlayerUtil.isNearSlime(p)
							|| PlayerUtil.wasOnSlime(p)
							|| p.hasPotionEffect(PotionEffectType.JUMP)
							|| p.hasPotionEffect(PotionEffectType.SPEED)
							|| p.isSprinting()){
						return;
					} else {
						if (!p.getAllowFlight()) {
							getAntiCheat().logCheat(this, p, "[0] - Player Moved Too Fast. Speed: " + speed + "; Threshold: " + speedAThreshold, "(Type: A)");
							verbose = 0;
						}
					}
				}
				data.setSpeedAVerbose(verbose);
			} else {
				data.setSpeedAVerbose(0);
			}

			final Location l = p.getLocation();
			final int x = l.getBlockX();
			final int y = l.getBlockY();
			final int z = l.getBlockZ();
			final Location blockLoc = new Location(p.getWorld(), x, y - 1, z);
			final Location loc = new Location(p.getWorld(), x, y, z);
			final Location loc2 = new Location(p.getWorld(), x, y + 1, z);
			final Location above = new Location(p.getWorld(), x, y + 2, z);
			final Location above3 = new Location(p.getWorld(), x - 1, y + 2, z - 1);
			double MaxAirSpeed = 0.4;
			double maxSpeed = 0.42;
			double MaxSpeedNEW = 0.75;
			if (data.isNearIce()) {
				MaxSpeedNEW = 1.0;
			}
			final double Max = 0.28;
			if (p.hasPotionEffect(PotionEffectType.SPEED)) {
				final int level = PlayerUtil.getPotionEffectLevel(p, PotionEffectType.SPEED);
				if (level > 0) {
					maxSpeed = (maxSpeed * (((level * 20) * 0.011) + 1));
					MaxAirSpeed = (MaxAirSpeed * (((level * 20) * 0.011) + 1));
					maxSpeed = (maxSpeed * (((level * 20) * 0.011) + 1));
					MaxSpeedNEW = (MaxSpeedNEW * (((level * 20) * 0.011) + 1));
				}
			}
			MaxAirSpeed += p.getWalkSpeed() > 0.2 ? p.getWalkSpeed() * 0.8 : 0;
			maxSpeed += p.getWalkSpeed() > 0.2 ? p.getWalkSpeed() * 0.8 : 0;
			if (!PlayerUtil.isOnGround4(p) && speed >= MaxAirSpeed && !data.isNearIce()
					&& blockLoc.getBlock().getType() != Material.ICE && !blockLoc.getBlock().isLiquid()
					&& !loc.getBlock().isLiquid() && blockLoc.getBlock().getType() != Material.PACKED_ICE
					&& above.getBlock().getType() == Material.AIR && above3.getBlock().getType() == Material.AIR
					&& blockLoc.getBlock().getType() != Material.AIR && !NewVelocityUtil.didTakeVel(p) && !BlockUtil.isNearStair(p)) {
				if (!NewVelocityUtil.didTakeVel(p) && PlayerUtil.getDistanceToGround(p) > 4 == false) {
					if (data.getSpeed2Verbose() >= 8 || p.getNoDamageTicks() == 0 == false && !VelocityUtil.didTakeVelocity(p) && !NewVelocityUtil.didTakeVel(p)
							&& p.getLocation().add(0, 1.94, 0).getBlock().getType() != Material.AIR) {
						getAntiCheat().logCheat(this, p, "[1] - Player Moved Too Fast.", "(Type: A)");
					} else {
						data.setSpeed2Verbose(data.getSpeed2Verbose() + 1);
					}
				} else {
					data.setSpeed2Verbose(0);
				}
			}

			final double onGroundDiff = (to.getY() - from.getY());
			if (speed > Max && !PlayerUtil.isAir(p) && onGroundDiff <= -0.4 && p.getFallDistance() <= 0.4 && blockLoc.getBlock().getType() != Material.ICE
					&& e.getTo().getY() != e.getFrom().getY() && blockLoc.getBlock().getType() != Material.PACKED_ICE
					&& loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
					&& above3.getBlock().getType() == Material.AIR && data.getAboveBlockTicks() != 0) {
				getAntiCheat().logCheat(this, p, "[2] - Player Moved Too Fast.", "(Type: A)");
			}

			if (speed > 0.7 && !PlayerUtil.isAir(p) && onGroundDiff <= -0.4 && p.getFallDistance() <= 0.4 && blockLoc.getBlock().getType() != Material.ICE
					&& e.getTo().getY() != e.getFrom().getY() && blockLoc.getBlock().getType() != Material.PACKED_ICE
					&& loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
					&& above3.getBlock().getType() == Material.AIR && !NewVelocityUtil.didTakeVel(p) && !VelocityUtil.didTakeVelocity(p) && !PlayerUtil.hasPistonNear(p) &&
					p.getLocation().getBlock().getType() != Material.PISTON_MOVING_PIECE && p.getLocation().getBlock().getType() != Material.PISTON_BASE
					&& p.getLocation().getBlock().getType() != Material.PISTON_STICKY_BASE && !BlockUtil.isNearPistion(p) && !data.isSpeed_PistonExpand_Set()) {
				if (!data.isSpeed_PistonExpand_Set()) {
					if (data.getSpeed_C_3_Verbose() > 1) {
						getAntiCheat().logCheat(this, p, "[3] - Player Moved Too Fast.", "(Type: A)");
					} else {
						data.setSpeed_C_3_Verbose(data.getSpeed_C_3_Verbose() + 1);
					}

				}
				if (p.hasPotionEffect(PotionEffectType.SPEED)) {
					final int level = PlayerUtil.getPotionEffectLevel(p, PotionEffectType.SPEED);
					if (level > 0) {
						maxSpeed = (maxSpeed * (((level * 20) * 0.011) + 1));
						MaxAirSpeed = (MaxAirSpeed * (((level * 20) * 0.011) + 1));
						maxSpeed = (maxSpeed * (((level * 20) * 0.011) + 1));
						MaxSpeedNEW = (MaxSpeedNEW * (((level * 20) * 0.011) + 1));
					}
				}
				MaxAirSpeed += p.getWalkSpeed() > 0.2 ? p.getWalkSpeed() * 0.8 : 0;
				maxSpeed += p.getWalkSpeed() > 0.2 ? p.getWalkSpeed() * 0.8 : 0;
				if (!PlayerUtil.isOnGround4(p) && speed >= MaxAirSpeed && !data.isNearIce()
						&& blockLoc.getBlock().getType() != Material.ICE && !blockLoc.getBlock().isLiquid()
						&& !loc.getBlock().isLiquid() && blockLoc.getBlock().getType() != Material.PACKED_ICE
						&& above.getBlock().getType() == Material.AIR && above3.getBlock().getType() == Material.AIR
						&& blockLoc.getBlock().getType() != Material.AIR && !NewVelocityUtil.didTakeVel(p) && !BlockUtil.isNearStair(p)) {
					if (!NewVelocityUtil.didTakeVel(p) && PlayerUtil.getDistanceToGround(p) > 4 == false) {
						if (data.getSpeed2Verbose() >= 8 || p.getNoDamageTicks() == 0 == false && !VelocityUtil.didTakeVelocity(p) && !NewVelocityUtil.didTakeVel(p)
								&& p.getLocation().add(0, 1.94, 0).getBlock().getType() != Material.AIR) {
							getAntiCheat().logCheat(this, p, "[4] - Player Moved Too Fast.", "(Type: A)");
						} else {
							data.setSpeed2Verbose(data.getSpeed2Verbose() + 1);
						}
					} else {
						data.setSpeed2Verbose(0);
					}
					boolean speedPot = false;
					for (final PotionEffect effect : p.getActivePotionEffects()) {
						if (effect.getType().equals(PotionEffectType.SPEED)) {
							speedPot = true;
						}
					}
					if (speed > 0.29 && PlayerUtil.isOnTheGround(p) && !data.isNearIce() && !BlockUtil.isNearStair(p) && !NewVelocityUtil.didTakeVel(p) && !speedPot) {
						if (data.getSpeed_OnGround_Verbose() >= 5) {
						}

						if (speed > Max && !PlayerUtil.isAir(p) && onGroundDiff <= -0.4 && p.getFallDistance() <= 0.4 && blockLoc.getBlock().getType() != Material.ICE
								&& e.getTo().getY() != e.getFrom().getY() && blockLoc.getBlock().getType() != Material.PACKED_ICE
								&& loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
								&& above3.getBlock().getType() == Material.AIR && data.getIceTicks() == 0 && !PlayerUtil.isNearIce(p)) {
							getAntiCheat().logCheat(this, p, "[5] - Player Moved Too Fast.", "(Type: A)");
						}

						if (speed > 0.7 && !PlayerUtil.isAir(p) && onGroundDiff <= -0.4 && p.getFallDistance() <= 0.4 && blockLoc.getBlock().getType() != Material.ICE
								&& e.getTo().getY() != e.getFrom().getY() && blockLoc.getBlock().getType() != Material.PACKED_ICE
								&& loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
								&& above3.getBlock().getType() == Material.AIR && !NewVelocityUtil.didTakeVel(p) && !VelocityUtil.didTakeVelocity(p) && !PlayerUtil.hasPistonNear(p) &&
								p.getLocation().getBlock().getType() != Material.PISTON_MOVING_PIECE && p.getLocation().getBlock().getType() != Material.PISTON_BASE
								&& p.getLocation().getBlock().getType() != Material.PISTON_STICKY_BASE && !BlockUtil.isNearPistion(p)) {
							getAntiCheat().logCheat(this, p, "[6] - Player Moved Too Fast.", "(Type: A)");

						}
					}
				}
			}
		}
	}
}