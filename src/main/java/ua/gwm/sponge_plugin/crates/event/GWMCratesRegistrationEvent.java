package ua.gwm.sponge_plugin.crates.event;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.ConfigurationDialog;
import ua.gwm.sponge_plugin.crates.util.SuperObject;
import ua.gwm.sponge_plugin.crates.util.SuperObjectStorage;
import ua.gwm.sponge_plugin.crates.util.SuperObjectType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GWMCratesRegistrationEvent extends AbstractEvent {

    private final HashSet<SuperObjectStorage> super_object_storage =
            new HashSet<SuperObjectStorage>();

    public void register(SuperObjectType super_object_type, String type, Class<? extends SuperObject> super_object_class, Optional<Class<? extends ConfigurationDialog>> configuration_dialog) {
        super_object_storage.add(new SuperObjectStorage(super_object_type, type, super_object_class, configuration_dialog));
    }
    @Override
    public Cause getCause() {
        return GWMCrates.getInstance().getCause();
    }

    public Set<SuperObjectStorage> getSuperObjectStorage() {
        return Collections.unmodifiableSet(super_object_storage);
    }
}
