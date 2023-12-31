package me.andrewdisco.rektanticheat.update;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdateEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final UpdateType Type;

	public UpdateEvent(UpdateType Type) {
		this.Type = Type;
	}

	public UpdateType getType() {
		return this.Type;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}