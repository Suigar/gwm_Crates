package ua.gwm.sponge_plugin.crates.util;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import ua.gwm.sponge_plugin.crates.GWMCrates;

import java.lang.reflect.Method;
import java.util.Optional;

//API-7 compatibility (Yeah, shitcode again! :D)
public class UnsafeUtils {

    public static Cause createDefaultCause() {
        try {
            if (GWMCrates.getInstance().getApiVersion() >= 7) {
                Class cause_class = Class.forName("org.spongepowered.api.event.cause.Cause");
                Class event_context_class = Class.forName("org.spongepowered.api.event.cause.EventContext");
                Method method = cause_class.getMethod("of", event_context_class, Object.class);
                return (Cause) method.invoke(null, event_context_class.getMethod("empty").invoke(null), GWMCrates.getInstance().getPluginContainer());
            } else {
                return Cause.of(NamedCause.of("root", GWMCrates.getInstance().getPluginContainer()));
            }
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("[UNSAFE] Exception making Cause!", e);
            return null;
        }
    }

    public static Optional<Container> openInventory(Player player, Inventory inventory) {
        try {
            if (GWMCrates.getInstance().getApiVersion() >= 7) {
                Method method = player.getClass().getMethod("openInventory", Inventory.class);
                return (Optional<Container>) method.invoke(player, inventory);
            } else {
                return player.openInventory(inventory, GWMCrates.getInstance().getDefaultCause());
            }
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("[UNSAFE] Exception opening inventory!", e);
            return null;
        }
    }

    public static void closeInventory(Player player) {
        try {
            if (GWMCrates.getInstance().getApiVersion() >= 7) {
                Method method = player.getClass().getMethod("closeInventory");
                method.invoke(player);
            } else {
                player.closeInventory(GWMCrates.getInstance().getDefaultCause());
            }
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("[UNSAFE] Exception closing inventory!", e);
        }
    }

    public static void setBlock(Location location, BlockState state, BlockChangeFlag flag) {
        try {
            if (GWMCrates.getInstance().getApiVersion() >= 7) {
                Method method = location.getClass().getMethod("setBlock", BlockState.class, BlockChangeFlag.class);
                method.invoke(location, state, flag);
            } else {
                location.setBlock(state, flag, GWMCrates.getInstance().getDefaultCause());
            }
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("[UNSAFE] Exception closing inventory!", e);
        }
    }

    public static void setBlock(Location location, BlockState state) {
        try {
            if (GWMCrates.getInstance().getApiVersion() >= 7) {
                Method method = location.getClass().getMethod("setBlock", BlockState.class);
                method.invoke(location, state);
            } else {
                location.setBlock(state, GWMCrates.getInstance().getDefaultCause());
            }
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("[UNSAFE] Exception closing inventory!", e);
        }
    }

    public static void setBlockType(Location location, BlockType type, BlockChangeFlag flag) {
        try {
            if (GWMCrates.getInstance().getApiVersion() >= 7) {
                Method method = location.getClass().getMethod("setBlockType", BlockType.class, BlockChangeFlag.class);
                method.invoke(location, type, flag);
            } else {
                location.setBlockType(type, flag, GWMCrates.getInstance().getDefaultCause());
            }
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("[UNSAFE] Exception closing inventory!", e);
        }
    }

    public static void setBlockType(Location location, BlockType type) {
        try {
            if (GWMCrates.getInstance().getApiVersion() >= 7) {
                Method method = location.getClass().getMethod("setBlockType", BlockType.class);
                method.invoke(location, type);
            } else {
                location.setBlockType(type, GWMCrates.getInstance().getDefaultCause());
            }
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("[UNSAFE] Exception closing inventory!", e);
        }
    }

    public static void spawnEntity(World world, Entity entity) {
        try {
            if (GWMCrates.getInstance().getApiVersion() >= 7) {
                Method method = world.getClass().getMethod("spawnEntity", Entity.class);
                method.invoke(world, entity);
            } else {
                world.spawnEntity(entity, GWMCrates.getInstance().getDefaultCause());
            }
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("[UNSAFE] Exception spawning entity!", e);
        }
    }
}
