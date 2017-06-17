package ua.gwm.sponge_plugin.crates.caze.cases;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import ua.gwm.sponge_plugin.crates.caze.Case;
import ua.gwm.sponge_plugin.crates.util.GWMCratesUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class BlockCase extends Case {

    protected Collection<Location<World>> locations;
    protected boolean start_preview_on_left_click = false;

    public BlockCase(ConfigurationNode node) {
        super(node);
        ConfigurationNode locations_node = node.getNode("LOCATIONS");
        ConfigurationNode start_preview_on_left_click_node = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
        if (locations_node.isVirtual()) {
            throw new RuntimeException("LOCATIONS node does not exist!");
        }
        locations = new HashSet<Location<World>>();
        for (ConfigurationNode location_node : locations_node.getChildrenList()) {
            locations.add(GWMCratesUtils.parseLocation(location_node));
        }
        start_preview_on_left_click = start_preview_on_left_click_node.getBoolean(false);
    }

    public BlockCase(Optional<BigDecimal> price, Collection<Location<World>> locations) {
        super(price);
        this.locations = locations;
    }

    @Override
    public void add(Player player, int amount) {
    }

    @Override
    public int get(Player player) {
        return Integer.MAX_VALUE;
    }

    public Collection<Location<World>> getLocations() {
        return locations;
    }

    public void setLocations(Collection<Location<World>> locations) {
        this.locations = locations;
    }

    public boolean isStartPreviewOnLeftClick() {
        return start_preview_on_left_click;
    }

    public void setStartPreviewOnLeftClick(boolean start_preview_on_left_click) {
        this.start_preview_on_left_click = start_preview_on_left_click;
    }
}
