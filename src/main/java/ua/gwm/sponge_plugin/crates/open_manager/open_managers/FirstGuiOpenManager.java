package ua.gwm.sponge_plugin.crates.open_manager.open_managers;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.event.PlayerOpenCrateEvent;
import ua.gwm.sponge_plugin.crates.event.PlayerOpenedCrateEvent;
import ua.gwm.sponge_plugin.crates.manager.Manager;
import ua.gwm.sponge_plugin.crates.open_manager.OpenManager;
import ua.gwm.sponge_plugin.crates.util.GWMCratesUtils;
import ua.gwm.sponge_plugin.crates.util.LanguageUtils;
import ua.gwm.sponge_plugin.crates.util.Pair;

import java.util.*;

public class FirstGuiOpenManager extends OpenManager {

    public static final HashMap<Container, Pair<FirstGuiOpenManager, Manager>> FIRST_GUI_CONTAINERS = new HashMap<Container, Pair<FirstGuiOpenManager, Manager>>();
    public static final HashSet<Container> SHOWN_GUI = new HashSet<Container>();

    private Optional<Text> display_name = Optional.empty();
    private List<ItemStack> decorative_items;
    private List<Integer> scroll_delays;
    private boolean clear_decorative_items;
    private boolean clear_other_drops;
    private int close_delay;
    private boolean forbid_close;
    private Optional<SoundType> scroll_sound = Optional.empty();
    private Optional<SoundType> win_sound = Optional.empty();

    public FirstGuiOpenManager(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode display_name_node = node.getNode("DISPLAY_NAME");
            ConfigurationNode decorative_items_node = node.getNode("DECORATIVE_ITEMS");
            ConfigurationNode scroll_delays_node = node.getNode("SCROLL_DELAYS");
            ConfigurationNode clear_decorative_items_node = node.getNode("CLEAR_DECORATIVE_ITEMS");
            ConfigurationNode clear_other_drops_node = node.getNode("CLEAR_OTHER_DROPS");
            ConfigurationNode close_delay_node = node.getNode("CLOSE_DELAY");
            ConfigurationNode forbid_close_node = node.getNode("FORBID_CLOSE");
            ConfigurationNode scroll_sound_node = node.getNode("SCROLL_SOUND");
            ConfigurationNode win_sound_node = node.getNode("WIN_SOUND");
            if (!display_name_node.isVirtual()) {
                display_name = Optional.of(TextSerializers.FORMATTING_CODE.deserialize(display_name_node.getString()));
            }
            if (decorative_items_node.isVirtual()) {
                throw new RuntimeException("DECORATIVE_ITEMS node does not exist!");
            }
            decorative_items = new ArrayList<ItemStack>();
            for (ConfigurationNode decorative_item_node : decorative_items_node.getChildrenList()) {
                decorative_items.add(GWMCratesUtils.parseItem(decorative_item_node));
            }
            if (decorative_items.size() != 20) {
                throw new RuntimeException("DECORATIVE_ITEMS size must be 20 instead of " + decorative_items.size() + "!");
            }
            if (scroll_delays_node.isVirtual()) {
                throw new RuntimeException("SCROLL_DELAYS node does not exist!");
            }
            scroll_delays = scroll_delays_node.getList(TypeToken.of(Integer.class));
            clear_decorative_items = clear_decorative_items_node.getBoolean(false);
            clear_other_drops = clear_other_drops_node.getBoolean(true);
            if (close_delay_node.isVirtual()) {
                throw new RuntimeException("CLOSE_DELAY node does not exist!");
            }
            close_delay = close_delay_node.getInt();
            forbid_close = forbid_close_node.getBoolean(true);
            if (!scroll_sound_node.isVirtual()) {
                scroll_sound = Optional.of(scroll_sound_node.getValue(TypeToken.of(SoundType.class)));
            }
            if (!win_sound_node.isVirtual()) {
                win_sound = Optional.of(win_sound_node.getValue(TypeToken.of(SoundType.class)));
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception creating First Gui Open Manager!", e);
        }
    }

    public FirstGuiOpenManager(Optional<SoundType> open_sound, Optional<Text> display_name,
                               List<ItemStack> decorative_items, List<Integer> scroll_delays,
                               boolean clear_decorative_items, boolean clear_other_drops,
                               int close_delay, Optional<SoundType> scroll_sound,
                               Optional<SoundType> win_sound) {
        super(open_sound);
        this.display_name = display_name;
        if (decorative_items.size() != 20) {
            throw new RuntimeException("DECORATIVE_ITEMS size must be 20 instead of " + decorative_items.size() + "!");
        }
        this.decorative_items = decorative_items;
        this.scroll_delays = scroll_delays;
        this.clear_decorative_items = clear_decorative_items;
        this.clear_other_drops = clear_other_drops;
        this.close_delay = close_delay;
        this.scroll_sound = scroll_sound;
        this.win_sound = win_sound;
    }

