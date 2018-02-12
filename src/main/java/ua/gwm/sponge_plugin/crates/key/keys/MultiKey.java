package ua.gwm.sponge_plugin.crates.key.keys;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import ua.gwm.sponge_plugin.crates.key.Key;
import ua.gwm.sponge_plugin.crates.util.SuperObjectType;
import ua.gwm.sponge_plugin.crates.util.CratesUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MultiKey extends Key {

    private List<Key> keys;
    private boolean all_keys_needed;

    public MultiKey(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode keys_node = node.getNode("KEYS");
            ConfigurationNode all_keys_needed_node = node.getNode("ALL_KEYS_NEEDED");
            if (keys_node.isVirtual()) {
                throw new RuntimeException("KEYS node does not exist");
            }
            keys = new ArrayList<Key>();
            for (ConfigurationNode key_node : keys_node.getChildrenList()) {
                keys.add((Key) CratesUtils.createSuperObject(key_node, SuperObjectType.KEY));
            }
            all_keys_needed = all_keys_needed_node.getBoolean(false);
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Multi Key!", e);
        }
    }

    public MultiKey(Optional<BigDecimal> price, Optional<String> id, List<Key> keys, boolean all_keys_needed) {
        super("MULTI", id, price);
        this.keys = keys;
        this.all_keys_needed = all_keys_needed;
    }

    @Override
    public void add(Player player, int amount) {
        if (all_keys_needed) {
            for (Key key : keys) {
                key.add(player, amount);
            }
        } else if (keys.size() > 0) {
            if (amount > 0) {
                keys.iterator().next().add(player, amount);
            } else if (amount < 0) {
                amount = -amount;
                for (Key key : keys) {
                    int value = key.get(player);
                    if (value > 0) {
                        if (value >= amount) {
                            key.add(player, -amount);
                            break;
                        } else {
                            key.add(player, 0);
                            amount -= value;
                        }
                    }
                }
            }
        }
    }

    @Override
    public int get(Player player) {
        if (all_keys_needed) {
            if (keys.size() == 0) {
                return 0;
            } else if (keys.size() == 1) {
                return keys.iterator().next().get(player);
            } else {
                int min = Integer.MAX_VALUE;
                for (Key key : keys) {
                    int value = key.get(player);
                    if (value < 0) {
                        return 0;
                    } else if (value < min) {
                        min = value;
                    }
                }
                return min;
            }
        } else {
            int sum = 0;
            for (Key key : keys) {
                sum += key.get(player);
            }
            return sum;
        }
    }
}
