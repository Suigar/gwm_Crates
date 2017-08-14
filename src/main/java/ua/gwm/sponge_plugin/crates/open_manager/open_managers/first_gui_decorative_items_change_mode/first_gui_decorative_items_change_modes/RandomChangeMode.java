package ua.gwm.sponge_plugin.crates.open_manager.open_managers.first_gui_decorative_items_change_mode.first_gui_decorative_items_change_modes;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStack;
import ua.gwm.sponge_plugin.crates.open_manager.open_managers.first_gui_decorative_items_change_mode.FirstGuiDecorativeItemsChangeMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomChangeMode extends FirstGuiDecorativeItemsChangeMode {

    public RandomChangeMode(ConfigurationNode node) {
        super(node);
    }

    public RandomChangeMode(int change_delay, List<Integer> ignored_indices) {
        super(change_delay, ignored_indices);
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
        Collections.shuffle(indices_to_swap);
        for (int i = 0; i + 1 < indices_to_swap.size(); i += 2) {
            Collections.swap(decorative_items, indices_to_swap.get(i), indices_to_swap.get(i + 1));
        }
        return decorative_items;
    }
}
