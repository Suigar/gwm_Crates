package ua.gwm.sponge_plugin.crates.caze.cases;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import ua.gwm.sponge_plugin.crates.caze.Case;
import ua.gwm.sponge_plugin.crates.util.CratesUtils;

import java.math.BigDecimal;
import java.util.Optional;

public class ItemCase extends Case {

    private ItemStack item;
    private boolean start_preview_on_left_click = false;

    public ItemCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode item_node = node.getNode("ITEM");
            ConfigurationNode start_preview_on_left_click_node = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            if (item_node.isVirtual()) {
                throw new RuntimeException("ITEM node does not exist!");
            }
            item = CratesUtils.parseItem(item_node);
            start_preview_on_left_click = start_preview_on_left_click_node.getBoolean(false);
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Item Case!", e);
        }
    }

    public ItemCase(Optional<BigDecimal> price, Optional<String> id, ItemStack item, boolean start_preview_on_left_click) {
        super("ITEM", id, price);
        this.item = item;
        this.start_preview_on_left_click = start_preview_on_left_click;
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
        return item.copy();
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public boolean isStartPreviewOnLeftClick() {
        return start_preview_on_left_click;
    }

    public void setStartPreviewOnLeftClick(boolean start_preview_on_left_click) {
        this.start_preview_on_left_click = start_preview_on_left_click;
    }
}
