package ua.gwm.sponge_plugin.crates.preview;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import ua.gwm.sponge_plugin.crates.util.SuperObject;
import ua.gwm.sponge_plugin.crates.manager.Manager;

import java.util.Optional;

public abstract class Preview extends SuperObject {

    public Preview(ConfigurationNode node) {
        super(node);
    }

    public Preview(String type, Optional<String> id) {
        super(type, id);
    }

    public abstract void preview(Player player, Manager manager);
}
