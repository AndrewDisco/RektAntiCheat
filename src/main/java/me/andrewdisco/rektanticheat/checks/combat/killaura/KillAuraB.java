package me.andrewdisco.rektanticheat.checks.combat.killaura;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.data.DataPlayer;
import me.andrewdisco.rektanticheat.packets.PacketPlayerType;
import me.andrewdisco.rektanticheat.packets.events.PacketAttackEvent;
import me.andrewdisco.rektanticheat.utils.MathUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class KillAuraB extends Check {

	public KillAuraB(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("KillAuraB", "KillAura",  CheckType.Combat, true, true, false, true, false, 10, 1, 600000L, AntiCheat);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onAttack(PacketAttackEvent e) {
		final Player p = e.getPlayer();
		if (p == null) {
			return;
		}
		final DataPlayer data = AntiCheat.getInstance().getDataManager().getData(p);
		if(e.getType() != PacketPlayerType.USE
				|| (data == null)) {
			return;
		}
		int verboseA = data.getKillauraAVerbose();
		long time = data.getLastAimTime();
		if(MathUtil.elapsed(time, 1100L)) {
			time = System.currentTimeMillis();
			verboseA = 0;
		}
		if ((Math.abs(data.getLastKillauraPitch() - e.getPlayer().getEyeLocation().getPitch()) > 1
				|| angleDistance((float) data.getLastKillauraYaw(), p.getEyeLocation().getYaw()) > 1
				|| Double.compare(p.getEyeLocation().getYaw(), data.getLastKillauraYaw()) != 0)
				&& !MathUtil.elapsed(data.getLastPacket(), 100L)) {
			if(angleDistance((float) data.getLastKillauraYaw(), p.getEyeLocation().getYaw()) != data.getLastKillauraYawDif()) {
				if(++verboseA > 9) {
					if (getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
							|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
						return;
					}
					getAntiCheat().logCheat(this, p, "Verbose: " + verboseA, "(Type: B)");
				}
			}
			data.setLastKillauraYawDif(angleDistance((float) data.getLastKillauraYaw(), p.getEyeLocation().getYaw()));
		} else {
			verboseA = 0;
		}
		data.setKillauraAVerbose(verboseA);
		data.setLastAimTime(time);
	}

	private static float angleDistance(float alpha, float beta) {
		final float phi = Math.abs(beta - alpha) % 360;
		return phi > 180 ? 360 - phi : phi;
	}
}