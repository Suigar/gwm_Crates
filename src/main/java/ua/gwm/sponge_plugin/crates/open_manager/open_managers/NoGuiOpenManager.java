package ua.gwm.sponge_plugin.crates.open_manager.open_managers;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.event.PlayerOpenCrateEvent;
import ua.gwm.sponge_plugin.crates.event.PlayerOpenedCrateEvent;
import ua.gwm.sponge_plugin.crates.manager.Manager;
import ua.gwm.sponge_plugin.crates.open_manager.OpenManager;
import ua.gwm.sponge_plugin.crates.util.GWMCratesUtils;
import ua.gwm.sponge_plugin.crates.util.LanguageUtils;
import ua.gwm.sponge_plugin.crates.util.Pair;

import java.util.Optional;

public class NoGuiOpenManager extends OpenManager {

    public NoGuiOpenManager(ConfigurationNode node) {
        super(node);
    }

    public NoGuiOpenManager(Optional<SoundType> open_sound) {
        super(open_sound);
    }

    @Override
    public void open(Player player, Manager manager) {
        PlayerOpenCrateEvent open_event = new PlayerOpenCrateEvent(player, manager);
        Sponge.getEventManager().post(open_event);
        if (open_event.isCancelled()) return;
        Drop drop = GWMCratesUtils.chooseDropByLevel(manager.getDrops(), player, false);
        drop.apply(player);
        getOpenSound().ifPresent(open_sound -> player.playSound(open_sound, player.getLocation().getPosition(), 1.));
        PlayerOpenedCrateEvent opened_event = new PlayerOpenedCrateEvent(player, manager,
                LanguageUtils.getText("SUCCESSFULLY_OPENED_MANAGER",
                        new Pair<String, String>("%MANAGER%", manager.getName())));
        Sponge.getEventManager().post(opened_event);
        player.sendMessage(opened_event.getMessage());
    }

    @Override
    public boolean canOpen(Player player, Manager manager) {
        return true;
    }
}
