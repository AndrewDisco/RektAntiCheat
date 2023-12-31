package me.andrewdisco.rektanticheat.checks.other;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.utils.PlayerUtil;

public class ScaffoldA extends Check {
	public static Map<UUID, Map.Entry<Integer, Long>> speedTicks;
	public ScaffoldA(AntiCheat AntiCheat) {
		super("ScaffoldA", "Scaffold", CheckType.Other, true, false, false, false, true, 20, 1, 600000L, AntiCheat);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onInteract(BlockPlaceEvent e) {
		final Player p = e.getPlayer();
		final double x = p.getEyeLocation().getX() - e.getBlockPlaced().getX();
		final double y = p.getEyeLocation().getY() - e.getBlockPlaced().getY();
		final double z = p.getEyeLocation().getZ() - e.getBlockPlaced().getZ();
		if (getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()
				|| p.getAllowFlight()
				|| p.getGameMode().equals(GameMode.CREATIVE)
				|| y < 2.6
				|| !p.isSprinting()
				|| PlayerUtil.isFlying2(e,p)
				|| !PlayerUtil.isFlying(e,p)
				|| PlayerUtil.isOnGround(e, p)) {
			return;
		}
		if (x > 0.8) {
			getAntiCheat().logCheat(this, p, "[1]", "(Type: A)");
		}
		if (z > 0.8) {
			getAntiCheat().logCheat(this, p, "[2]", "(Type: A)");
		}
	}
}