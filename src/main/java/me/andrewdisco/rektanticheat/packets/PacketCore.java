package me.andrewdisco.rektanticheat.packets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import me.andrewdisco.rektanticheat.AntiCheat;
import me.andrewdisco.rektanticheat.data.DataPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import me.andrewdisco.rektanticheat.packets.events.PacketAttackEvent;
import me.andrewdisco.rektanticheat.packets.events.PacketBlockPlacementEvent;
import me.andrewdisco.rektanticheat.packets.events.PacketEntityActionEvent;
import me.andrewdisco.rektanticheat.packets.events.PacketHeldItemChangeEvent;
import me.andrewdisco.rektanticheat.packets.events.PacketKeepAliveEvent;
import me.andrewdisco.rektanticheat.packets.events.PacketKillauraEvent;
import me.andrewdisco.rektanticheat.packets.events.PacketPlayerEvent;
import me.andrewdisco.rektanticheat.packets.events.PacketSwingArmEvent;
import me.andrewdisco.rektanticheat.packets.events.PacketUseEntityEvent;
import me.andrewdisco.rektanticheat.utils.ServerUtil;

public class PacketCore {
	private static me.andrewdisco.rektanticheat.AntiCheat AntiCheat;
	private HashSet<EntityType> enabled;
	public static Map<UUID, Integer> movePackets;
	private static final PacketType[] ENTITY_PACKETS = new PacketType[] { PacketType.Play.Server.SPAWN_ENTITY_LIVING,
			PacketType.Play.Server.NAMED_ENTITY_SPAWN, PacketType.Play.Server.ENTITY_METADATA };

