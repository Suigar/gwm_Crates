package ua.gwm.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import ua.gwm.sponge_plugin.crates.key.Key;
import ua.gwm.sponge_plugin.crates.util.SuperObjectType;
import ua.gwm.sponge_plugin.crates.util.CratesUtils;

import java.math.BigDecimal;
import java.util.Optional;

public class MultipleAmountKey extends Key {

    private Key child_key;
    private int amount;

    public MultipleAmountKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode child_key_node = node.getNode("CHILD_KEY");
            ConfigurationNode amount_node = node.getNode("AMOUNT");
            if (child_key_node.isVirtual()) {
                throw new RuntimeException("CHILD_KEY node does not exist!");
            }
            child_key = (Key) CratesUtils.createSuperObject(child_key_node, SuperObjectType.KEY);
            amount = amount_node.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Multiple Amount Key!", e);
        }
    }

    public MultipleAmountKey(Optional<BigDecimal> price, Optional<String> id, Key child_key, int amount) {
        super("TIMED", id, price);
        this.child_key = child_key;
        this.amount = amount;
    }

    @Override
    public void add(Player player, int amount) {
        child_key.add(player, amount*this.amount);
    }

    @Override
    public int get(Player player) {
        return child_key.get(player)/amount;
    }

    public Key getChildKey() {
        return child_key;
    }

    public void setChildKey(Key child_key) {
        this.child_key = child_key;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
