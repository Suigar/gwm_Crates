package ua.gwm.sponge_plugin.crates.util;

import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.ConfigurationDialog;

import java.util.Optional;

public class SuperObjectStorage {

    private final SuperObjectType super_object_type;
    private final String type;
    private final Class<? extends SuperObject> super_object_class;
    private final Optional<Class<? extends ConfigurationDialog>> configuration_dialog;

    public SuperObjectStorage(SuperObjectType super_object_type, String type,
                              Class<? extends SuperObject> super_object_class,
                              Optional<Class<? extends ConfigurationDialog>> configuration_dialog) {
        this.super_object_type = super_object_type;
        this.type = type;
        this.super_object_class = super_object_class;
        this.configuration_dialog = configuration_dialog;
    }

    public SuperObjectType getSuperObjectType() {
        return super_object_type;
    }

    public String getType() {
        return type;
    }

    public Class<? extends SuperObject> getSuperObjectClass() {
        return super_object_class;
    }

    public Optional<Class<? extends ConfigurationDialog>> getConfigurationDialog() {
        return configuration_dialog;
    }
}
