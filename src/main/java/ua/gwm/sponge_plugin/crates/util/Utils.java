package ua.gwm.sponge_plugin.crates.util;

import com.google.common.reflect.TypeToken;
import de.randombyte.holograms.api.HologramsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.drop.drops.CommandsDrop;
import ua.gwm.sponge_plugin.crates.gui.GWMCratesGUI;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.ConfigurationDialog;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.SavedSuperObjectConfigurationDialog;
import ua.gwm.sponge_plugin.crates.manager.Manager;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static boolean itemStacksEquals(ItemStack item, ItemStack other) {
        ItemStack copy1 = item.copy();
        ItemStack copy2 = other.copy();
        copy1.setQuantity(1);
        copy2.setQuantity(1);
        return copy1.equalTo(copy2);
    }

    public static int getRandomIntLevel(int min, int max) {
        Random random = new Random();
        while (random.nextBoolean() && min < max) {
            min++;
        }
        return min;
    }

    public static Location<World> parseLocation(ConfigurationNode node) {
        ConfigurationNode x_node = node.getNode("X");
        ConfigurationNode y_node = node.getNode("Y");
        ConfigurationNode z_node = node.getNode("Z");
        ConfigurationNode world_node = node.getNode("WORLD_NAME");
        if (x_node.isVirtual()) {
            throw new RuntimeException("X node does not exist!");
        }
        if (y_node.isVirtual()) {
            throw new RuntimeException("Y node does not exist!");
        }
        if (z_node.isVirtual()) {
            throw new RuntimeException("Z node does not exist!");
        }
        if (world_node.isVirtual()) {
            throw new RuntimeException("WORLD_NAME node does not exist!");
        }
        double x = x_node.getDouble();
        double y = y_node.getDouble();
        double z = z_node.getDouble();
        String world_name = world_node.getString();
        Optional<World> optional_world = Sponge.getServer().getWorld(world_name);
        if (!optional_world.isPresent()) {
            throw new RuntimeException("World \"" + world_name + "\" does not exist!");
        }
        World world = optional_world.get();
        return new Location<World>(world, x, y, z);
    }

    public static ItemStack parseItem(ConfigurationNode node) {
        try {
            ConfigurationNode item_type_node = node.getNode("ITEM_TYPE");
            ConfigurationNode quantity_node = node.getNode("QUANTITY");
            ConfigurationNode sub_id_node = node.getNode("SUB_ID");
            ConfigurationNode nbt_node = node.getNode("NBT");
            ConfigurationNode durability_node = node.getNode("DURABILITY");
            ConfigurationNode display_name_node = node.getNode("DISPLAY_NAME");
            ConfigurationNode lore_node = node.getNode("LORE");
            ConfigurationNode enchantments_node = node.getNode("ENCHANTMENTS");
            ConfigurationNode hide_enchantments_node = node.getNode("HIDE_ENCHANTMENTS");
            if (item_type_node.isVirtual()) {
                throw new RuntimeException("ITEM_TYPE node does not exist!");
            }
            //Mega-shit-code start
            ConfigurationNode temp_node = node.getNode("TEMP_SPONGE_ITEM_STACK_NODE");
            temp_node.getNode("ItemType").setValue(item_type_node.getString());
            temp_node.getNode("UnsafeDamage").setValue(sub_id_node.getInt(0));
            temp_node.getNode("Count").setValue(quantity_node.getInt(1));
            ItemStack item = temp_node.getValue(TypeToken.of(ItemStack.class));
            temp_node.setValue(null);
            //Mega-shit-code end; Another not good code start
            if (!nbt_node.isVirtual()) {
                LinkedHashMap nbt_map = (LinkedHashMap) nbt_node.getValue();
                if (item.toContainer().get(DataQuery.of("UnsafeData")).isPresent()) {
                    Map unsafe_data_map = item.toContainer().getMap(DataQuery.of("UnsafeData")).get();
                    nbt_map.putAll(unsafe_data_map);
                }
                DataContainer container = item.toContainer().set(DataQuery.of("UnsafeData"), nbt_map);
                item = ItemStack.builder().fromContainer(container).build();
            }
            //Another not good code end
            if (!durability_node.isVirtual()) {
                int durability = durability_node.getInt();
                item.offer(Keys.ITEM_DURABILITY, durability);
            }
            if (!display_name_node.isVirtual()) {
                Text display_name = TextSerializers.FORMATTING_CODE.deserialize(display_name_node.getString());
                item.offer(Keys.DISPLAY_NAME, display_name);
            }
            if (!lore_node.isVirtual()) {
                List<Text> lore = lore_node.getList(TypeToken.of(String.class)).stream().
                        map(TextSerializers.FORMATTING_CODE::deserialize).
                        collect(Collectors.toList());
                item.offer(Keys.ITEM_LORE, lore);
            }
            if (!enchantments_node.isVirtual()) {
                List<ItemEnchantment> item_enchantments = new ArrayList<ItemEnchantment>();
                for (ConfigurationNode enchantment_node : enchantments_node.getChildrenList()) {
                    item_enchantments.add(parseEnchantments(enchantment_node));
                }
                item.offer(Keys.ITEM_ENCHANTMENTS, item_enchantments);
            }
            if (!hide_enchantments_node.isVirtual()) {
                item.offer(Keys.HIDE_ENCHANTMENTS, hide_enchantments_node.getBoolean());
            }
            return item;
        } catch (Exception e) {
            throw new RuntimeException("Exception parsing item!", e);
        }
    }

    public static ItemEnchantment parseEnchantments(ConfigurationNode node) {
        ConfigurationNode enchantment_node = node.getNode("ENCHANTMENT");
        ConfigurationNode level_node = node.getNode("LEVEL");
        if (enchantment_node.isVirtual()) {
            throw new RuntimeException("ENCHANTMENT node does not exist!");
        }
        try {
            Enchantment enchantment = enchantment_node.getValue(TypeToken.of(Enchantment.class));
            int level = level_node.getInt(1);
            return new ItemEnchantment(enchantment, level);
        } catch (Exception e) {
            throw new RuntimeException("Exception parsing enchantment!", e);
        }
    }

    public static CommandsDrop.Command parseCommand(ConfigurationNode node) {
        ConfigurationNode cmd_node = node.getNode("CMD");
        ConfigurationNode console_node = node.getNode("CONSOLE");
        if (cmd_node.isVirtual()) {
            throw new RuntimeException("CMD node does not exist!");
        }
        String cmd = cmd_node.getString();
        boolean console = console_node.getBoolean(true);
        return new CommandsDrop.Command(cmd, console);
    }

    public static boolean isFirstInventory(Container container, Slot slot) {
        int upperSize = container.iterator().next().capacity();
        Integer affectedSlot = slot.getProperty(SlotIndex.class, "slotindex").map(SlotIndex::getValue).orElse(-1);
        return affectedSlot != -1 && affectedSlot < upperSize;
    }

    public static int getInventoryHeight(int size) {
        int height = (int) Math.ceil((size)/9.);
        if (height < 1) {
            return 1;
        }
        if (height > 6) {
            return 6;
        }
        return height;
    }

    public static boolean isLocationChanged(Transform<World> from, Transform<World> to, boolean y_sensitive) {
        Location<World> location_from = from.getLocation();
        Location<World> location_to = to.getLocation();
        int x_from = location_from.getBlockX();
        int y_from = location_from.getBlockY();
        int z_from = location_from.getBlockZ();
        int x_to = location_to.getBlockX();
        int y_to = location_to.getBlockY();
        int z_to = location_to.getBlockZ();
        return x_from != x_to || z_from != z_to || (y_sensitive && y_from != y_to);
    }

    public static Drop chooseDropByLevel(Iterable<Drop> drops, Player player, boolean fake) {
        Map<Integer, List<Drop>> sorted_drops = new HashMap<Integer, List<Drop>>();
        for (Drop drop : drops) {
            boolean found_by_permission = false;
            for (Map.Entry<String, Integer> entry : fake ?
                    drop.getPermissionFakeLevels().entrySet() : drop.getPermissionLevels().entrySet()) {
                String permission = entry.getKey();
                int permission_level = entry.getValue();
                if (player.hasPermission(permission)) {
                    if (sorted_drops.containsKey(permission_level)) {
                        sorted_drops.get(permission_level).add(drop);
                        found_by_permission = true;
                        break;
                    } else {
                        List<Drop> list = new ArrayList<Drop>();
                        list.add(drop);
                        sorted_drops.put(permission_level, list);
                        found_by_permission = true;
                        break;
                    }
                }
            }
            if (!found_by_permission) {
                int level = fake ? drop.getFakeLevel().orElse(drop.getLevel()) : drop.getLevel();
                if (sorted_drops.containsKey(level)) {
                    sorted_drops.get(level).add(drop);
                } else {
                    List<Drop> list = new ArrayList<Drop>();
                    list.add(drop);
                    sorted_drops.put(level, list);
                }
            }
        }
        int max_level = 1;
        for (int level : sorted_drops.keySet()) {
            if (level > max_level) {
                max_level = level;
            }
        }
        while (true) {
            int level = Utils.getRandomIntLevel(1, max_level);
            if (sorted_drops.containsKey(level)) {
                List<Drop> actual_drops = sorted_drops.get(level);
                return actual_drops.get(new Random().nextInt(actual_drops.size()));
            }
        }
    }

    public static void addItemStack(Player player, ItemStack item, int amount) {
        if (amount > 0) {
            ItemStack copy = item.copy();
            copy.setQuantity(amount);
            player.getInventory().offer(copy);
            /*int max_stack_quantity = item.getMaxStackQuantity();
            Inventory inventory = player.getInventory();
            Iterator<Slot> slot_iterator = inventory.<Slot>slots().iterator();
            while (slot_iterator.hasNext() && amount > 0) {
                Slot slot = slot_iterator.next();
                Optional<ItemStack> optional_inventory_item = slot.peek();
                if (optional_inventory_item.isPresent()) {
                    ItemStack inventory_item = optional_inventory_item.get();
                    if (itemStacksEquals(inventory_item, item)) {
                        int inventory_item_quantity = inventory_item.getQuantity();
                        if (inventory_item_quantity < max_stack_quantity) {
                            int difference = max_stack_quantity - inventory_item_quantity;
                            if (amount >= difference) {
                                inventory_item.setQuantity(max_stack_quantity);
                                slot.offer(inventory_item);
                                amount -= difference;
                            } else {
                                inventory_item.setQuantity(inventory_item_quantity + amount);
                                slot.offer(inventory_item);
                                amount = 0;
                            }
                        }
                    }
                } else {
                    if (amount >= max_stack_quantity) {
                        ItemStack copy = item.copy();
                        copy.setQuantity(max_stack_quantity);
                        slot.offer(copy);
                        amount -= max_stack_quantity;
                    } else {
                        ItemStack copy = item.copy();
                        copy.setQuantity(amount);
                        slot.offer(copy);
                        amount = 0;
                    }
                }
            }
            if (amount > 0) {
                ItemStack copy = item.copy();
                copy.setQuantity(amount);
                Location<World> player_location = player.getLocation();
                World world = player_location.getExtent();
                Entity entity = world.createEntity(EntityTypes.ITEM, player_location.getPosition());
                UnsafeUtils.spawnEntity(world, entity);
                entity.offer(Keys.REPRESENTED_ITEM, copy.createSnapshot());
            }*/
        } else if (amount < 0) {
            amount = -amount;
            Inventory inventory = player.getInventory();
            Iterator<Slot> slot_iterator = inventory.<Slot>slots().iterator();
            while (slot_iterator.hasNext() && amount > 0) {
                Slot slot = slot_iterator.next();
                Optional<ItemStack> optional_inventory_item = slot.peek();
                if (optional_inventory_item.isPresent()) {
                    ItemStack inventory_item = optional_inventory_item.get();
                    if (Utils.itemStacksEquals(inventory_item, item)) {
                        int item_quantity = inventory_item.getQuantity();
                        if (item_quantity > amount) {
                            item.setQuantity(item_quantity - amount);
                            slot.set(item);
                            amount = 0;
                        } else {
                            slot.set(ItemStack.of(ItemTypes.NONE, 1));
                            amount -= item_quantity;
                        }
                    }
                }
            }
        }
    }

    public static int getItemStackAmount(Player player, ItemStack item) {
        int amount = 0;
        Inventory inventory = player.getInventory();
        for (Slot slot : inventory.<Slot>slots()) {
            Optional<ItemStack> optional_inventory_item = slot.peek();
            if (optional_inventory_item.isPresent()) {
                ItemStack inventory_item = optional_inventory_item.get();
                if (Utils.itemStacksEquals(inventory_item, item)) {
                    amount += item.getQuantity();
                }
            }
        }
        return amount;
    }

    public static Optional<HologramsService.Hologram> tryCreateHologram(Location<World> location, Optional<Text> optional_text) {
        if (!optional_text.isPresent()) {
            return Optional.empty();
        }
        Text text = optional_text.get();
        Optional<HologramsService> optional_hologram_service = GWMCrates.getInstance().getHologramsService();
        if (!optional_hologram_service.isPresent()) {
            GWMCrates.getInstance().getLogger().warn("Unable to create hologram, Holograms Service not found!");
            return Optional.empty();
        }
        HologramsService holograms_service = optional_hologram_service.get();
        location.getExtent().loadChunk(location.getChunkPosition(), true);
        Optional<HologramsService.Hologram> optional_hologram = holograms_service.
                createHologram(location.add(GWMCrates.getInstance().getHologramOffset()), text);
        if (!optional_hologram.isPresent()) {
            GWMCrates.getInstance().getLogger().warn("Holograms Service found, but hologram can not be created! :-(");
            return Optional.empty();
        }
        return optional_hologram;
    }

    public static SuperObject createSuperObject(ConfigurationNode node, SuperObjectType super_object_type) {
        ConfigurationNode type_node = node.getNode("TYPE");
        ConfigurationNode id_node = node.getNode("ID");
        if (type_node.isVirtual()) {
            throw new RuntimeException("TYPE node does not exist!");
        }
        String type = type_node.getString();
        String id = id_node.isVirtual() ? "Unknown ID" : id_node.getString().toLowerCase().replace(' ', '_');
        if (type.equals("SAVED")) {
            ConfigurationNode saved_id_node = node.getNode("SAVED_ID");
            if (saved_id_node.isVirtual()) {
                throw new RuntimeException("SAVED_ID node does not exist for Super Object \"" + super_object_type + "\" with type \"" + type + "\" and ID \"" + id + "\"!");
            }
            String saved_id = saved_id_node.getString();
            Optional<SuperObject> saved_super_object = getSavedSuperObject(super_object_type, saved_id);
            if (!saved_super_object.isPresent()) {
                throw new RuntimeException("Saved Super Object \"" + super_object_type + "\" with ID \"" + saved_id + "\" does not found!");
            }
            return saved_super_object.get();
        }
        Optional<SuperObjectStorage> optional_super_object_storage = getSuperObjectStorage(super_object_type, type);
        if (!optional_super_object_storage.isPresent()) {
            throw new RuntimeException("Type \"" + type + "\" for Super Object \"" + super_object_type + "\" does not found!");
        }
        SuperObjectStorage super_object_storage = optional_super_object_storage.get();
        try {
            Class<? extends SuperObject> super_object_class = super_object_storage.getSuperObjectClass();
            Constructor<? extends SuperObject> super_object_constructor = super_object_class.getConstructor(ConfigurationNode.class);
            SuperObject super_object = super_object_constructor.newInstance(node);
            return super_object;
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Super Object \"" + super_object_type + "\" with type \"" + type + "\" and ID \"" + id + "\"!", e);
        }
    }

    public static Optional<Manager> getManager(String manager_id) {
        for (Manager manager : GWMCrates.getInstance().getCreatedManagers()) {
            if (manager.getId().equalsIgnoreCase(manager_id)) {
                return Optional.of(manager);
            }
        }
        return Optional.empty();
    }

    public static Optional<SuperObjectStorage> getSuperObjectStorage(SuperObjectType super_object_type, String type) {
        for (SuperObjectStorage super_object_storage : GWMCrates.getInstance().getSuperObjectStorage()) {
            if (super_object_storage.getSuperObjectType().equals(super_object_type) &&
                    super_object_storage.getType().equals(type)) {
                return Optional.of(super_object_storage);
            }
        }
        return Optional.empty();
    }

    public static Optional<SuperObject> getSavedSuperObject(SuperObjectType super_object_type, String super_object_name) {
        for (Map.Entry<Pair<SuperObjectType, String>, SuperObject> entry : GWMCrates.getInstance().getSavedSuperObjects().entrySet()) {
            Pair<SuperObjectType, String> pair = entry.getKey();
            if (pair.getKey().equals(super_object_type) && pair.getValue().equals(super_object_name)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public static void createGUIConfigurationDialog(SuperObjectType super_object_type, String type, ConfigurationNode node) {
        if (type.equals("SAVED")) {
            SavedSuperObjectConfigurationDialog dialog = new SavedSuperObjectConfigurationDialog(super_object_type, type, node);
            dialog.setVisible(true);
            return;
        }
        Optional<SuperObjectStorage> optional_super_object_storage = getSuperObjectStorage(super_object_type, type);
        if (!optional_super_object_storage.isPresent()) {
            JOptionPane.showMessageDialog(null, "Wrong type \"" + type + "\" for Super Object \"" + super_object_type.toString() + "\"!", "Error!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SuperObjectStorage super_object_storage = optional_super_object_storage.get();
        Optional<Class<? extends ConfigurationDialog>> optional_configuration_dialog_class = super_object_storage.getConfigurationDialog();
        if (!optional_configuration_dialog_class.isPresent()) {
            JOptionPane.showMessageDialog(null, "Super Object \"" + super_object_type.toString() + "\" with type \"" + type + "\" does not supports graphical configurator!", "Error!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Class<? extends ConfigurationDialog> configuration_dialog_class = optional_configuration_dialog_class.get();
            Constructor<? extends ConfigurationDialog> configuration_dialog_constructor = configuration_dialog_class.getConstructor(ConfigurationNode.class);
            ConfigurationDialog configuration_dialog = configuration_dialog_constructor.newInstance(node);
            configuration_dialog.setVisible(true);
        } catch (Exception e) {
            GWMCrates.getInstance().getLogger().warn("Exception creating graphical configurator for Super Object \"" + super_object_type.toString() + "\" with type \"" + type + "\"!", e);
            JOptionPane.showMessageDialog(null, "Exception creating graphical configurator! See details in console!", "Error!", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static String[] getItemTypes(boolean with_empty) {
        if (with_empty) {
            String[] item_types = new String[GWMCratesGUI.ITEM_TYPES.size()+1];
            item_types[0] = "NO ITEM";
            for (int i = 0; i < GWMCratesGUI.ITEM_TYPES.size(); i++) {
                item_types[i+1] = GWMCratesGUI.ITEM_TYPES.get(i);
            }
            return item_types;
        }
        return GWMCratesGUI.ITEM_TYPES.toArray(new String[GWMCratesGUI.ITEM_TYPES.size()]);
    }

    public static String[] getSoundTypes(boolean with_empty) {
        if (with_empty) {
            String[] sound_types = new String[GWMCratesGUI.SOUND_TYPES.size()+1];
            sound_types[0] = "NO SOUND";
            for (int i = 0; i < GWMCratesGUI.SOUND_TYPES.size(); i++) {
                sound_types[i+1] = GWMCratesGUI.SOUND_TYPES.get(i);
            }
            return sound_types;
        }
        return GWMCratesGUI.SOUND_TYPES.toArray(new String[GWMCratesGUI.SOUND_TYPES.size()]);
    }

    public static String[] getEnchantments(boolean with_empty) {
        if (with_empty) {
            String[] enchantments = new String[GWMCratesGUI.ENCHANTMENTS.size()+1];
            enchantments[0] = "NO ENCHANTMENT";
            for (int i = 0; i < GWMCratesGUI.ENCHANTMENTS.size(); i++) {
                enchantments[i+1] = GWMCratesGUI.ENCHANTMENTS.get(i);
            }
            return enchantments;
        }
        return GWMCratesGUI.ENCHANTMENTS.toArray(new String[GWMCratesGUI.ENCHANTMENTS.size()]);
    }

    public static String[] getBlockTypes(boolean with_empty) {
        if (with_empty) {
            String[] block_types = new String[GWMCratesGUI.BLOCK_TYPES.size()+1];
            block_types[0] = "NO BLOCK";
            for (int i = 0; i < GWMCratesGUI.BLOCK_TYPES.size(); i++) {
                block_types[i+1] = GWMCratesGUI.BLOCK_TYPES.get(i);
            }
            return block_types;
        }
        return GWMCratesGUI.BLOCK_TYPES.toArray(new String[GWMCratesGUI.BLOCK_TYPES.size()]);
    }

    public static List<String> stringToList(String string) {
        return Arrays.asList(string.split("\n"));
    }

    public static String listToString(List<String> list) {
        return StringUtils.join(list, "\n");
    }

    public static List<Integer> stringToIntList(String string) {
        List<Integer> list = new ArrayList<Integer>();
        String[] splited = string.split(" ");
        for (String str : splited) {
            list.add(Integer.valueOf(str));
        }
        return list;
    }

    public static String intListToString(List<Integer> list) {
        return StringUtils.join(list, " ");
    }
}
