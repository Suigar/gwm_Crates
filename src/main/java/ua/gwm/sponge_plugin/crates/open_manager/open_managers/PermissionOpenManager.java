package ua.gwm.sponge_plugin.crates.open_manager.open_managers;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.manager.Manager;
import ua.gwm.sponge_plugin.crates.open_manager.OpenManager;
import ua.gwm.sponge_plugin.crates.util.SuperObjectType;
import ua.gwm.sponge_plugin.crates.util.Utils;

import java.util.Optional;

public class PermissionOpenManager extends OpenManager {

    private String permission;
    private OpenManager open_manager1;
    private OpenManager open_manager2;

    public PermissionOpenManager(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode permission_node = node.getNode("PERMISSION");
            ConfigurationNode open_manager1_node = node.getNode("OPEN_MANAGER1");
            ConfigurationNode open_manager2_node = node.getNode("OPEN_MANAGER2");
            if (permission_node.isVirtual()) {
                throw new RuntimeException("PERMISSION node does not exist!");
            }
            if (open_manager1_node.isVirtual()) {
                throw new RuntimeException("OPEN_MANAGER1 node does not exist!");
            }
            if (open_manager2_node.isVirtual()) {
                throw new RuntimeException("OPEN_MANAGER2 node does not exist!");
            }
            permission = permission_node.getString();
            open_manager1 = (OpenManager) Utils.createSuperObject(open_manager1_node, SuperObjectType.OPEN_MANAGER);
            open_manager2 = (OpenManager) Utils.createSuperObject(open_manager2_node, SuperObjectType.OPEN_MANAGER);

        } catch (Exception e) {
            throw new RuntimeException("Exception creating Permission Open Manager!", e);
        }
    }

    public PermissionOpenManager(Optional<String> id, Optional<SoundType> open_sound) {
        super("PERMISSION", id, open_sound);
    }

    @Override
    public void open(Player player, Manager manager) {
        if (player.hasPermission(permission)) {
            open_manager1.open(player, manager);
        } else {
            open_manager2.open(player, manager);
        }
    }
}
