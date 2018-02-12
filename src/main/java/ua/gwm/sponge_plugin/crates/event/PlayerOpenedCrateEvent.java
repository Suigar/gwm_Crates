package ua.gwm.sponge_plugin.crates.event;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.manager.Manager;

public class PlayerOpenedCrateEvent extends AbstractEvent{

    private final Player player;
    private final Manager manager;
    private final Drop drop;

    public PlayerOpenedCrateEvent(Player player, Manager manager, Drop drop) {
        this.player = player;
        this.manager = manager;
        this.drop = drop;
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

    public Drop getDrop() {
        return drop;
    }
}
