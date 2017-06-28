package ua.gwm.sponge_plugin.crates.hologram;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import ua.gwm.sponge_plugin.crates.GWMCrates;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class Hologram {

    public static final Set<Hologram> HOLOGRAMS = new HashSet<Hologram>();

    private ArmorStand entity;

    private Hologram(ArmorStand entity) {
        this.entity = entity;
    }

    public static void deleteHolograms() {
        HOLOGRAMS.forEach(hologram -> hologram.entity.remove());
        HOLOGRAMS.clear();
    }

    public synchronized static Hologram createHologram(Location<World> location, Text name) {
        World world = location.getExtent();
        ArmorStand entity = (ArmorStand) world.createEntity(EntityTypes.ARMOR_STAND, location.getPosition());
        entity.offer(Keys.HAS_GRAVITY, false);
        entity.offer(Keys.DISPLAY_NAME, name);
        entity.offer(Keys.CUSTOM_NAME_VISIBLE, true);
        entity.offer(Keys.INVISIBLE, true);
        world.spawnEntity(entity, GWMCrates.getInstance().getDefaultCause());
        Hologram hologram = new Hologram(entity);
        HOLOGRAMS.add(hologram);
        return hologram;
    }

    public ArmorStand getEntity() {
        return entity;
    }
}
