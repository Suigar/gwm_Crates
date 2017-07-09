package ua.gwm.sponge_plugin.crates.listener;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.caze.cases.EntityCase;
import ua.gwm.sponge_plugin.crates.hologram.Hologram;
import ua.gwm.sponge_plugin.crates.manager.Manager;

public class ExtraEntityRemover {

    @Listener
    public void deleteHologram(SpawnEntityEvent.ChunkLoad event) {
        for (Entity entity : event.getEntities()) {
            if (entity.getCreator().isPresent() && entity.getCreator().get().equals(GWMCrates.PLUGIN_UUID) &&
                    !isHologramExist(entity) && !isEntityCaseExist(entity)) {
                Sponge.getScheduler().createTaskBuilder().delayTicks(1).execute((Runnable) entity::remove).submit(GWMCrates.getInstance());
            }
        }
    }

    private boolean isHologramExist(Entity entity) {
        for (Hologram hologram : Hologram.HOLOGRAMS) {
            if (hologram.getEntity().equals(entity)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEntityCaseExist(Entity entity) {
        for (Manager manager : GWMCrates.getInstance().getCreatedManagers()) {
            if (manager.getCase() instanceof EntityCase) {
                if (((EntityCase) manager.getCase()).getEntity().equals(entity)) {
                    return true;
                }
            }
        }
        return false;
    }
}
