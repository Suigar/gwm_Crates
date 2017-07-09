package ua.gwm.sponge_plugin.crates.key;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.math.BigDecimal;
import java.util.Optional;

public abstract class Key {

    private Optional<BigDecimal> price = Optional.empty();

    public Key(ConfigurationNode node) {
        try {
            ConfigurationNode price_node = node.getNode("PRICE");
            if (!price_node.isVirtual()) {
                price = Optional.of(new BigDecimal(price_node.getString("0")));
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Key!", e);
        }
    }

    public Key(Optional<BigDecimal> price) {
        this.price = price;
    }

    public abstract void add(Player player, int amount);

    public abstract int get(Player player);

    public Optional<BigDecimal> getPrice() {
        return price;
    }

    public void setPrice(Optional<BigDecimal> price) {
        this.price = price;
    }
}
