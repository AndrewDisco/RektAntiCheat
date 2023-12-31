package me.andrewdisco.rektanticheat.checks.other;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;

public class InvMoveB extends Check {
	public InvMoveB(AntiCheat AntiCheat) {
		super("InvMoveB", "InvMove", CheckType.Other, true, false, false, false, true, 15, 1, 600000L, AntiCheat);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void attack(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)) {
			return;
		}
		final Player p = (Player) e.getDamager();
		final InventoryView view = p.getOpenInventory();
		final Inventory top = view.getTopInventory();
		if (view !=null) {
			if (top.toString().contains("CraftInventoryCrafting")
					|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
					|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()
					|| p.getAllowFlight()
					|| p.getGameMode().equals(GameMode.CREATIVE)) {
				return;
			}
			getAntiCheat().logCheat(this, p, "Attacking while having a gui open!", "(Type: B)");
		}
	}
}