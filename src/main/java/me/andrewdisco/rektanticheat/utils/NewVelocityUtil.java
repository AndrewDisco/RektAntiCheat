package me.andrewdisco.rektanticheat.utils;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.data.DataPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

public class NewVelocityUtil implements Listener {
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final DataPlayer data = AntiCheat.getInstance().getDataManager().getData(p);
		if (data != null) {
			if (data.isLastVelUpdateBoolean()) {
				if (TimerUtil.elapsed(data.getLastVelUpdate(),Values.VelTimeReset_1_FORCE_RESET)) {
					data.setLastVelUpdateBoolean(false);
				}
				if (TimerUtil.elapsed(data.getLastVelUpdate(),Values.VelTimeReset_1)) {
					if (!p.isOnGround()) {
						data.setLastVelUpdate(TimerUtil.nowlong());
					} else {
						data.setLastVelUpdateBoolean(false);
					}
				}
			}
		}
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onVelChange(PlayerVelocityEvent e) {
		final Player p = e.getPlayer();
		final DataPlayer data = AntiCheat.getInstance().getDataManager().getData(p);
		if (data != null) {
			if (p.getNoDamageTicks() > 0 == false) {
				if (!data.isLastVelUpdateBoolean()) {
					data.setLastVelUpdateBoolean(true);
					data.setLastVelUpdate(TimerUtil.nowlong());
				}
			}
		}
	}
	public static boolean didTakeVel(Player p) {
		final DataPlayer data = AntiCheat.getInstance().getDataManager().getData(p);
		if (data != null) {
			return data.isLastVelUpdateBoolean();
		} else {
			return false;
		}
	}
}