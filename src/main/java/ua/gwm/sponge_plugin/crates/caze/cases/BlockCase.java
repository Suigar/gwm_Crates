package ua.gwm.sponge_plugin.crates.caze.cases;

import de.randombyte.holograms.api.HologramsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.caze.Case;
import ua.gwm.sponge_plugin.crates.util.Utils;

import java.math.BigDecimal;
import java.util.Optional;

public class BlockCase extends Case {

    private Location<World> location;
    private Optional<Text> hologram = Optional.empty();
    private boolean start_preview_on_left_click = false;
    private Optional<HologramsService.Hologram> created_hologram = Optional.empty();

    public BlockCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode location_node = node.getNode("LOCATION");
            ConfigurationNode hologram_node = node.getNode("HOLOGRAM");
            ConfigurationNode start_preview_on_left_click_node = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            if (location_node.isVirtual()) {
                throw new RuntimeException("LOCATION node does not exist!");
            }
            location = Utils.parseLocation(location_node);
            if (!hologram_node.isVirtual()) {
                hologram = Optional.of(TextSerializers.FORMATTING_CODE.deserialize(hologram_node.getString()));
            }
            created_hologram = Utils.tryCreateHologram(location, hologram);
            start_preview_on_left_click = start_preview_on_left_click_node.getBoolean(false);
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("Exception creating Block Case!", e);
        }
    }

    public BlockCase(Optional<String> id, Optional<BigDecimal> price, Location<World> location, Optional<Text> hologram,
                     boolean start_preview_on_left_click) {
        super("BLOCK", id, price);
        this.location = location;
        this.hologram = hologram;
        this.start_preview_on_left_click = start_preview_on_left_click;
    }

    @Override
    public void add(Player player, int amount) {
    }

    @Override
    public int get(Player player) {
        return Integer.MAX_VALUE;
    }

    public Location<World> getLocation() {
        return location;
    }

    public void setLocation(Location<World> location) {
        this.location = location;
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

    public Optional<HologramsService.Hologram> getCreatedHologram() {
        return created_hologram;
    }

    public void setCreatedHologram(Optional<HologramsService.Hologram> created_hologram) {
        this.created_hologram = created_hologram;
    }
}
