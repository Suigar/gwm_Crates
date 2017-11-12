package ua.gwm.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.util.SuperObjectType;
import ua.gwm.sponge_plugin.crates.util.Utils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class DelayDrop extends Drop {

    private Drop child_drop;
    private long delay;

    public DelayDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode child_drop_node = node.getNode("CHILD_DROP");
            ConfigurationNode delay_node = node.getNode("DELAY");
            if (child_drop_node.isVirtual()) {
                throw new RuntimeException("CHILD_DROP node does not exist!");
            }
            child_drop = (Drop) Utils.createSuperObject(child_drop_node, SuperObjectType.DROP);
            if (delay_node.isVirtual()) {
                throw new RuntimeException("DELAY node does not exist!");
            }
            delay = delay_node.getLong();
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Delay Drop!", e);
        }
    }

    public DelayDrop(String type, Optional<String> id, Optional<BigDecimal> price,
                     Optional<ItemStack> drop_item, int level, Drop child_drop, int delay) {
        super(type, id, price, drop_item, level);
        this.child_drop = child_drop;
        this.delay = delay;
    }

    @Override
    public void apply(Player player) {
        Sponge.getScheduler().createTaskBuilder().delay(delay, TimeUnit.MILLISECONDS).
                execute(() -> child_drop.apply(player)).
                submit(GWMCrates.getInstance());
    }
}
