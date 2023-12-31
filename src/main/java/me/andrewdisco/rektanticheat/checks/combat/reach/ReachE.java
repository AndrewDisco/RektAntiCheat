package me.andrewdisco.rektanticheat.checks.combat.reach;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Giant;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.packets.PacketPlayerType;
import me.andrewdisco.rektanticheat.packets.events.PacketAttackEvent;
import me.andrewdisco.rektanticheat.utils.PlayerUtil;

public class ReachE extends Check {


	public ReachE(AntiCheat AntiCheat) {
		super("ReachE", "Reach",  CheckType.Combat, true, false, false, false, true, 20, 1, 600000L, AntiCheat);
	}
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onAttack(PacketAttackEvent e) {
		if (e.getType() != PacketPlayerType.USE
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()) {
			return;
		}
		final Player p = e.getPlayer();
		if (p == null) {
			return;
		}
		final Entity d = e.getEntity();
		if (getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
			return;
		}
		final double YawDifference = Math.abs(180 - Math.abs(d.getLocation().getYaw() - p.getLocation().getYaw()));
		double Difference = PlayerUtil.getEyeLocation(p).distance(d.getLocation()) - 0.35;
		final int Ping = getAntiCheat().getLag().getPing(p);
		final double TPS = getAntiCheat().getLag().getTPS();
		double maxReach = 3.0 + d.getVelocity().length();
		if (p.getAllowFlight()) {
			maxReach += p.getFlySpeed();
		}

		if (p.isSprinting()) {
			maxReach += 0.2;
		}
		if (!(d instanceof Player)) {
			maxReach += 1.0;
		}
		if (d instanceof Player) {
			final Player player = (Player) d;
			if (player.getAllowFlight()) {
				maxReach += 2.0;
			}
		}
		if (PlayerUtil.isNearSlime(p.getLocation())
				|| PlayerUtil.isNearSlime(d.getLocation())) {
			maxReach += 1.0;
		}
		if (d instanceof Spider || d instanceof Giant) {
			maxReach += 1.0;
		}
		if (d instanceof Slime) {
			final Slime slime = (Slime) d;
			maxReach += slime.getSize()/4;
		}
		if (d instanceof MagmaCube) {
			final MagmaCube MagmaCube = (MagmaCube) d;
			maxReach += MagmaCube.getSize()/4;
		}
		if (p.getGameMode().equals(GameMode.CREATIVE)) {
			maxReach += 1.0;
		}
		if (p.getLocation().getY() > d.getLocation().getY()) {
			Difference = p.getLocation().getY() - p.getLocation().getY();
			maxReach += Difference / 2.5;
		} else if (p.getLocation().getY() > p.getLocation().getY()) {
			Difference = p.getLocation().getY() - p.getLocation().getY();
			maxReach += Difference / 2.5;
		}
		for (final PotionEffect effect : p.getActivePotionEffects()) {
			if (effect.getType().equals(PotionEffectType.SPEED)) {
				maxReach += 0.2D * (effect.getAmplifier() + 1);
			}
		}
		if (TPS <20) {
			final double TPSMultiplier = TPS / 20;
			final double tmp = maxReach / TPSMultiplier;
			maxReach = tmp;
		}
		final float velocity = (float)((Ping*0.0025) + Math.abs(d.getVelocity().length()) * 0.8);
		maxReach += velocity;
		maxReach += Ping < 250 ? Ping * 0.01262 : Ping * 0.0415;
		maxReach += YawDifference * 0.008;
		final double x = Math.abs(Math.abs(p.getLocation().getX()) - Math.abs(d.getLocation().getX()));
		final double y = Math.abs(Math.abs(p.getLocation().getY()) - Math.abs(d.getLocation().getY()));
		final double z = Math.abs(Math.abs(p.getLocation().getZ()) - Math.abs(d.getLocation().getZ()));
		final double distance = x+y+z;
		final double Reach1 = Difference - maxReach;
		double Reach = Reach1;
		if (p.getGameMode().equals(GameMode.CREATIVE)) {
			Reach += 4;
		}
		else {
			Reach += 3;
		}
		final String en = d.toString();
		if (maxReach < Difference) {
			final double ChanceVal = Math.round(Math.abs((Difference - maxReach) * 100));
			getAntiCheat().logCheat(this, p, "Attacked: " + en + "; Reach: " + Reach + "; Max Reach: " + maxReach + "; Distance: " + distance + "; Chance: " + ChanceVal + "%", "(Type: E)");
		}
	}
}