package ua.gwm.sponge_plugin.crates.listener;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Container;
import ua.gwm.sponge_plugin.crates.open_manager.open_managers.FirstOpenManager;

public class FirstGuiOpenManagerListener {

    @Listener(order = Order.LATE)
    public void cancelClick(ClickInventoryEvent event) {
        Container container = event.getTargetInventory();
        if (FirstOpenManager.FIRST_GUI_CONTAINERS.containsKey(container)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void cancelClose(InteractInventoryEvent.Close event) {
        Container container = event.getTargetInventory();
        if (!FirstOpenManager.SHOWN_GUI.contains(container) &&
                FirstOpenManager.FIRST_GUI_CONTAINERS.containsKey(container) &&
                FirstOpenManager.FIRST_GUI_CONTAINERS.get(container).getKey().isForbidClose()) {
            event.setCancelled(true);
        }
    }
}
