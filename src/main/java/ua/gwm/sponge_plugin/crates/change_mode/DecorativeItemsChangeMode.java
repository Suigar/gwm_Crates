package ua.gwm.sponge_plugin.crates.change_mode;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStack;
import ua.gwm.sponge_plugin.crates.util.SuperObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class DecorativeItemsChangeMode extends SuperObject {

    private int change_delay = 10;
    private List<Integer> ignored_indices;

    public DecorativeItemsChangeMode(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode change_delay_node = node.getNode("CHANGE_DELAY");
            ConfigurationNode ignored_indices_node = node.getNode("IGNORED_INDICES");
            change_delay = change_delay_node.getInt(10);
            ignored_indices = ignored_indices_node.getList(TypeToken.of(Integer.class), new ArrayList<Integer>());
        } catch (Exception e) {
            throw new RuntimeException("Exception creating First Gui Decorative Items Change Mode!", e);
        }
    }

    public DecorativeItemsChangeMode(String type, Optional<String> id, int change_delay, List<Integer> ignored_indices) {
        super(type, id);
        this.change_delay = change_delay;
        this.ignored_indices = ignored_indices;
    }

    public abstract List<ItemStack> shuffle(List<ItemStack> decorative_items);

    public int getChangeDelay() {
        return change_delay;
    }

    public List<Integer> getIgnoredIndices() {
        return ignored_indices;
    }
}