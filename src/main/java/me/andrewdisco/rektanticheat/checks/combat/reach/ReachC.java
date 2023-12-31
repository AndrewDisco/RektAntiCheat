package me.andrewdisco.rektanticheat.checks.combat.reach;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.utils.PlayerUtil;

public class ReachC extends Check {

	public static HashMap<UUID, Integer> toBan;

	public ReachC(AntiCheat AntiCheat) {
		super("ReachC", "Reach",  CheckType.Combat, true, false, false, false, true, 20, 1, 600000L, AntiCheat);
		toBan = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onAttack(EntityDamageByEntityEvent e) {
		if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
				|| !(e.getDamager() instanceof Player)
				|| !(e.getEntity() instanceof Player)
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()) {
			return;
		}

		final Player p = (Player) e.getDamager();
		final Player d = (Player) e.getEntity();
		if (p == null) {
			return;
		}
		if (d.getAllowFlight() && d.isFlying()) {
			return;
		}
		if (p.getAllowFlight() && p.isFlying()) {
			return;
		}

		if (p.getGameMode().equals(GameMode.CREATIVE)
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()
				|| getAntiCheat().getLag().getPing(d) > getAntiCheat().getPingCancel()) {
			return;
		}
		final double YawDifference = Math.abs(180 - Math.abs(d.getLocation().getYaw() - p.getLocation().getYaw()));
		double Difference = PlayerUtil.getEyeLocation(p).distance(d.getEyeLocation()) - 0.35;

		final int Ping = getAntiCheat().getLag().getPing(p);
		final double TPS = getAntiCheat().getLag().getTPS();
		double MaxReach = 4.0 + d.getVelocity().length();

		if (p.isSprinting()) {
			MaxReach += 0.2;
		}

		if (p.getLocation().getY() > d.getLocation().getY()) {
			Difference = p.getLocation().getY() - p.getLocation().getY();
			MaxReach += Difference / 2.5;
		} else if (p.getLocation().getY() > p.getLocation().getY()) {
			Difference = p.getLocation().getY() - p.getLocation().getY();
			MaxReach += Difference / 2.5;
		}
		for (final PotionEffect effect : p.getActivePotionEffects()) {
			if (effect.getType().equals(PotionEffectType.SPEED)) {
				MaxReach += 0.2D * (effect.getAmplifier() + 1);
			}
		}

		final double velocity = p.getVelocity().length() + d.getVelocity().length();

		MaxReach += velocity * 1.5;
		MaxReach += Ping < 250 ? Ping * 0.00212 : Ping * 0.031;
		MaxReach += YawDifference * 0.008;

		double ChanceVal = Math.round(Math.abs((Difference - MaxReach) * 100));

		if (ChanceVal > 100) {
			ChanceVal = 100;
		}

		if (MaxReach < Difference) {
			this.dumplog(p, "Logged for Reach Type C; Reach: " + Difference
					+ "; MaxReach; " + MaxReach + "; Chance: " + ChanceVal + "%" + "; Ping: " + Ping + "; TPS: " + TPS);

			getAntiCheat().logCheat(this, p, "Reach: " + Difference
					+ "; MaxReach; " + MaxReach + "; Chance: " + ChanceVal + "%" + "; Ping: " + Ping + "; TPS: " + TPS, "(Type: C)");
		}
	}
}