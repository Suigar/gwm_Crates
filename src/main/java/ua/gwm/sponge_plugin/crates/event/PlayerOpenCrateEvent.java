package ua.gwm.sponge_plugin.crates.event;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.manager.Manager;

public class PlayerOpenCrateEvent extends AbstractEvent implements Cancellable {

    private final Player player;
    private final Manager manager;
    private boolean cancelled = false;

    public PlayerOpenCrateEvent(Player player, Manager manager) {
        this.player = player;
        this.manager = manager;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Cause getCause() {
        return GWMCrates.getInstance().getCause();
    }

    public Player getPlayer() {
        return player;
    }

    public Manager getManager() {
        return manager;
    }
}
