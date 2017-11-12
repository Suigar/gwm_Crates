package ua.gwm.sponge_plugin.crates.drop;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import ua.gwm.sponge_plugin.crates.util.SuperObject;
import ua.gwm.sponge_plugin.crates.util.Utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Drop extends SuperObject {

    private Optional<BigDecimal> price = Optional.empty();
    private int level;
    private Optional<ItemStack> drop_item = Optional.empty();
    private Optional<Integer> fake_level = Optional.empty();
    private Map<String, Integer> permission_levels = new HashMap<String, Integer>();
    private Map<String, Integer> permission_fake_levels = new HashMap<String, Integer>();

    public Drop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode price_node = node.getNode("PRICE");
            ConfigurationNode drop_item_node = node.getNode("DROP_ITEM");
            ConfigurationNode fake_level_node = node.getNode("FAKE_LEVEL");
            ConfigurationNode level_node = node.getNode("LEVEL");
            ConfigurationNode permission_levels_node = node.getNode("PERMISSION_LEVELS");
            ConfigurationNode permission_fake_levels_node = node.getNode("PERMISSION_FAKE_LEVELS");
            if (!price_node.isVirtual()) {
                price = Optional.of(new BigDecimal(price_node.getString("0")));
            }
            if (!drop_item_node.isVirtual()) {
                drop_item = Optional.of(Utils.parseItem(drop_item_node));
            }
            if (!fake_level_node.isVirtual()) {
                fake_level = Optional.of(fake_level_node.getInt(1));
            }
            if (!permission_levels_node.isVirtual()) {
                permission_levels = permission_levels_node.getValue(new TypeToken<Map<String, Integer>>(){});
            }
            if (!permission_fake_levels_node.isVirtual()) {
                permission_fake_levels = permission_fake_levels_node.getValue(new TypeToken<Map<String, Integer>>(){});
            }
            level = level_node.getInt(1);
            if (level < 1) {
                level = 1;
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Drop!", e);
        }
    }

    public Drop(String type, Optional<String> id, Optional<BigDecimal> price, Optional<ItemStack> drop_item, int level) {
        super(type, id);
        this.price = price;
        this.drop_item = drop_item;
        if (level < 1) {
            level = 1;
        }
        this.level = level;
    }

    public abstract void apply(Player player);

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

    public Optional<Integer> getFakeLevel() {
        return fake_level;
    }

    public void setFakeLevel(Optional<Integer> fake_level) {
        this.fake_level = fake_level;
    }

    public Map<String, Integer> getPermissionLevels() {
        return permission_levels;
    }

    public void setPermissionLevels(Map<String, Integer> permission_levels) {
        this.permission_levels = permission_levels;
    }

    public Map<String, Integer> getPermissionFakeLevels() {
        return permission_fake_levels;
    }

    public void setPermissionFakeLevels(Map<String, Integer> permission_fake_levels) {
        this.permission_fake_levels = permission_fake_levels;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
