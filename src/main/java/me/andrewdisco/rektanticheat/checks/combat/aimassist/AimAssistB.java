package me.andrewdisco.rektanticheat.checks.combat.aimassist;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import me.andrewdisco.rektanticheat.checks.combat.autoclicker.AutoClickerA;
import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.checks.Check;
import me.andrewdisco.rektanticheat.checks.CheckType;
import me.andrewdisco.rektanticheat.data.DataPlayer;
import me.andrewdisco.rektanticheat.events.MoveEvent;
import me.andrewdisco.rektanticheat.utils.ExtraUtil;
import me.andrewdisco.rektanticheat.utils.MathUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;

public class AimAssistB extends Check {
	private double flag = 2;

	public AimAssistB(me.andrewdisco.rektanticheat.AntiCheat AntiCheat) {
		super("AimAssistB", "AimAssist",  CheckType.Combat, true, false, false, false, false, 20, 2, 60000L, AntiCheat);
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(me.andrewdisco.rektanticheat.AntiCheat.getInstance(), PacketType.Play.Client.USE_ENTITY) {
			@Override
			public void onPacketReceiving(PacketEvent e) {
				final Player p = e.getPlayer();
				if (p == null) {
					return;
				}
				final Optional<Entity> entityOp = p.getWorld().getEntities().stream().filter(entity -> entity.getEntityId() == e.getPacket().getIntegers().read(0)).findFirst();
				if(entityOp.isPresent()) {
					final Entity entity = entityOp.get();
					final EnumWrappers.EntityUseAction action = e.getPacket().getEntityUseActions().read(0);
					if(action.equals(EnumWrappers.EntityUseAction.ATTACK) && entity instanceof LivingEntity) {
						if (entity instanceof Player) {
							if (MoveEvent.lastMove.containsKey(entity.getUniqueId())) {
								if (System.currentTimeMillis() - MoveEvent.lastMove.get(entity.getUniqueId()) >= 50) {
									flag = 1;
								}
								else {
									flag = 2;
								}
							}
							else {
								flag = 1;
							}
						} else {
							flag = 1;
						}
						final DataPlayer data = AntiCheat.getDataManager().getDataPlayer(p);
						if(data != null) {
							data.lastAttack = System.currentTimeMillis();
							data.lastHitEntity = (LivingEntity) entity;
						}
					}
				}
			}
		});
	}
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final DataPlayer data = AntiCheat.getInstance().getDataManager().getDataPlayer(p);
		if(data == null
				|| data.lastHitEntity == null
				|| (System.currentTimeMillis() - data.lastAttack) > 350L
				|| getAntiCheat().getLag().getTPS() < getAntiCheat().getTPSCancel()
				|| getAntiCheat().getLag().getPing(p) > getAntiCheat().getPingCancel()) {
			return;
		}
		final float offset = ExtraUtil.yawTo180F((float) MathUtil.getOffsetFromEntity(p, data.lastHitEntity)[0]);
		if(data.patterns.size() >= 10) {
			Collections.sort(data.patterns);
			final float range = Math.abs(data.patterns.get(data.patterns.size() - 1) -  data.patterns.get(0));
			final float check = Math.abs(range - data.lastRange);
			if(check < flag && check > 0.6) {
				if (AutoClickerA.Clicks.containsKey(p.getUniqueId())) {
					final List<Long> x = AutoClickerA.Clicks.get(p.getUniqueId());
					final long w = Collections.min(x);
					if (w < 142) {
						getAntiCheat().logCheat(this, p, "(Aimbot) Range: " + range + "; Last Range: " + data.lastRange + "; Flag: " + flag + "; Last Hit: " + data.lastHitEntity + "; Check: " + check, "(Type: B)");
					}
				}
				else {
					return;
				}
			}
			data.lastRange = range;
			data.patterns.clear();
		} else {
			data.patterns.add(offset);
		}
	}
}