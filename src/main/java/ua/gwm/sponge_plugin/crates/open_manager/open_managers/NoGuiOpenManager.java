package ua.gwm.sponge_plugin.crates.open_manager.open_managers;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.manager.Manager;
import ua.gwm.sponge_plugin.crates.open_manager.OpenManager;
import ua.gwm.sponge_plugin.crates.util.LanguageUtils;
import ua.gwm.sponge_plugin.crates.util.Pair;

import java.util.Optional;

public class NoGuiOpenManager extends OpenManager {

    public NoGuiOpenManager(ConfigurationNode node) {
        super(node);
    }

    public NoGuiOpenManager(Optional<SoundType> open_sound) {
        super(open_sound, Optional.empty());
    }

    @Override
    public void open(Player player, Manager manager) {
        Drop drop = manager.getRandomDrop();
        drop.apply(player);
        getOpenSound().ifPresent(open_sound -> player.playSound(open_sound, player.getLocation().getPosition(), 1.));
        player.sendMessage(LanguageUtils.getText("SUCCESSFULLY_OPENED_MANAGER",
                new Pair<String, String>("%MANAGER%", manager.getName())));
    }

    @Override
    public boolean canOpen(Player player, Manager manager) {
        return true;
    }
}
