package ua.gwm.sponge_plugin.crates.caze;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.math.BigDecimal;
import java.util.Optional;

public abstract class Case {

    private Optional<BigDecimal> price = Optional.empty();

    public Case(ConfigurationNode node) {
        try {
            ConfigurationNode price_node = node.getNode("PRICE");
            if (!price_node.isVirtual()) {
                price = Optional.of(new BigDecimal(price_node.getString("0")));
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Case!", e);
        }
    }

    public Case(Optional<BigDecimal> price) {
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
