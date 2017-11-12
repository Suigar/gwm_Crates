package ua.gwm.sponge_plugin.crates.listener;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Tristate;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.event.PlayerOpenCrateEvent;
import ua.gwm.sponge_plugin.crates.event.PlayerOpenedCrateEvent;
import ua.gwm.sponge_plugin.crates.manager.Manager;
import ua.gwm.sponge_plugin.crates.util.LanguageUtils;
import ua.gwm.sponge_plugin.crates.util.Pair;

import java.util.Optional;

public class DebugCrateListener {

    @Listener
    @IsCancelled(value = Tristate.FALSE)
    public void onOpen(PlayerOpenCrateEvent event) {
        Player player = event.getPlayer();
        Manager manager = event.getManager();
        if (GWMCrates.getInstance().isDebugEnabled()) {
            GWMCrates.getInstance().getLogger().info("Player " + player.getName() + " (" + player.getUniqueId() + ") trying to open manager \"" + manager.getId() + "\"!");
        }
    }

    @Listener
    public void onOpened(PlayerOpenedCrateEvent event) {
        Player player = event.getPlayer();
        Manager manager = event.getManager();
        Drop drop = event.getDrop();
        if (manager.isSendOpenMessage()) {
            Optional<String> optional_custom_open_message = manager.getCustomOpenMessage();
            if (optional_custom_open_message.isPresent()) {
                player.sendMessage(TextSerializers.FORMATTING_CODE.
                        deserialize(optional_custom_open_message.get().
                                replace("%MANAGER%", manager.getName())));
            } else {
                player.sendMessage(LanguageUtils.getText("SUCCESSFULLY_OPENED_MANAGER",
                        new Pair<String, String>("%MANAGER%", manager.getName())));
            }
        }
        if (GWMCrates.getInstance().isDebugEnabled()) {
            GWMCrates.getInstance().getLogger().info("Player " + player.getName() + " (" + player.getUniqueId() + ") opened manager \"" + manager.getId() + "\" and won drop with id \"" + (drop == null ? "null" : drop.getId().orElse("Unknown ID")) + "\"!");
        }
    }
}
