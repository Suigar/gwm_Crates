package ua.gwm.sponge_plugin.crates.caze.cases;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class EntityCase extends Case {

    private Location<World> location;
    private EntityType entity_type;
    private LinkedHashMap nbt = new LinkedHashMap();
    private Optional<Text> name = Optional.empty();
    private boolean start_preview_on_left_click = false;
    private Entity entity;

    public EntityCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode location_node = node.getNode("LOCATION");
            ConfigurationNode entity_type_node = node.getNode("ENTITY_TYPE");
            ConfigurationNode nbt_node = node.getNode("NBT");
            ConfigurationNode name_node = node.getNode("NAME");
            ConfigurationNode start_preview_on_left_click_node = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            if (location_node.isVirtual()) {
                throw new RuntimeException("LOCATION node does not exist!");
            }
            if (entity_type_node.isVirtual()) {
                throw new RuntimeException("ENTITY_TYPE node does not exist!");
            }
            if(!nbt_node.isVirtual()) {
                nbt = (LinkedHashMap) nbt_node.getValue();
            }
            location = GWMCratesUtils.parseLocation(location_node);
            entity_type = entity_type_node.getValue(TypeToken.of(EntityType.class));
            if (!name_node.isVirtual()) {
                name = Optional.of(TextSerializers.FORMATTING_CODE.deserialize(name_node.getString()));
            }
            start_preview_on_left_click = start_preview_on_left_click_node.getBoolean(false);
            createEntity();
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Entity Case!", e);
        }
    }

    public EntityCase(Optional<BigDecimal> price, Location<World> location, EntityType entity_type, LinkedHashMap nbt,
                      Optional<Text> name, boolean start_preview_on_left_click) {
        super(price);
        this.location = location;
        this.entity_type = entity_type;
        this.nbt = nbt;
        this.name = name;
        this.start_preview_on_left_click = start_preview_on_left_click;
        createEntity();
    }

    private void createEntity() {
        World world = location.getExtent();
        location.getExtent().loadChunk(location.getChunkPosition(), true);
        Entity entity = world.createEntity(entity_type, location.getPosition());
        entity.setCreator(GWMCrates.PLUGIN_UUID);
        if (name.isPresent()) {
            entity.offer(Keys.DISPLAY_NAME, name.get());
            entity.offer(Keys.CUSTOM_NAME_VISIBLE, true);
        }
        entity.offer(Keys.AI_ENABLED, false);
        entity.offer(Keys.HAS_GRAVITY, false);
        DataContainer container = entity.toContainer();
        LinkedHashMap nbt_map = new LinkedHashMap(nbt);
        if (container.get(DataQuery.of("UnsafeData")).isPresent()) {
            Map unsafe_data_map = container.getMap(DataQuery.of("UnsafeData")).get();
            nbt_map.putAll(unsafe_data_map);
        }
        entity.setRawData(container.set(DataQuery.of("UnsafeData"), nbt_map));
        entity = world.createEntity(container, location.getPosition()).get();
        world.spawnEntity(entity, GWMCrates.getInstance().getDefaultCause());
        this.entity = entity;
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

    public EntityType getEntityType() {
        return entity_type;
    }

    public void setEntityType(EntityType entity_type) {
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

    public Entity getEntity() {
        return entity;
    }
}