    @Override
    public void open(Player player, Manager manager) {
        PlayerOpenCrateEvent open_event = new PlayerOpenCrateEvent(player, manager);
        Sponge.getEventManager().post(open_event);
        if (open_event.isCancelled()) return;
        Inventory inventory = display_name.map(text -> Inventory.builder().of(InventoryArchetypes.CHEST).
                property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(text)).
                build(GWMCrates.getInstance())).orElseGet(() -> Inventory.builder().of(InventoryArchetypes.CHEST).
                build(GWMCrates.getInstance()));
        ArrayList<Drop> drop_list = new ArrayList<Drop>();
        OrderedInventory ordered = inventory.query(OrderedInventory.class);
        for (int i = 0; i < 10; i++) {
            ordered.getSlot(new SlotIndex(i)).get().set(decorative_items.get(i));
        }
        for (int i = 10; i < 17; i++) {
            Drop new_drop = manager.getRandomDrop();
            drop_list.add(new_drop);
            ordered.getSlot(new SlotIndex(i)).get().set(new_drop.getDropItem().orElse(ItemStack.of(ItemTypes.NONE, 1)));
        }
        for (int i = 17; i < 27; i++) {
            ordered.getSlot(new SlotIndex(i)).get().set(decorative_items.get(i - 7));
        }
        Container container = player.openInventory(inventory, GWMCrates.getInstance().getDefaultCause()).get();
        getOpenSound().ifPresent(open_sound -> player.playSound(open_sound, player.getLocation().getPosition(), 1.));
        FIRST_GUI_CONTAINERS.put(container, new Pair<FirstGuiOpenManager, Manager>(this, manager));
        int wait_time = 0;
        for (int i = 0; i < scroll_delays.size() - 1; i++) {
            wait_time += scroll_delays.get(i);
            Sponge.getScheduler().createTaskBuilder().delayTicks(wait_time).execute(() -> {
                for (int j = 10; j < 16; j++) {
                    ordered.getSlot(new SlotIndex(j)).get().set(inventory.query(new SlotIndex(j + 1)).peek().orElse(ItemStack.of(ItemTypes.NONE, 1)));
                }
                Drop new_drop = manager.getRandomDrop();
                drop_list.add(new_drop);
                ordered.getSlot(new SlotIndex(16)).get().set(new_drop.getDropItem().orElse(ItemStack.of(ItemTypes.NONE, 1)));
                scroll_sound.ifPresent(sound -> player.playSound(sound, player.getLocation().getPosition(), 1.));
            }).submit(GWMCrates.getInstance());
        }
        Sponge.getScheduler().createTaskBuilder().delayTicks(wait_time + scroll_delays.get(scroll_delays.size() - 1)).execute(() -> {
            Drop drop = drop_list.get(drop_list.size() - 4);
            drop.apply(player);
            win_sound.ifPresent(sound -> player.playSound(sound, player.getLocation().getPosition(), 1.));
            if (clear_decorative_items) {
                for (int i = 0; i < 10; i++) {
                    ordered.getSlot(new SlotIndex(i)).get().set(ItemStack.of(ItemTypes.NONE, 1));
                }
                for (int i = 17; i < 17; i++) {
                    ordered.getSlot(new SlotIndex(i)).get().set(ItemStack.of(ItemTypes.NONE, 1));
                }
            }
            if (clear_other_drops) {
                for (int i = 10; i < 13; i++) {
                    ordered.getSlot(new SlotIndex(i)).get().set(ItemStack.of(ItemTypes.NONE, 1));
                }
                for (int i = 14; i < 17; i++) {
                    ordered.getSlot(new SlotIndex(i)).get().set(ItemStack.of(ItemTypes.NONE, 1));
                }
            }
            SHOWN_GUI.add(container);
            PlayerOpenedCrateEvent opened_event = new PlayerOpenedCrateEvent(player, manager,
                    LanguageUtils.getText("SUCCESSFULLY_OPENED_MANAGER",
                            new Pair<String, String>("%MANAGER%", manager.getName())));
            Sponge.getEventManager().post(opened_event);
            player.sendMessage(opened_event.getMessage());
        }).submit(GWMCrates.getInstance());
        Sponge.getScheduler().createTaskBuilder().delayTicks(wait_time + scroll_delays.get(scroll_delays.size() - 1) + close_delay).execute(() -> {
            Optional<Container> optional_open_inventory = player.getOpenInventory();
            if (optional_open_inventory.isPresent() && container.equals(optional_open_inventory.get())) {
                player.closeInventory(GWMCrates.getInstance().getDefaultCause());
            }
            SHOWN_GUI.remove(container);
            FIRST_GUI_CONTAINERS.remove(container);
        }).submit(GWMCrates.getInstance());
    }

    @Override
    public boolean canOpen(Player player, Manager manager) {
        return true;
    }

    public List<ItemStack> getDecorativeItems() {
        return decorative_items;
    }

    public void setDecorativeItems(List<ItemStack> decorative_items) {
        this.decorative_items = decorative_items;
    }

    public List<Integer> getScrollDelays() {
        return scroll_delays;
    }

    public void setScrollDelays(List<Integer> scroll_delays) {
        this.scroll_delays = scroll_delays;
    }

    public boolean isClearDecorativeItems() {
        return clear_decorative_items;
    }

    public void setClearDecorativeItems(boolean clear_decorative_items) {
        this.clear_decorative_items = clear_decorative_items;
    }

    public boolean isClearOtherDrops() {
        return clear_other_drops;
    }

    public void setClearOtherDrops(boolean clear_other_drops) {
        this.clear_other_drops = clear_other_drops;
    }

    public int getCloseCooldown() {
        return close_delay;
    }

    public void setCloseCooldown(int close_cooldown) {
        this.close_delay = close_cooldown;
    }

    public boolean isForbidClose() {
        return forbid_close;
    }

    public void setForbidClose(boolean forbid_close) {
        this.forbid_close = forbid_close;
    }
}
