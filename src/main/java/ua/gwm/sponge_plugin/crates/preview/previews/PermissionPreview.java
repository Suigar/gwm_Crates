package ua.gwm.sponge_plugin.crates.preview.previews;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.manager.Manager;
import ua.gwm.sponge_plugin.crates.preview.Preview;
import ua.gwm.sponge_plugin.crates.util.SuperObjectType;
import ua.gwm.sponge_plugin.crates.util.Utils;

import java.util.Optional;

public class PermissionPreview extends Preview {

    private String permission;
    private Preview preview1;
    private Preview preview2;

    public PermissionPreview(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode permission_node = node.getNode("PERMISSION");
            ConfigurationNode preview1_node = node.getNode("PREVIEW1");
            ConfigurationNode preview2_node = node.getNode("PREVIEW2");
            if (permission_node.isVirtual()) {
                throw new RuntimeException("PERMISSION node does not exist!");
            }
            if (preview1_node.isVirtual()) {
                throw new RuntimeException("PREVIEW1 node does not exist!");
            }
            if (preview2_node.isVirtual()) {
                throw new RuntimeException("PREVIEW2 node does not exist!");
            }
            permission = permission_node.getString();
            preview1 = (Preview) Utils.createSuperObject(preview1_node, SuperObjectType.PREVIEW);
            preview2 = (Preview) Utils.createSuperObject(preview2_node, SuperObjectType.PREVIEW);

        } catch (Exception e) {
            throw new RuntimeException("Exception creating Permission Preview!", e);
        }
    }

    public PermissionPreview(Optional<String> id) {
        super("PERMISSION", id);
    }

    @Override
    public void preview(Player player, Manager manager) {
        if (player.hasPermission(permission)) {
            preview1.preview(player, manager);
        } else {
            preview2.preview(player, manager);
        }
    }
}
