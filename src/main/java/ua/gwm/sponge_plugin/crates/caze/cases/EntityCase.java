package ua.gwm.sponge_plugin.crates.caze.cases;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import ua.gwm.sponge_plugin.crates.caze.Case;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class EntityCase extends Case {

    private UUID entity_uuid;
    private boolean start_preview_on_left_click = false;

    public EntityCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode entity_uuid_node = node.getNode("ENTITY_UUID");
            ConfigurationNode start_preview_on_left_click_node = node.getNode("START_PREVIEW_ON_LEFT_CLICK");
            if (entity_uuid_node.isVirtual()) {
                throw new RuntimeException("ENTITY_UUID node does not exist!");
            }
            entity_uuid = entity_uuid_node.getValue(TypeToken.of(UUID.class));
            start_preview_on_left_click = start_preview_on_left_click_node.getBoolean(false);
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Entity Case!", e);
        }
    }

    public EntityCase(Optional<String> id, Optional<BigDecimal> price, UUID entity_uuid, boolean start_preview_on_left_click) {
        super("ENTITY", id, price);
        this.entity_uuid = entity_uuid;
        this.start_preview_on_left_click = start_preview_on_left_click;
    }

    @Override
    public void add(Player player, int amount) {
    }

    @Override
    public int get(Player player) {
        return Integer.MAX_VALUE;
    }

    public UUID getEntityUuid() {
        return entity_uuid;
    }

    public void setEntityUuid(UUID entity_uuid) {
        this.entity_uuid = entity_uuid;
    }

    public boolean isStartPreviewOnLeftClick() {
        return start_preview_on_left_click;
    }

    public void setStartPreviewOnLeftClick(boolean start_preview_on_left_click) {
        this.start_preview_on_left_click = start_preview_on_left_click;
    }
}
