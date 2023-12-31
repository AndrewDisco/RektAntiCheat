package me.andrewdisco.rektanticheat.checks.movement.spider;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.data.DataPlayer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.events.SharedEvents;
import me.andrewdisco.rektanticheat.utils.BlockUtil;
import me.andrewdisco.rektanticheat.utils.CheatUtil;
import me.andrewdisco.rektanticheat.utils.Color;
import me.andrewdisco.rektanticheat.utils.MathUtil;
import me.andrewdisco.rektanticheat.utils.PlayerUtil;
import me.andrewdisco.rektanticheat.utils.ServerUtil;
import me.andrewdisco.rektanticheat.utils.VelocityUtil;

public class SpiderA extends Check {

	public SpiderA(AntiCheat AntiCheat) {
		super("SpiderA", "Spider", CheckType.Movement, true, true, false, true, false, 10, 1, 10000L, AntiCheat);
	}
	public static Map<UUID, Map.Entry<Long, Double>> AscensionTicks = new HashMap<>();

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final UUID u = p.getUniqueId();
		if (SharedEvents.placedBlock.containsKey(p)) {
			if (System.currentTimeMillis() - SharedEvents.placedBlock.get(p).longValue() < 2000) {
				return;
			}
		}
		if (p.getGameMode().equals(GameMode.CREATIVE)
				|| p.getAllowFlight()
				|| e.getTo().getY() < e.getFrom().getY()
				|| p.getVehicle() != null
				|| p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE
				|| p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().getId() == 165
				|| PlayerUtil.isOnClimbable(p, 0)
				|| PlayerUtil.isOnClimbable(p, 1)
				|| p.hasPotionEffect(PotionEffectType.JUMP)
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()
				|| !getAntiCheat().isEnabled()
				|| PlayerUtil.isOnFence(p.getLocation())
				|| PlayerUtil.isOnPressure(p.getLocation())
				|| BlockUtil.isNearFence(p)
				|| PlayerUtil.isNearPressure(p)
				|| PlayerUtil.isNearChest(p)
				|| PlayerUtil.isNearBar(p)
				|| ServerUtil.isBukkitVerison("1_13")
				|| VelocityUtil.didTakeVelocity(p)
				|| PlayerUtil.isNearSlime(p)
				|| PlayerUtil.isNearPiston(p.getLocation())
				|| PlayerUtil.isNearSlime(e.getFrom())
				|| PlayerUtil.isNearSlime(e.getTo())) {
			return;
		}
		if (DataPlayer.lastNearSlime !=null) {
			if (DataPlayer.lastNearSlime.contains(p.getPlayer().getName().toString())) {
				return;
			}
		}
		if (!ServerUtil.isBukkitVerison("1_8")
				&&!ServerUtil.isBukkitVerison("1_7")) {
			if (p.hasPotionEffect(PotionEffectType.getByName("LEVITATION"))) {
				return;
			}
		}
		if (!ServerUtil.isBukkitVerison("1_13")&& !ServerUtil.isBukkitVerison("1_7")) {
			if (PlayerUtil.isNotSpider(p)) {
				return;
			}
		}
		if (ServerUtil.isBukkitVerison("1_13") || ServerUtil.isBukkitVerison("1_7")) {

			if (!PlayerUtil.isFlying(e,p)) {
				return;
			}
		}
		if (BlockUtil.isNearLiquid(p) && PlayerUtil.isNearHalfBlock(p)) {
			return;
		}

		long Time = System.currentTimeMillis();
		double TotalBlocks = 0.0D;
		if (SpiderA.AscensionTicks.containsKey(u)) {
			Time = AscensionTicks.get(u).getKey().longValue();
			TotalBlocks = AscensionTicks.get(u).getValue().doubleValue();
		}
		final long MS = System.currentTimeMillis() - Time;
		final double OffsetY = MathUtil.offset(MathUtil.getVerticalVector(e.getFrom().toVector()), MathUtil.getVerticalVector(e.getTo().toVector()));

		boolean ya = false;
		final List<Material> Types = new ArrayList<>();
		Types.add(p.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType());
		Types.add(p.getLocation().getBlock().getRelative(BlockFace.NORTH).getType());
		Types.add(p.getLocation().getBlock().getRelative(BlockFace.WEST).getType());
		Types.add(p.getLocation().getBlock().getRelative(BlockFace.EAST).getType());
		for (final Material Type : Types) {
			if ((Type.isSolid()) && (Type != Material.LADDER) && (Type != Material.VINE) && (Type != Material.AIR)) {
				ya = true;
				break;
			}
		}
		if (OffsetY > 0.0D) {
			TotalBlocks += OffsetY;
		} else if ((!ya) || (!CheatUtil.blocksNear(p))) {
			TotalBlocks = 0.0D;
		} else if (((e.getFrom().getY() > e.getTo().getY()) || (PlayerUtil.isInGround(p)))) {
			TotalBlocks = 0.0D;
		}
		double Limit = 0.5D;
		if (p.hasPotionEffect(PotionEffectType.JUMP)) {
			for (final PotionEffect effect : p.getActivePotionEffects()) {
				if (effect.getType().equals(PotionEffectType.JUMP)) {
					final int level = effect.getAmplifier() + 1;
					Limit += Math.pow(level + 4.2D, 2.0D) / 16.0D;
					break;
				}
			}
		}
		if ((ya) && (TotalBlocks > Limit)) {
			if (MS > 500L) {
				getAntiCheat().logCheat(this, p, Color.Red + "(WallClimb)", "(Type: A)");
				Time = System.currentTimeMillis();
			}
		} else {
			Time = System.currentTimeMillis();
		}
		SpiderA.AscensionTicks.put(u, new AbstractMap.SimpleEntry<>(Time, TotalBlocks));
	}
}
