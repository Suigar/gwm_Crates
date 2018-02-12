package ua.gwm.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import ua.gwm.sponge_plugin.crates.key.Key;
import ua.gwm.sponge_plugin.crates.util.CratesUtils;

import java.math.BigDecimal;
import java.util.Optional;

public class ItemKey extends Key {

    private ItemStack item;

    public ItemKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode item_node = node.getNode("ITEM");
            if (item_node.isVirtual()) {
                throw new RuntimeException("ITEM node does not exist!");
            }
            item = CratesUtils.parseItem(item_node);
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Item Key!", e);
        }
    }

    public ItemKey(Optional<BigDecimal> price, Optional<String> id, ItemStack item) {
        super("ITEM", id, price);
        this.item = item;
    }

    @Override
    public void add(Player player, int amount) {
        CratesUtils.addItemStack(player, item, amount);
    }

    @Override
    public int get(Player player) {
        return CratesUtils.getItemStackAmount(player, item);
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}
