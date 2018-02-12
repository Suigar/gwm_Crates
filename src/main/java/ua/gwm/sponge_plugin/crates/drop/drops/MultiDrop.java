package ua.gwm.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.util.SuperObjectType;
import ua.gwm.sponge_plugin.crates.util.CratesUtils;

import java.math.BigDecimal;
import java.util.*;

public class MultiDrop extends Drop {

    private List<Drop> drops;
    private boolean give_all = true;

    public MultiDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode drops_node = node.getNode("DROPS");
            ConfigurationNode give_all_node = node.getNode("GIVE_ALL");
            if (drops_node.isVirtual()) {
                throw new RuntimeException("DROPS node does not exist");
            }
            drops = new ArrayList<Drop>();
            for (ConfigurationNode drop_node : drops_node.getChildrenList()) {
                drops.add((Drop) CratesUtils.createSuperObject(drop_node, SuperObjectType.DROP));
            }
            give_all = give_all_node.getBoolean(true);
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Multi Drop!", e);
        }
    }

    public MultiDrop(Optional<String> id, Optional<BigDecimal> price, Optional<ItemStack> drop_item, int level,
                     List<Drop> drops, boolean give_all) {
        super("MULTI", id, price, drop_item, level);
        this.drops = drops;
        this.give_all = give_all;
    }

    @Override
    public void apply(Player player) {
        if (give_all) {
            drops.forEach(drop -> drop.apply(player));
        } else {
            CratesUtils.chooseDropByLevel(drops, player, false).apply(player);
        }
    }

    public List<Drop> getDrops() {
        return drops;
    }

    public void setDrops(List<Drop> drops) {
        this.drops = drops;
    }
}