	public PacketCore(AntiCheat AntiCheat) {
		super();
		PacketCore.AntiCheat = AntiCheat;
		enabled = new HashSet<>();
		enabled.add(EntityType.valueOf("PLAYER"));
		movePackets = new HashMap<>();

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(PacketCore.AntiCheat,
				new PacketType[] { PacketType.Play.Client.USE_ENTITY }) {
			@Override
			public void onPacketReceiving(final PacketEvent event) {
				final PacketContainer packet = event.getPacket();
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				try {
					final Object playEntity = getNMSClass("PacketPlayInUseEntity");
					final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
					if (version.contains("1_7")) {
						if (packet.getHandle() == playEntity) {
							if (playEntity.getClass().getMethod("c") == null) {
								return;
							}
						}
					} else {
						if (packet.getHandle() == playEntity) {
							if (playEntity.getClass().getMethod("a") == null) {
								return;
							}
						}
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
				EnumWrappers.EntityUseAction type;
				try {
					type = packet.getEntityUseActions().read(0);
				} catch (final Exception ex) {
					return;
				}

				final Player entity = event.getPlayer();

				if(entity == null) {
					return;
				}

				Bukkit.getServer().getPluginManager().callEvent(new PacketUseEntityEvent(type, player, entity));
				if (type == EntityUseAction.ATTACK) {
					Bukkit.getServer().getPluginManager()
					.callEvent(new PacketKillauraEvent(player, PacketPlayerType.USE));
				}
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AntiCheat, ENTITY_PACKETS) {

			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				final Entity e = packet.getEntityModifier(event).read(0);
				if (e instanceof LivingEntity && enabled.contains(e.getType())
						&& packet.getWatchableCollectionModifier().read(0) != null
						&& e.getUniqueId() != event.getPlayer().getUniqueId()) {
					packet = packet.deepClone();
					event.setPacket(packet);
					if (event.getPacket().getType() == PacketType.Play.Server.ENTITY_METADATA) {
						final WrappedDataWatcher watcher = new WrappedDataWatcher(
								packet.getWatchableCollectionModifier().read(0));
						if (ServerUtil.isBukkitVerison("1_7")
								|| ServerUtil.isBukkitVerison("1_8")
								|| ServerUtil.isBukkitVerison("1_9")) {
							this.processDataWatcher1(watcher);
						}
						else {
							this.processDataWatcher2(watcher);
						}
						packet.getWatchableCollectionModifier().write(0,
								watcher.getWatchableObjects());
					}
				}
			}

			private void processDataWatcher1(WrappedDataWatcher watcher) {
				if (watcher != null && watcher.getObject(6) != null && watcher.getFloat(6) != 0.0F) {
					watcher.setObject(6, 1.0f);
				}
			}
			private void processDataWatcher2(WrappedDataWatcher watcher) {
				if (watcher != null && watcher.getObject(6) != null && watcher.getByte(6) != 0.0F) {
					watcher.setObject(6, 1.0f);
				}
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(PacketCore.AntiCheat,
				new PacketType[] { PacketType.Play.Client.POSITION_LOOK }) {
			@Override
			public void onPacketReceiving(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager().callEvent(new PacketPlayerEvent(player,
						event.getPacket().getDoubles().read(0),
						event.getPacket().getDoubles().read(1),
						event.getPacket().getDoubles().read(2), event.getPacket().getFloat().read(0),
						event.getPacket().getFloat().read(1), PacketPlayerType.POSLOOK));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(PacketCore.AntiCheat, new PacketType[] { PacketType.Play.Client.LOOK }) {
					@Override
					public void onPacketReceiving(final PacketEvent event) {
						final Player player = event.getPlayer();

						if (player == null) {
							return;
						}

						Bukkit.getServer().getPluginManager()
						.callEvent(new PacketPlayerEvent(player, event.getPacket().getDoubles().read(0),
								event.getPacket().getDoubles().read(1), event.getPacket().getDoubles().read(2),
								event.getPacket().getFloat().read(0), event.getPacket().getFloat().read(1),
								PacketPlayerType.POSLOOK));
					}
				});
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(PacketCore.AntiCheat,
				new PacketType[] { PacketType.Play.Client.POSITION }) {
			@Override
			public void onPacketReceiving(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager().callEvent(
						new PacketPlayerEvent(player, event.getPacket().getDoubles().read(0),
								event.getPacket().getDoubles().read(1),
								event.getPacket().getDoubles().read(2), player.getLocation().getYaw(),
								player.getLocation().getPitch(), PacketPlayerType.POSITION));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(PacketCore.AntiCheat,
				new PacketType[] { PacketType.Play.Server.POSITION}) {
			@Override
			public void onPacketSending(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}

				int i = movePackets.getOrDefault(player.getUniqueId(), 0);
				i++;
				movePackets.put(player.getUniqueId(), i);
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(PacketCore.AntiCheat,
				new PacketType[] { PacketType.Play.Client.ENTITY_ACTION }) {
			@Override
			public void onPacketReceiving(final PacketEvent event) {
				final PacketContainer packet = event.getPacket();
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager()
				.callEvent(new PacketEntityActionEvent(player, packet.getIntegers().read(1)));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(PacketCore.AntiCheat,
				new PacketType[] { PacketType.Play.Client.KEEP_ALIVE }) {
			@Override
			public void onPacketReceiving(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager().callEvent(new PacketKeepAliveEvent(player));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(PacketCore.AntiCheat,
				new PacketType[] { PacketType.Play.Client.ARM_ANIMATION }) {
			@Override
			public void onPacketReceiving(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager()
				.callEvent(new PacketKillauraEvent(player, PacketPlayerType.ARM_SWING));
				Bukkit.getServer().getPluginManager().callEvent(new PacketSwingArmEvent(event, player));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(PacketCore.AntiCheat,
				new PacketType[] { PacketType.Play.Client.HELD_ITEM_SLOT }) {
			@Override
			public void onPacketReceiving(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager().callEvent(new PacketHeldItemChangeEvent(event, player));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(PacketCore.AntiCheat,
				new PacketType[] { PacketType.Play.Client.BLOCK_PLACE }) {
			@Override
			public void onPacketReceiving(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager().callEvent(new PacketBlockPlacementEvent(event, player));
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(PacketCore.AntiCheat, new PacketType[] { PacketType.Play.Client.FLYING }) {
					@Override
					public void onPacketReceiving(final PacketEvent event) {
						final Player player = event.getPlayer();
						if (player == null) {
							return;
						}
						Bukkit.getServer().getPluginManager()
						.callEvent(new PacketPlayerEvent(player, player.getLocation().getX(),
								player.getLocation().getY(), player.getLocation().getZ(),
								player.getLocation().getYaw(), player.getLocation().getPitch(),
								PacketPlayerType.FLYING));
					}
				});
	}

	public Class<?> getNMSClass(String name) {
		final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		}

		catch (final ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static void init() {
		movePackets = new HashMap<>();

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(me.andrewdisco.rektanticheat.AntiCheat.getInstance(), PacketType.Play.Server.POSITION) {
			@Override
			public void onPacketSending(final PacketEvent event) {
				final Player player = event.getPlayer();
				if (player == null) {
					return;
				}

				movePackets.put(player.getUniqueId(), movePackets.getOrDefault(player.getUniqueId(), 0) + 1);
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(me.andrewdisco.rektanticheat.AntiCheat.getInstance(), PacketType.Play.Client.USE_ENTITY) {
					@Override
					public void onPacketReceiving(PacketEvent event) {
						final PacketContainer packet = event.getPacket();
						final Player player = event.getPlayer();
						if (player == null) {
							return;
						}

						EnumWrappers.EntityUseAction type;
						try {
							type = packet.getEntityUseActions().read(0);
						} catch (final Exception ex) {
							return;
						}

						final Entity entity = event.getPacket().getEntityModifier(player.getWorld()).read(0);

						if (entity == null) {
							return;
						}

						if (type == EnumWrappers.EntityUseAction.ATTACK) {
							Bukkit.getServer().getPluginManager().callEvent(new PacketAttackEvent(player, entity, PacketPlayerType.USE));
						}
					}
				});
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(me.andrewdisco.rektanticheat.AntiCheat.getInstance(), PacketType.Play.Client.LOOK) {
			@Override
			public void onPacketReceiving(PacketEvent packetEvent) {
				final Player player = packetEvent.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager().callEvent(new PacketPlayerEvent(player, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), packetEvent.getPacket().getFloat().read(0), packetEvent.getPacket().getFloat().read(1), PacketPlayerType.LOOK));

				final DataPlayer data = me.andrewdisco.rektanticheat.AntiCheat.getInstance().getDataManager().getData(player);

				if(data != null) {
					data.setLastPacket(System.currentTimeMillis());
				}
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(me.andrewdisco.rektanticheat.AntiCheat.getInstance(), PacketType.Play.Client.POSITION) {
			@Override
			public void onPacketReceiving(PacketEvent packetEvent) {
				final Player player = packetEvent.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager().callEvent(new PacketPlayerEvent(player, packetEvent.getPacket().getDoubles().read(0), packetEvent.getPacket().getDoubles().read(1), packetEvent.getPacket().getDoubles().read(2), player.getLocation().getYaw(), player.getLocation().getPitch(), PacketPlayerType.POSITION));

				final DataPlayer data = me.andrewdisco.rektanticheat.AntiCheat.getInstance().getDataManager().getData(player);

				if(data != null) {
					data.setLastPacket(System.currentTimeMillis());
				}
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(me.andrewdisco.rektanticheat.AntiCheat.getInstance(), PacketType.Play.Client.POSITION_LOOK) {
			@Override
			public void onPacketReceiving(PacketEvent packetEvent) {
				final Player player = packetEvent.getPlayer();
				if (player == null) {
					return;
				}

				Bukkit.getServer().getPluginManager().callEvent(new PacketPlayerEvent(player, packetEvent.getPacket().getDoubles().read(0), packetEvent.getPacket().getDoubles().read(1), packetEvent.getPacket().getDoubles().read(2), packetEvent.getPacket().getFloat().read(0), packetEvent.getPacket().getFloat().read(1), PacketPlayerType.POSLOOK));

				final DataPlayer data = me.andrewdisco.rektanticheat.AntiCheat.getInstance().getDataManager().getData(player);

				if(data != null) {
					data.setLastKillauraPitch(packetEvent.getPacket().getFloat().read(1));
					data.setLastKillauraYaw(packetEvent.getPacket().getFloat().read(0));
					data.setLastPacket(System.currentTimeMillis());
				}
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(me.andrewdisco.rektanticheat.AntiCheat.getInstance(), PacketType.Play.Client.FLYING) {
			@Override
			public void onPacketReceiving(PacketEvent packetEvent) {
				final Player player = packetEvent.getPlayer();
				if (player == null) {
					return;
				}
				Bukkit.getServer().getPluginManager().callEvent(new PacketPlayerEvent(player, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch(), PacketPlayerType.FLYING));

				final DataPlayer data = me.andrewdisco.rektanticheat.AntiCheat.getInstance().getDataManager().getData(player);

				if(data != null) {
					data.setLastPacket(System.currentTimeMillis());
				}
			}
		});
	}
}