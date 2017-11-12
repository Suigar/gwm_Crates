package ua.gwm.sponge_plugin.crates.change_mode.change_modes;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStack;
import ua.gwm.sponge_plugin.crates.change_mode.DecorativeItemsChangeMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderedChangeMode extends DecorativeItemsChangeMode {

    private boolean right;

    public OrderedChangeMode(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode right_node = node.getNode("RIGHT");
            right = right_node.getBoolean(false);
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Ordered First Gui Decorative Items Change Mode!", e);
        }
    }

    public OrderedChangeMode(int change_delay, Optional<String> id, List<Integer> ignored_indices, boolean right) {
        super("ORDERED", id, change_delay, ignored_indices);
        this.right = right;
    }

    @Override
    public List<ItemStack> shuffle(List<ItemStack> decorative_items) {
        List<Integer> ignored_indices = getIgnoredIndices();
        List<Integer> indices_to_swap = new ArrayList<Integer>();
        for (int i = 0; i < decorative_items.size(); i++) {
            if (!ignored_indices.contains(i)) {
                indices_to_swap.add(i);
            }
        }
        if (right) {
            ItemStack previous = decorative_items.get(0);
            ItemStack temp;
            decorative_items.set(0, decorative_items.get(indices_to_swap.get(indices_to_swap.size() - 1)));
            for (int i = 1 ; i < indices_to_swap.size(); i++) {
                temp = previous;
                previous = decorative_items.get(indices_to_swap.get(i));
                decorative_items.set(indices_to_swap.get(i), temp);
            }
        } else {
            ItemStack previous = decorative_items.get(indices_to_swap.get(indices_to_swap.size() - 1));
            ItemStack temp;
            decorative_items.set(indices_to_swap.get(indices_to_swap.size() - 1), decorative_items.get(indices_to_swap.get(0)));
            for (int i = indices_to_swap.size() - 2 ; i > -1; i--) {
                temp = previous;
                previous = decorative_items.get(indices_to_swap.get(i));
                decorative_items.set(indices_to_swap.get(i), temp);
            }
        }
        return decorative_items;
    }
}
