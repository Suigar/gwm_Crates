package ua.gwm.sponge_plugin.crates.event;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.text.Text;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.manager.Manager;

import java.util.Optional;

public class PlayerOpenedCrateEvent implements Event {

    private final Player player;
    private final Manager manager;
    private Text message;
    private NamedCause[] additional_causes = new NamedCause[0];

    public PlayerOpenedCrateEvent(Player player, Manager manager, Text message, NamedCause... additional_causes) {
        this.player = player;
        this.manager = manager;
        this.message = message;
        this.additional_causes = additional_causes;
    }

    @Override
    public Cause getCause() {
        Cause cause = GWMCrates.getInstance().getDefaultCause().with(
                NamedCause.of("player", player),
                NamedCause.of("manager", manager),
                NamedCause.of("message", message));
        if (additional_causes != null && additional_causes.length > 0) {
            for (NamedCause additional_cause : additional_causes) {
                cause = cause.with(additional_cause);
            }
        }
        return cause;
    }

    public void setMessage(Text message) {
        this.message = message;
    }

    public Text getMessage() {
        return message;
    }
}
