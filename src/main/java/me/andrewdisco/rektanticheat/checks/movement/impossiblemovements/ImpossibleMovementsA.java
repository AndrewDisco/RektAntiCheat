package me.andrewdisco.rektanticheat.checks.movement.impossiblemovements;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.data.DataPlayer;
import me.andrewdisco.rektanticheat.utils.TimerUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class ImpossibleMovementsA extends Check {
	public ImpossibleMovementsA(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("ImpossibleMovementsA", "ImpMove", CheckType.Movement, true, false, false, false, true, 20, 1, 600000L, AntiCheat);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final Location from  =e.getFrom();
		final Location to = e.getTo();
		if (getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()
				|| p.getGameMode().equals(GameMode.CREATIVE)
				|| DataPlayer.getWasFlying() > 0
				|| p.getAllowFlight()) {
			return;
		}
		final DataPlayer data = AntiCheat.getInstance().getDataManager().getData(p);
		if (data != null) {
			if (p.getLocation().add(0,-0.30,0).getBlock().getType() == Material.CACTUS && p.getLocation().getBlock().getType() == Material.AIR) {
				if (data.getAntiCactus_VL() >= 3) {
					getAntiCheat().logCheat(this, p, "Impossible Movements: (Anti Cactus)", "(Type: A)");
				} else {
					data.setAntiCactus_VL(data.getAntiCactus_VL()+1);
				}
			} else {
				data.setAntiCactus_VL(0);
			}
			if (!data.isWebFloatMS_Set() && p.getLocation().add(0,-0.50,0).getBlock().getType() == Material.WEB) {
				data.setWebFloatMS_Set(true);
				data.setWebFloatMS(TimerUtil.nowlong());
			} else if (data.isWebFloatMS_Set()) {
				if (e.getTo().getY() == e.getFrom().getY()) {
					final double x = Math.floor(from.getX());
					final double z = Math.floor(from.getZ());
					if(Math.floor(to.getX())!=x||Math.floor(to.getZ())!=z) {
						if (data.getWebFloat_BlockCount() > 0) {
							if (p.getLocation().add(0,-0.50,0).getBlock().getType() != Material.WEB) {
								data.setWebFloatMS_Set(false);
								data.setWebFloat_BlockCount(0);
							}
							getAntiCheat().logCheat(this, p, "Impossible Movements: (Web Float)", "(Type: A)");
						} else {
							data.setWebFloat_BlockCount(data.getWebFloat_BlockCount()+1);
						}
					}
				} else {
					data.setWebFloatMS_Set(false);
					data.setWebFloat_BlockCount(0);
				}
			}
		}
	}
}