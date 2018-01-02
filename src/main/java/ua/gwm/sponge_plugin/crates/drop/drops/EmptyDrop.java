package ua.gwm.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import ua.gwm.sponge_plugin.crates.drop.Drop;

import java.util.Optional;

public class EmptyDrop extends Drop {

    public EmptyDrop(ConfigurationNode node) {
        super(node);
    }

    public EmptyDrop(Optional<String> id, Optional<ItemStack> drop_item, int level) {
        super("EMPTY", id, Optional.empty(), drop_item, level);
    }

    @Override
    public void apply(Player player) {
    }
}
