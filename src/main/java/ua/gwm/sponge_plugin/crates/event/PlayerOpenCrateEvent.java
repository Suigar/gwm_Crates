package ua.gwm.sponge_plugin.crates.event;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.util.OptBool;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.manager.Manager;

import java.util.Optional;

public class PlayerOpenCrateEvent implements Event, Cancellable {

    private final Player player;
    private final Manager manager;
    private boolean cancelled = false;
    private NamedCause[] additional_causes = new NamedCause[0];

    public PlayerOpenCrateEvent(Player player, Manager manager, NamedCause... additional_causes) {
        this.player = player;
        this.manager = manager;
        this.additional_causes = additional_causes;
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
        Cause cause = GWMCrates.getInstance().getDefaultCause().with(
                NamedCause.of("player", player),
                NamedCause.of("manager", manager));
        if (additional_causes != null && additional_causes.length > 0) {
            for (NamedCause additional_cause : additional_causes) {
                cause = cause.with(additional_cause);
            }
        }
        return cause;
    }
}
