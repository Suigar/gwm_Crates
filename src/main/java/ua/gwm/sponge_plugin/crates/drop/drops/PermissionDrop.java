package ua.gwm.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.util.SuperObjectType;
import ua.gwm.sponge_plugin.crates.util.CratesUtils;

import java.math.BigDecimal;
import java.util.Optional;

public class PermissionDrop extends Drop {

    private String permission;
    private Drop drop1;
    private Drop drop2;

    public PermissionDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode permission_node = node.getNode("PERMISSION");
            ConfigurationNode drop1_node = node.getNode("DROP1");
            ConfigurationNode drop2_node = node.getNode("DROP2");
            if (permission_node.isVirtual()) {
                throw new RuntimeException("PERMISSION node does not exist!");
            }
            if (drop1_node.isVirtual()) {
                throw new RuntimeException("DROP1 node does not exist!");
            }
            if (drop2_node.isVirtual()) {
                throw new RuntimeException("DROP2 node does not exist!");
            }
            permission = permission_node.getString();
            drop1 = (Drop) CratesUtils.createSuperObject(drop1_node, SuperObjectType.DROP);
            drop2 = (Drop) CratesUtils.createSuperObject(drop2_node, SuperObjectType.DROP);

        } catch (Exception e) {
            throw new RuntimeException("Exception creating Permission Drop!", e);
        }
    }

    public PermissionDrop(Optional<String> id, Optional<BigDecimal> price, Optional<ItemStack> drop_item, int level,
                          String permission, Drop drop1, Drop drop2) {
        super("PERMISSION", id, price, drop_item, level);
        this.permission = permission;
        this.drop1 = drop1;
        this.drop2 = drop2;
    }

    @Override
    public void apply(Player player) {
        if (player.hasPermission(permission)) {
            drop1.apply(player);
        } else {
            drop2.apply(player);
        }
    }
}
