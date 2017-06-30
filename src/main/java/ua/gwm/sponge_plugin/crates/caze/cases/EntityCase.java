package ua.gwm.sponge_plugin.crates.caze.cases;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.caze.Case;
import ua.gwm.sponge_plugin.crates.util.GWMCratesUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class EntityCase extends Case {

    protected Collection<Location<World>> locations;
    protected EntityType entity_type;
    protected Optional<Text> name = Optional.empty();
    protected boolean start_preview_on_left_click = false;
    private Collection<Entity> entities;

    public EntityCase(ConfigurationNode node) {
        super(node);
        ConfigurationNode locations_node = node.getNode("LOCATIONS");
        ConfigurationNode entity_type_node = node.getNode("ENTITY_TYPE");
        ConfigurationNode name_node = node.getNode("NAME");
        ConfigurationNode start_preview_on_left_click_node = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
        try {
            if (locations_node.isVirtual()) {
                throw new RuntimeException("LOCATIONS node does not exist!");
            }
            if (entity_type_node.isVirtual()) {
                throw new RuntimeException("ENTITY_TYPE node does not exist!");
            }
            locations = new HashSet<Location<World>>();
            for (ConfigurationNode location_node : locations_node.getChildrenList()) {
                locations.add(GWMCratesUtils.parseLocation(location_node));
            }
            entity_type = entity_type_node.getValue(TypeToken.of(EntityType.class));
            if (!name_node.isVirtual()) {
                name = Optional.of(TextSerializers.FORMATTING_CODE.deserialize(name_node.getString()));
            }
            start_preview_on_left_click = start_preview_on_left_click_node.getBoolean(false);
            createEntities();
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Entity Case!", e);
        }
    }

    public EntityCase(Optional<BigDecimal> price, Collection<Location<World>> locations, EntityType entity_type, Optional<Text> name,
                      boolean start_preview_on_left_click) {
        super(price);
        this.locations = locations;
        this.entity_type = entity_type;
        this.name = name;
        this.start_preview_on_left_click = start_preview_on_left_click;
        createEntities();
    }

    private void createEntities() {
        entities = new HashSet<Entity>();
        locations.forEach(location -> {
            World world = location.getExtent();
            Entity entity = world.createEntity(entity_type, location.getPosition());
            entity.setCreator(GWMCrates.PLUGIN_UUID);
            name.ifPresent(text -> entity.offer(Keys.DISPLAY_NAME, text));
            entity.offer(Keys.AI_ENABLED, false);
            entity.offer(Keys.HAS_GRAVITY, false);
            world.spawnEntity(entity, GWMCrates.getInstance().getDefaultCause());
            entities.add(entity);
        });
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

    public EntityType getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(EntityType entity_type) {
        this.entity_type = entity_type;
    }

    public Optional<Text> getName() {
        return name;
    }

    public void setName(Optional<Text> name) {
        this.name = name;
    }

    public boolean isStartPreviewOnLeftClick() {
        return start_preview_on_left_click;
    }

    public void setStartPreviewOnLeftClick(boolean start_preview_on_left_click) {
        this.start_preview_on_left_click = start_preview_on_left_click;
    }

    public Collection<Entity> getEntities() {
        return entities;
    }
}
