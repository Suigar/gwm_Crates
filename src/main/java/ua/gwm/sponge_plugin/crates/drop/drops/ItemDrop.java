package ua.gwm.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.util.CratesUtils;

import java.math.BigDecimal;
import java.util.Optional;

public class ItemDrop extends Drop {

    private ItemStack item;

    public ItemDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode item_node = node.getNode("ITEM");
            if (item_node.isVirtual()) {
                throw new RuntimeException("ITEM node does not exist!");
            }
            item = CratesUtils.parseItem(item_node);
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Item Drop!", e);
        }
    }

    public ItemDrop(Optional<String> id, Optional<BigDecimal> price, Optional<ItemStack> drop_item, int level,
                    ItemStack item) {
        super("ITEM", id, price, drop_item, level);
        this.item = item;
    }

    @Override
    public void apply(Player player) {
        player.getInventory().offer(item.copy());
    }

    @Override
    public Optional<ItemStack> getDropItem() {
        Optional<ItemStack> super_drop_item = super.getDropItem();
        return super_drop_item.isPresent() ? super_drop_item : Optional.of(item.copy());
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}
