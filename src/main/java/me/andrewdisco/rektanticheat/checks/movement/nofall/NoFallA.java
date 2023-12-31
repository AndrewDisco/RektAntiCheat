package me.andrewdisco.rektanticheat.checks.movement.nofall;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.data.DataPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import me.andrewdisco.rektanticheat.utils.BlockUtil;
import me.andrewdisco.rektanticheat.utils.PlayerUtil;
import me.andrewdisco.rektanticheat.utils.TimeUtil;

public class NoFallA extends Check {
	public static Map<UUID, Map.Entry<Long, Integer>> NoFallTicks;
	public static Map<UUID, Double> FallDistance;
	public static ArrayList<Player> cancel;

	public NoFallA(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("NoFallA", "NoFall", CheckType.Movement, true, true, false, true, false, 9, 1, 120000L, AntiCheat);
		NoFallTicks = new HashMap<>();
		FallDistance = new HashMap<>();
		cancel = new ArrayList<>();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		if (p.getAllowFlight()
				|| p.getGameMode().equals(GameMode.CREATIVE)
				|| p.getVehicle() != null
				|| p.getAllowFlight()
				|| cancel.remove(p)
				|| DataPlayer.getWasFlying() > 0
				|| BlockUtil.isNearLiquid(p)
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()
				|| PlayerUtil.isOnClimbable(p, 0)
				|| PlayerUtil.isInWater(p)) {
			return;
		}
		final Damageable dp = e.getPlayer();

		if (dp.getHealth() <= 0.0D) {
			return;
		}

		double Falling = 0.0D;
		if ((!PlayerUtil.isInGround(p)) && (e.getFrom().getY() > e.getTo().getY())) {
			if (FallDistance.containsKey(p.getUniqueId())) {
				Falling = FallDistance.get(p.getUniqueId()).doubleValue();
			}
			Falling += e.getFrom().getY() - e.getTo().getY();
		}
		FallDistance.put(p.getUniqueId(), Double.valueOf(Falling));
		if (Falling < 3.0D) {
			return;
		}
		long Time = System.currentTimeMillis();
		int Count = 0;
		if (NoFallTicks.containsKey(p.getUniqueId())) {
			Time = NoFallTicks.get(p.getUniqueId()).getKey().longValue();
			Count = NoFallTicks.get(p.getUniqueId()).getValue().intValue();
		}
		if ((p.isOnGround()) || (p.getFallDistance() == 0.0F)) {
			dumplog(p, "Logged for NoFall Type A; . Real Fall Distance: " + Falling);
			p.damage(5);
			Count += 2;
		} else {
			Count--;
		}
		if (NoFallTicks.containsKey(p.getUniqueId()) && TimeUtil.elapsed(Time, 10000L)) {
			Count = 0;
			Time = System.currentTimeMillis();
		}
		if (Count >= 4) {
			dumplog(p, "Logged for NoFall Type A;  Count: " + Count);
			Count = 0;

			FallDistance.put(p.getUniqueId(), Double.valueOf(0.0D));
			getAntiCheat().logCheat(this, p, "(Packet)", "(Type: A)");
		}
		NoFallTicks.put(p.getUniqueId(),
				new AbstractMap.SimpleEntry<>(Time, Count));
		return;
	}
}