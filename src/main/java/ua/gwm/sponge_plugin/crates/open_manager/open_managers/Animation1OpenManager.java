package ua.gwm.sponge_plugin.crates.open_manager.open_managers;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import de.randombyte.holograms.api.HologramsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.event.PlayerOpenCrateEvent;
import ua.gwm.sponge_plugin.crates.listener.Animation1Listener;
import ua.gwm.sponge_plugin.crates.manager.Manager;
import ua.gwm.sponge_plugin.crates.open_manager.OpenManager;
import ua.gwm.sponge_plugin.crates.util.SuperObjectType;
import ua.gwm.sponge_plugin.crates.util.UnsafeUtils;
import ua.gwm.sponge_plugin.crates.util.Utils;

import java.util.*;

public class Animation1OpenManager extends OpenManager {

    public static HashMap<Player, Information> PLAYERS_OPENING_ANIMATION1 = new HashMap<Player, Information>();

    private BlockType floor_block_type;
    private BlockType fence_block_type;
    private BlockType crate_block_type;
    private OpenManager open_manager = new NoGuiOpenManager(Optional.empty(), Optional.empty());
    private Optional<Text> hologram = Optional.empty();
    private long close_delay = 0;

    public Animation1OpenManager(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode floor_block_type_node = node.getNode("FLOOR_BLOCK_TYPE");
            ConfigurationNode fence_block_type_node = node.getNode("FENCE_BLOCK_TYPE");
            ConfigurationNode crate_block_type_node = node.getNode("CRATE_BLOCK_TYPE");
            ConfigurationNode hologram_node = node.getNode("HOLOGRAM");
            ConfigurationNode close_delay_node = node.getNode("CLOSE_DELAY");
            ConfigurationNode open_manager_node = node.getNode("OPEN_MANAGER");
            if (floor_block_type_node.isVirtual()) {
                throw new RuntimeException("FLOOR_BLOCK_TYPE node does not exist!");
            }
            if (fence_block_type_node.isVirtual()) {
                throw new RuntimeException("FENCE_BLOCK_TYPE node does not exist!");
            }
            if (crate_block_type_node.isVirtual()) {
                throw new RuntimeException("CRATE_BLOCK_TYPE node does not exist!");
            }
            floor_block_type = floor_block_type_node.getValue(TypeToken.of(BlockType.class));
            fence_block_type = fence_block_type_node.getValue(TypeToken.of(BlockType.class));
            crate_block_type = crate_block_type_node.getValue(TypeToken.of(BlockType.class));
            if (!hologram_node.isVirtual()) {
                hologram = Optional.of(TextSerializers.FORMATTING_CODE.deserialize(hologram_node.getString()));
            }
            if (!close_delay_node.isVirtual()) {
                close_delay = close_delay_node.getLong();
            }
            if (!open_manager_node.isVirtual()) {
                open_manager = (OpenManager) Utils.createSuperObject(node, SuperObjectType.OPEN_MANAGER);
            }
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().info("Exception creating Animation1 Open Manager!");
        }
    }

    public Animation1OpenManager(Optional<String> id, Optional<SoundType> open_sound, BlockType floor_block_type,
                                 BlockType fence_block_type, BlockType crate_block_type, OpenManager open_manager,
                                 Optional<Text> hologram, int close_delay) {
        super("ANIMATION1", id, open_sound);
        this.floor_block_type = floor_block_type;
        this.fence_block_type = fence_block_type;
        this.crate_block_type = crate_block_type;
        this.open_manager = open_manager;
        this.hologram = hologram;
        this.close_delay = close_delay;
    }

    @Override
    public void open(Player player, Manager manager) {
        PlayerOpenCrateEvent open_event = new PlayerOpenCrateEvent(player, manager);
        Sponge.getEventManager().post(open_event);
        if (open_event.isCancelled()) return;
        Location<World> location = player.getLocation();
        Vector3i position = player.getLocation().getBlockPosition();
        World world = location.getExtent();
        int loc_x = position.getX();
        int loc_y = position.getY();
        int loc_z = position.getZ();
        HashMap<Location<World>, BlockState> original_block_states = new HashMap<Location<World>, BlockState>();
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 3; y++) {
                for (int z = -2; z <= 2; z++) {
                    Location loc = new Location<World>(
                            world, loc_x + x, loc_y + y, loc_z + z);
                    BlockState loc_state = loc.getBlock();
                    original_block_states.put(loc, loc_state);
                    UnsafeUtils.setBlockType(location, BlockTypes.AIR, BlockChangeFlag.NONE);
                }
            }
        }
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                UnsafeUtils.setBlockType(new Location<World>(world, loc_x + x, loc_y - 1, loc_z + z), floor_block_type, BlockChangeFlag.NONE);
                if (z == 2 || z == -2 || x == 2 || x == -2) {
                    UnsafeUtils.setBlockType(new Location<World>(world, loc_x + x, loc_y , loc_z + z), fence_block_type, BlockChangeFlag.NONE);
                }
            }
        }
        HashSet<HologramsService.Hologram> holograms = new HashSet<HologramsService.Hologram>();
        Location<World> loc1 = new Location<World>(world, loc_x + 2, loc_y, loc_z);
        UnsafeUtils.setBlock(loc1, BlockState.builder().
                        blockType(crate_block_type).
                        add(Keys.DIRECTION, Direction.WEST).
                        build(),
                BlockChangeFlag.NONE);
        Location<World> loc2 = new Location<World>(world, loc_x - 2, loc_y, loc_z);
        UnsafeUtils.setBlock(loc2, BlockState.builder().
                        blockType(crate_block_type).
                        add(Keys.DIRECTION, Direction.EAST).
                        build(),
                BlockChangeFlag.NONE);
        Location<World> loc3 = new Location<World>(world, loc_x, loc_y, loc_z + 2);
        UnsafeUtils.setBlock(loc3, BlockState.builder().
                        blockType(crate_block_type).
                        add(Keys.DIRECTION, Direction.NORTH).
                        build(),
                BlockChangeFlag.NONE);
        Location<World> loc4 = new Location<World>(world, loc_x, loc_y, loc_z - 2);
        UnsafeUtils.setBlock(loc4, BlockState.builder().
                        blockType(crate_block_type).
                        add(Keys.DIRECTION, Direction.SOUTH).
                        build(),
                BlockChangeFlag.NONE);
        Utils.tryCreateHologram(loc1, hologram).ifPresent(holograms::add);
        Utils.tryCreateHologram(loc2, hologram).ifPresent(holograms::add);
        Utils.tryCreateHologram(loc3, hologram).ifPresent(holograms::add);
        Utils.tryCreateHologram(loc4, hologram).ifPresent(holograms::add);
        getOpenSound().ifPresent(sound -> player.playSound(sound, player.getLocation().getPosition(), 1.));
        PLAYERS_OPENING_ANIMATION1.put(player, new Information(this, manager,
                new HashMap<Location<World>, Boolean>(){{
                    put(loc1, false);
                    put(loc2, false);
                    put(loc3, false);
                    put(loc4, false);
                }}, original_block_states, holograms));
    }

    @Override
    public boolean canOpen(Player player, Manager manager) {
        return super.canOpen(player, manager) &&
                !PLAYERS_OPENING_ANIMATION1.containsKey(player) &&
                !Animation1Listener.OPENED_PLAYERS.containsKey(player) &&
                !containsNearPlayers(player);
    }

    private boolean containsNearPlayers(Player player) {
        for (Player p : PLAYERS_OPENING_ANIMATION1.keySet()) {
            if (p.getLocation().getPosition().distance(player.getLocation().getPosition()) < 5) {
                return true;
            }
        }
        for (Player p : Animation1Listener.OPENED_PLAYERS.keySet()) {
            if (p.getLocation().getPosition().distance(player.getLocation().getPosition()) < 5) {
                return true;
            }
        }
        return false;
    }

    public BlockType getFloorBlockType() {
        return floor_block_type;
    }

    public void setFloorBlockType(BlockType floor_block_type) {
        this.floor_block_type = floor_block_type;
    }

    public BlockType getFenceBlockType() {
        return fence_block_type;
    }

    public void setFenceBlockType(BlockType fence_block_type) {
        this.fence_block_type = fence_block_type;
    }

    public BlockType getCrateBlockType() {
        return crate_block_type;
    }

    public void setCrateBlockType(BlockType crate_block_type) {
        this.crate_block_type = crate_block_type;
    }

    public Optional<Text> getHologram() {
        return hologram;
    }

    public void setHologram(Optional<Text> hologram) {
        this.hologram = hologram;
    }

    public OpenManager getOpenManager() {
        return open_manager;
    }

    public void setOpenManager(OpenManager open_manager) {
        this.open_manager = open_manager;
    }

    public long getCloseDelay() {
        return close_delay;
    }

    public void setCloseDelay(long close_delay) {
        this.close_delay = close_delay;
    }

    public static class Information {

        private Animation1OpenManager open_manager;
        private Manager manager;
        private Map<Location<World>, Boolean> locations;
        private Map<Location<World>, BlockState> original_block_states;
        private Set<HologramsService.Hologram> holograms;

        public Information(Animation1OpenManager open_manager, Manager manager,
                           Map<Location<World>, Boolean> locations, Map<Location<World>, BlockState> original_block_states,
                           Set<HologramsService.Hologram> holograms) {
            this.open_manager = open_manager;
            this.manager = manager;
            this.locations = locations;
            this.original_block_states = original_block_states;
            this.holograms = holograms;
        }

        public Animation1OpenManager getOpenManager() {
            return open_manager;
        }

        public void setOpenManager(Animation1OpenManager open_manager) {
            this.open_manager = open_manager;
        }

        public Manager getManager() {
            return manager;
        }

        public void setManager(Manager manager) {
            this.manager = manager;
        }

        public Map<Location<World>, Boolean> getLocations() {
            return locations;
        }

        public void setLocations(Map<Location<World>, Boolean> locations) {
            this.locations = locations;
        }

        public Map<Location<World>, BlockState> getOriginalBlockStates() {
            return original_block_states;
        }

        public void setOriginalBlockStates(Map<Location<World>, BlockState> original_block_states) {
            this.original_block_states = original_block_states;
        }

        public Set<HologramsService.Hologram> getHolograms() {
            return holograms;
        }

        public void setHolograms(Set<HologramsService.Hologram> holograms) {
            this.holograms = holograms;
        }
    }
}
