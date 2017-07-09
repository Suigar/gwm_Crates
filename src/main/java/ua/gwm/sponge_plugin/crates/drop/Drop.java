package ua.gwm.sponge_plugin.crates.drop;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import ua.gwm.sponge_plugin.crates.util.GWMCratesUtils;

import java.math.BigDecimal;
import java.util.Optional;

public abstract class Drop {

    private Optional<String> id = Optional.empty();
    private Optional<BigDecimal> price = Optional.empty();
    private Optional<ItemStack> drop_item = Optional.empty();
    private int level;

    public Drop(ConfigurationNode node) {
        try {
            ConfigurationNode id_node = node.getNode("ID");
            ConfigurationNode price_node = node.getNode("PRICE");
            ConfigurationNode drop_item_node = node.getNode("DROP_ITEM");
            ConfigurationNode level_node = node.getNode("LEVEL");
            if (!id_node.isVirtual()) {
                id = Optional.of(id_node.getString());
            }
            if (!price_node.isVirtual()) {
                price = Optional.of(new BigDecimal(price_node.getString("0")));
            }
            if (!drop_item_node.isVirtual()) {
                drop_item = Optional.of(GWMCratesUtils.parseItem(drop_item_node));
            }
            level = level_node.getInt(1);
            if (level < 1) {
                level = 1;
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Drop!", e);
        }
    }

    public Drop(Optional<String> id, Optional<BigDecimal> price, Optional<ItemStack> drop_item, int level) {
        this.id = id;
        this.price = price;
        this.drop_item = drop_item;
        if (level < 1) {
            level = 1;
        }
        this.level = level;
    }

    public abstract void apply(Player player);

    public Optional<String> getId() {
        return id;
    }

    public Optional<BigDecimal> getPrice() {
        return price;
    }

    public void setPrice(Optional<BigDecimal> price) {
        this.price = price;
    }

    public Optional<ItemStack> getDropItem() {
        return drop_item;
    }

    public void setDropItem(Optional<ItemStack> drop_item) {
        this.drop_item = drop_item;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
