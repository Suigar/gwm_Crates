package ua.gwm.sponge_plugin.crates.caze.cases;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.caze.Case;
import ua.gwm.sponge_plugin.crates.hologram.Hologram;
import ua.gwm.sponge_plugin.crates.util.GWMCratesUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class BlockCase extends Case {

    protected Collection<Location<World>> locations;
    protected Optional<Text> hologram = Optional.empty();
    protected boolean start_preview_on_left_click = false;

    public BlockCase(ConfigurationNode node) {
        super(node);
        ConfigurationNode locations_node = node.getNode("LOCATIONS");
        ConfigurationNode hologram_node = node.getNode("HOLOGRAM");
        ConfigurationNode start_preview_on_left_click_node = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
        try {
            if (locations_node.isVirtual()) {
                throw new RuntimeException("LOCATIONS node does not exist!");
            }
            locations = new HashSet<Location<World>>();
            if (!hologram_node.isVirtual()) {
                hologram = Optional.of(TextSerializers.FORMATTING_CODE.deserialize(hologram_node.getString()));
            }
            for (ConfigurationNode location_node : locations_node.getChildrenList()) {
                locations.add(GWMCratesUtils.parseLocation(location_node));
            }
            hologram.ifPresent(name -> locations.forEach(location -> Hologram.createHologram(location.add(0.5, -1.2, 0.5), name)));
            start_preview_on_left_click = start_preview_on_left_click_node.getBoolean(false);
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("Exception creating Block Case!", e);
        }
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

    public Optional<Text> getHologram() {
        return hologram;
    }

    public void setHologram(Optional<Text> hologram) {
        this.hologram = hologram;
    }

    public boolean isStartPreviewOnLeftClick() {
        return start_preview_on_left_click;
    }

    public void setStartPreviewOnLeftClick(boolean start_preview_on_left_click) {
        this.start_preview_on_left_click = start_preview_on_left_click;
    }
}
