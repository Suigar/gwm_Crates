package ua.gwm.sponge_plugin.crates.manager;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import ua.gwm.sponge_plugin.crates.caze.Case;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.key.Key;
import ua.gwm.sponge_plugin.crates.open_manager.OpenManager;
import ua.gwm.sponge_plugin.crates.preview.Preview;
import ua.gwm.sponge_plugin.crates.util.SuperObjectType;
import ua.gwm.sponge_plugin.crates.util.Utils;

import java.util.*;

public class Manager {

    private String id;
    private String name;
    private Case caze;
    private Key key;
    private OpenManager open_manager;
    private List<Drop> drops;
    private Optional<Preview> preview = Optional.empty();
    private boolean send_open_message = true;
    private Optional<String> custom_open_message = Optional.empty();
    private Optional<Text> custom_info = Optional.empty();

    public Manager(ConfigurationNode node) {
        ConfigurationNode id_node = node.getNode("ID");
        ConfigurationNode name_node = node.getNode("NAME");
        ConfigurationNode case_node = node.getNode("CASE");
        ConfigurationNode key_node = node.getNode("KEY");
        ConfigurationNode open_manager_node = node.getNode("OPEN_MANAGER");
        ConfigurationNode drops_node = node.getNode("DROPS");
        ConfigurationNode preview_node = node.getNode("PREVIEW");
        ConfigurationNode send_open_message_node = node.getNode("SEND_OPEN_MESSAGE");
        ConfigurationNode custom_open_message_node = node.getNode("CUSTOM_OPEN_MESSAGE");
        ConfigurationNode custom_info_node = node.getNode("CUSTOM_INFO");
        if (id_node.isVirtual()) {
            throw new RuntimeException("ID node does not exist!");
        }
        if (name_node.isVirtual()) {
            throw new RuntimeException("NAME node does not exist!");
        }
        if (case_node.isVirtual()) {
            throw new RuntimeException("CASE node does not exist!");
        }
        if (key_node.isVirtual()) {
            throw new RuntimeException("KEY node does not exist!");
        }
        if (open_manager_node.isVirtual()) {
            throw new RuntimeException("OPEN_MANGER node does not exist!");
        }
        if (drops_node.isVirtual()) {
            throw new RuntimeException("DROPS node does not exist!");
        }
        id = id_node.getString();
        name = name_node.getString();
        caze = (Case) Utils.createSuperObject(case_node, SuperObjectType.CASE);
        key = (Key) Utils.createSuperObject(key_node, SuperObjectType.KEY);
        drops = new ArrayList<Drop>();
        for (ConfigurationNode drop_node : drops_node.getChildrenList()) {
            drops.add((Drop) Utils.createSuperObject(drop_node, SuperObjectType.DROP));
        }
        open_manager = (OpenManager) Utils.createSuperObject(open_manager_node, SuperObjectType.OPEN_MANAGER);
        if (!preview_node.isVirtual()) {
            preview = Optional.of((Preview) Utils.createSuperObject(preview_node, SuperObjectType.PREVIEW));
        }
        send_open_message = send_open_message_node.getBoolean(true);
        if (!custom_open_message_node.isVirtual()) {
            custom_open_message = Optional.of(custom_open_message_node.getString());
        }
        if (!custom_info_node.isVirtual()) {
            custom_info = Optional.of(TextSerializers.FORMATTING_CODE.deserialize(custom_info_node.getString()));
        }
    }

    public Manager(String id, String name, Case caze, Key key, List<Drop> drops, OpenManager open_manager, Optional<Preview> preview) {
        this.id = id;
        this.name = name;
        this.caze = caze;
        this.key = key;
        this.drops = drops;
        this.open_manager = open_manager;
        this.preview = preview;
    }

    public Optional<Drop> getDropById(String id) {
        for (Drop drop : drops) {
            if (drop.getId().isPresent() && drop.getId().get().equals(id)) {
                return Optional.of(drop);
            }
        }
        return Optional.empty();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Case getCase() {
        return caze;
    }

    public void setCase(Case caze) {
        this.caze = caze;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public List<Drop> getDrops() {
        return drops;
    }

    public void setDrops(List<Drop> drops) {
        this.drops = drops;
    }

    public OpenManager getOpenManager() {
        return open_manager;
    }

    public void setOpenManager(OpenManager open_manager) {
        this.open_manager = open_manager;
    }

    public Optional<Preview> getPreview() {
        return preview;
    }

    public void setPreview(Optional<Preview> preview) {
        this.preview = preview;
    }

    public boolean isSendOpenMessage() {
        return send_open_message;
    }

    public void setSendOpenMessage(boolean send_open_message) {
        this.send_open_message = send_open_message;
    }

    public Optional<String> getCustomOpenMessage() {
        return custom_open_message;
    }

    public void setCustomOpenMessage(Optional<String> custom_open_message) {
        this.custom_open_message = custom_open_message;
    }

    public Optional<Text> getCustomInfo() {
        return custom_info;
    }

    public void setCustomInfo(Optional<Text> custom_info) {
        this.custom_info = custom_info;
    }
}
