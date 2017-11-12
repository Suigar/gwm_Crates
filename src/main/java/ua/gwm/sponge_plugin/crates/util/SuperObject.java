package ua.gwm.sponge_plugin.crates.util;

import ninja.leaping.configurate.ConfigurationNode;

import java.util.Optional;

public abstract class SuperObject {

    private final String type;
    private final Optional<String> id;

    public SuperObject(ConfigurationNode node) {
        ConfigurationNode type_node = node.getNode("TYPE");
        ConfigurationNode id_node = node.getNode("ID");
        if (type_node.isVirtual()) {
            throw new RuntimeException("TYPE node does not exist for Super Object!");
        }
        type = type_node.getString();
        id = id_node.isVirtual() ? Optional.empty() : Optional.of(id_node.getString());
    }

    public SuperObject(String type, Optional<String> id) {
        this.type = type;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public Optional<String> getId() {
        return id;
    }
}
