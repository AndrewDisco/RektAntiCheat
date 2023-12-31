package me.andrewdisco.rektanticheat.checks.combat.reach;

import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Giant;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.packets.PacketPlayerType;
import me.andrewdisco.rektanticheat.packets.events.PacketAttackEvent;
import me.andrewdisco.rektanticheat.utils.MathUtil;

public class ReachA extends Check {

	public ReachA(AntiCheat AntiCheat) {
		super("ReachA", "Reach",  CheckType.Combat, true, true, false, true, false, 7, 1, 30000L, AntiCheat);
	}

	private int getKB(Player p){
		int enchantmentLevel = 0;
		final ItemStack[] inv = p.getInventory().getContents();
		for(final ItemStack item:inv){
			if (item != null) {
				if(item.getType() != null){
					if(item.getEnchantments().containsKey(Enchantment.KNOCKBACK)){
						return enchantmentLevel = item.getEnchantmentLevel(Enchantment.KNOCKBACK);
					}
				}
			}
		}
		return enchantmentLevel;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onAttack(PacketAttackEvent e) {
		final Entity p2 = e.getEntity();
		final Player p = e.getPlayer();
		if (p == null) {
			return;
		}
		if(e.getType() != PacketPlayerType.USE
				|| e.getEntity() == null
				|| p2 instanceof Enderman
				|| p2.isDead()
				|| p.getGameMode().equals(GameMode.CREATIVE)
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()){
			return;
		}

		final double distance = MathUtil.getHorizontalDistance(p.getLocation(), p2.getLocation()) - 0.35;
		double maxReach = 4.2;
		final double yawDifference = 180 - Math.abs(Math.abs(p.getEyeLocation().getYaw()) - Math.abs(p2.getLocation().getYaw()));
		final double KB = getKB(p);
		maxReach+= Math.abs(p.getVelocity().length() + p2.getVelocity().length()) * 0.4;
		maxReach+= yawDifference * 0.01;
		maxReach+= getAntiCheat().getLag().getPing(p) * 0.01097;

		if(maxReach < 4.2) {
			maxReach = 4.2;
		}
		if(KB > 0) {
			maxReach += KB;
		}
		if (p2 instanceof Slime) {
			final Slime slime = (Slime) p2;
			maxReach += slime.getSize()/4;
		}
		if (p2 instanceof MagmaCube) {
			final MagmaCube MagmaCube = (MagmaCube) p2;
			maxReach += MagmaCube.getSize()/4;
		}
		if (p2 instanceof Spider) {
			maxReach += 1.0;
		}
		if (p2 instanceof Giant) {
			maxReach += 2.0;
		}
		final String en = p2.toString();
		if(distance > maxReach) {
			getAntiCheat().logCheat(this, p, MathUtil.trim(3, distance) + " > " + MathUtil.trim(3, maxReach) + "; KB: " + KB + "; Attacked: " + en, "(Type: A)");
		}
	}
}