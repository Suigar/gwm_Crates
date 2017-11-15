package ua.gwm.sponge_plugin.crates;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import de.randombyte.holograms.api.HologramsService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import ua.gwm.sponge_plugin.crates.caze.Case;
import ua.gwm.sponge_plugin.crates.caze.cases.*;
import ua.gwm.sponge_plugin.crates.change_mode.change_modes.OrderedChangeMode;
import ua.gwm.sponge_plugin.crates.change_mode.change_modes.RandomChangeMode;
import ua.gwm.sponge_plugin.crates.command.GWMCratesCommand;
import ua.gwm.sponge_plugin.crates.drop.drops.*;
import ua.gwm.sponge_plugin.crates.event.GWMCratesRegistrationEvent;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.caze.*;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.change_mode.OrderedChangeModeConfigurationDialog;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.change_mode.RandomChangeModeConfigurationDialog;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.drop.*;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.key.*;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.open_manager.Animation1OpenManagerConfigurationDialog;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.open_manager.FirstOpenManagerConfigurationDialog;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.open_manager.NoGuiOpenManagerConfigurationDialog;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.open_manager.SecondOpenManagerConfigurationDialog;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.preview.FirstPreviewConfigurationDialog;
import ua.gwm.sponge_plugin.crates.gui.configuration_dialog.configuration_dialogues.preview.SecondPreviewConfigurationDialog;
import ua.gwm.sponge_plugin.crates.key.keys.*;
import ua.gwm.sponge_plugin.crates.listener.*;
import ua.gwm.sponge_plugin.crates.manager.Manager;
import ua.gwm.sponge_plugin.crates.open_manager.open_managers.Animation1OpenManager;
import ua.gwm.sponge_plugin.crates.open_manager.open_managers.FirstOpenManager;
import ua.gwm.sponge_plugin.crates.open_manager.open_managers.NoGuiOpenManager;
import ua.gwm.sponge_plugin.crates.open_manager.open_managers.SecondOpenManager;
import ua.gwm.sponge_plugin.crates.preview.previews.FirstGuiPreview;
import ua.gwm.sponge_plugin.crates.preview.previews.SecondGuiPreview;
import ua.gwm.sponge_plugin.crates.util.*;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

@Plugin(
        id = "gwm_crates",
        name = "GWMCrates",
        version = "2.01",
        description = "Universal crates plugin for your server!",
        authors = {"GWM"/*
                Nazar Kalinovskiy
                My contacts:
                email(gwm@tutanota.com),
                Discord(GWM#2192),
                Telegram(@grewema),
                Wire(@grewema)*/})
public class GWMCrates {

    public static final double CURRENT_VERSION = 2.01;

    private static GWMCrates instance;

    public static GWMCrates getInstance() {
        if (instance == null) {
            throw new RuntimeException("GWMCrates not initialized!");
        }
        return instance;
    }

    private Cause default_cause;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File config_directory;
    private File managers_directory;

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer plugin_container;

    private HashSet<SuperObjectStorage> super_objects =
            new HashSet<SuperObjectStorage>();

    private HashMap<Pair<SuperObjectType, String>, SuperObject> saved_super_objects =
            new HashMap<Pair<SuperObjectType, String>, SuperObject>();

    private HashSet<Manager> created_managers = new HashSet<Manager>();

    private Optional<EconomyService> optional_economy_service = Optional.empty();
    private Optional<HologramsService> optional_holograms_service = Optional.empty();

    private Config config;
    private Config language_config;
    private Config virtual_cases_config;
    private Config virtual_keys_config;
    private Config timed_cases_delays_config;
    private Config timed_keys_delays_config;
    private Config saved_super_objects_config;

    private boolean debug = false;
    private boolean check_updates = true;
    private double api_version = 6;
    private Vector3d hologram_offset = new Vector3d(0.5, -1.2, 0.5);

    @Listener
    public void onConstruct(GameConstructionEvent event) {
        instance = this;
    }

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        managers_directory = new File(config_directory, "managers");
        if (!config_directory.exists()) {
            logger.info("Config directory does not exist! Trying to create it...");
            try {
                config_directory.mkdirs();
                logger.info("Config directory successfully created!");
            } catch (Exception e) {
                logger.warn("Failed creating config directory!", e);
            }
        }
        if (!managers_directory.exists()) {
            logger.info("Managers directory does not exist! Trying to create it...");
            try {
                managers_directory.mkdirs();
                logger.info("Managers directory successfully created!");
            } catch (Exception e) {
                logger.warn("Failed creating managers config directory!", e);
            }
        }
        config = new Config("config.conf", false);
        language_config = new Config("language.conf", false);
        saved_super_objects_config = new Config("saved_super_objects.conf", false);
        virtual_cases_config = new Config("virtual_cases.conf", true);
        virtual_keys_config = new Config("virtual_keys.conf", true);
        timed_cases_delays_config = new Config("timed_cases_delays.conf", true);
        timed_keys_delays_config = new Config("timed_keys_delays.conf", true);
        loadConfigValues();
        default_cause = UnsafeUtils.createDefaultCause();
        if (check_updates) {
            checkUpdates();
        }
        logger.info("PreInitialization complete!");
    }

    @Listener
    public void onInitialize(GameInitializationEvent event) {
        Sponge.getEventManager().registerListeners(this, new ItemCaseListener());
        Sponge.getEventManager().registerListeners(this, new BlockCaseListener());
        Sponge.getEventManager().registerListeners(this, new FirstGuiOpenManagerListener());
        Sponge.getEventManager().registerListeners(this, new SecondGuiOpenManagerListener());
        Sponge.getEventManager().registerListeners(this, new PreviewListener());
        Sponge.getEventManager().registerListeners(this, new Animation1Listener());
        Sponge.getEventManager().registerListeners(this, new EntityCaseListener());
        Sponge.getEventManager().registerListeners(this, new DebugCrateListener());
        Sponge.getCommandManager().register(this, new GWMCratesCommand(),
                "gwmcrates", "gwmcrate", "crates", "crate");
        register();
        logger.info("Initialization complete!");
    }

    @Listener
    public void onPostInitialization(GamePostInitializationEvent event) {
        loadEconomy();
        loadHologramsService();
        loadSavedSuperObjects();
        Sponge.getScheduler().createTaskBuilder().
                delayTicks(config.getNode("MANAGERS_LOAD_DELAY").getLong(20)).
                execute(this::createManagers).submit(this);
        logger.info("PostInitialization complete!");
    }

    @Listener
    public void onStopping(GameStoppingServerEvent event) {
        deleteHolograms();
        save();
        logger.info("Stopping complete!");
    }

    @Listener
    public void reloadListener(GameReloadEvent event) {
        reload();
    }

    public void save() {
        config.save();
        language_config.save();
        virtual_cases_config.save();
        virtual_keys_config.save();
        timed_cases_delays_config.save();
        timed_keys_delays_config.save();
        saved_super_objects_config.save();
        logger.info("All plugin configs has been saved!");
    }

    public void reload() {
        deleteHolograms();
        created_managers.clear();
        config.reload();
        language_config.reload();
        virtual_cases_config.reload();
        virtual_keys_config.reload();
        timed_cases_delays_config.reload();
        timed_keys_delays_config.reload();
        saved_super_objects_config.reload();
        super_objects.clear();
        saved_super_objects.clear();
        loadConfigValues();
        default_cause = UnsafeUtils.createDefaultCause();
        register();
        optional_economy_service = Optional.empty();
        optional_holograms_service = Optional.empty();
        loadEconomy();
        loadHologramsService();
        loadSavedSuperObjects();
        createManagers();
        if (check_updates) {
            checkUpdates();
        }
        logger.info("Plugin has been reloaded.");
    }

    private void deleteHolograms() {
        try {
            for (Manager manager : created_managers) {
                Case caze = manager.getCase();
                if (caze instanceof BlockCase) {
                    BlockCase block_case = (BlockCase) caze;
                    block_case.getCreatedHologram().ifPresent(HologramsService.Hologram::remove);
                }
            }
            Animation1OpenManager.PLAYERS_OPENING_ANIMATION1.values().
                    forEach(information -> information.getHolograms().
                            forEach(HologramsService.Hologram::remove));
        } catch (Exception e) {
            if (debug) {
                logger.warn("Exception deleting holograms (Ignore this if you have no holograms)!", e);
            }
        }
    }

    private void register() {
        GWMCratesRegistrationEvent registration_event = new GWMCratesRegistrationEvent();
        registration_event.register(SuperObjectType.CASE, "ITEM", ItemCase.class, Optional.of(ItemCaseConfigurationDialog.class));
        registration_event.register(SuperObjectType.CASE, "BLOCK", BlockCase.class, Optional.of(BlockCaseConfigurationDialog.class));
        registration_event.register(SuperObjectType.CASE, "ENTITY", EntityCase.class, Optional.of(EntityCaseConfigurationDialog.class));
        registration_event.register(SuperObjectType.CASE, "TIMED", TimedCase.class, Optional.of(TimedCaseConfigurationDialog.class));
        registration_event.register(SuperObjectType.CASE, "VIRTUAL", VirtualCase.class, Optional.of(VirtualCaseConfigurationDialog.class));
        registration_event.register(SuperObjectType.CASE, "EMPTY", EmptyCase.class, Optional.of(EmptyCaseConfigurationDialog.class));
        registration_event.register(SuperObjectType.KEY, "ITEM", ItemKey.class, Optional.of(ItemKeyConfigurationDialog.class));
        registration_event.register(SuperObjectType.KEY, "MULTI", MultiKey.class, Optional.of(MultiKeyConfigurationDialog.class));
        registration_event.register(SuperObjectType.KEY, "MULTIPLE-AMOUNT", MultipleAmountKey.class, Optional.of(MultipleAmountKeyConfigurationDialog.class));
        registration_event.register(SuperObjectType.KEY, "TIMED", TimedKey.class, Optional.of(TimedKeyConfigurationDialog.class));
        registration_event.register(SuperObjectType.KEY, "VIRTUAL", VirtualKey.class, Optional.of(VirtualKeyConfigurationDialog.class));
        registration_event.register(SuperObjectType.KEY, "EMPTY", EmptyKey.class, Optional.of(EmptyKeyConfigurationDialog.class));
        registration_event.register(SuperObjectType.OPEN_MANAGER, "NO-GUI", NoGuiOpenManager.class, Optional.of(NoGuiOpenManagerConfigurationDialog.class));
        registration_event.register(SuperObjectType.OPEN_MANAGER, "FIRST", FirstOpenManager.class, Optional.of(FirstOpenManagerConfigurationDialog.class));
        registration_event.register(SuperObjectType.OPEN_MANAGER, "SECOND", SecondOpenManager.class, Optional.of(SecondOpenManagerConfigurationDialog.class));
        registration_event.register(SuperObjectType.OPEN_MANAGER, "ANIMATION1", Animation1OpenManager.class, Optional.of(Animation1OpenManagerConfigurationDialog.class));
        registration_event.register(SuperObjectType.PREVIEW, "FIRST", FirstGuiPreview.class, Optional.of(FirstPreviewConfigurationDialog.class));
        registration_event.register(SuperObjectType.PREVIEW, "SECOND", SecondGuiPreview.class, Optional.of(SecondPreviewConfigurationDialog.class));
        registration_event.register(SuperObjectType.DROP, "ITEM", ItemDrop.class, Optional.of(ItemDropConfigurationDialog.class));
        registration_event.register(SuperObjectType.DROP, "COMMANDS", CommandsDrop.class, Optional.of(CommandsDropConfigurationDialog.class));
        registration_event.register(SuperObjectType.DROP, "MULTI", MultiDrop.class, Optional.of(MultiDropConfigurationDialog.class));
        registration_event.register(SuperObjectType.DROP, "DELAY", DelayDrop.class, Optional.of(DelayDropConfigurationDialog.class));
        registration_event.register(SuperObjectType.DROP, "PERMISSION", PermissionDrop.class, Optional.of(PermissionDropConfigurationDialog.class));
        registration_event.register(SuperObjectType.DECORATIVE_ITEMS_CHANGE_MODE, "RANDOM", RandomChangeMode.class, Optional.of(RandomChangeModeConfigurationDialog.class));
        registration_event.register(SuperObjectType.DECORATIVE_ITEMS_CHANGE_MODE, "ORDERED", OrderedChangeMode.class, Optional.of(OrderedChangeModeConfigurationDialog.class));
        Sponge.getEventManager().post(registration_event);
        for (SuperObjectStorage super_object_storage : registration_event.getSuperObjectStorage()) {
            SuperObjectType super_object_type = super_object_storage.getSuperObjectType();
            String type = super_object_storage.getType();
            if (Utils.getSuperObjectStorage(super_object_type, type).isPresent()) {
                logger.warn("Super Objects already contains Super Object \"" + super_object_type + "\" with type \"" + type + "\"!");
            } else {
                super_objects.add(super_object_storage);
                logger.info("Successfully added Super Object \"" + super_object_type + "\" with type \"" + type + "\"!");
            }
        }
        logger.info("Registration complete!");
    }

    private void loadConfigValues() {
        try {
            debug = config.getNode("DEBUG").getBoolean(false);
            check_updates = config.getNode("CHECK_UPDATES").getBoolean(true);
            api_version = config.getNode("API_VERSION").getDouble(6);
            hologram_offset = config.getNode("HOLOGRAM_OFFSET").getValue(TypeToken.of(Vector3d.class), new Vector3d(0.5, -1.2, 0.5));
        } catch (ObjectMappingException e) {
            logger.warn("Exception loading config values!", e);
        }
    }

    private void loadSavedSuperObjects() {
        saved_super_objects_config.getNode("SAVED_SUPER_OBJECTS").getChildrenList().forEach(node -> {
            ConfigurationNode super_object_type_node = node.getNode("SUPER_OBJECT_TYPE");
            ConfigurationNode saved_id_node = node.getNode("SAVED_ID");
            ConfigurationNode id_node = node.getNode("ID");
            String id = id_node.isVirtual() ? "Unknown ID" : id_node.getString();
            if (super_object_type_node.isVirtual()) {
                throw new RuntimeException("SUPER_OBJECT_TYPE node does not exist for Saved Super Object with id \"" + id + "\"!");
            }
            String super_object_type_name = super_object_type_node.getString();
            if (!SuperObjectType.SUPER_OBJECT_TYPES.containsKey(super_object_type_name)) {
                throw new RuntimeException("Super Object Type \"" + super_object_type_name + "\" does not found!");
            }
            SuperObjectType super_object_type = SuperObjectType.SUPER_OBJECT_TYPES.get(super_object_type_name);
            if (saved_id_node.isVirtual()) {
                throw new RuntimeException("SAVED_ID node does not exist for Saved Super Object \"" + super_object_type + "\" with id \"" + id + "\"!");
            }
            String saved_id = saved_id_node.getString();
            Pair<SuperObjectType, String> pair = new Pair<SuperObjectType, String>(super_object_type, saved_id);
            if (saved_super_objects.containsKey(pair)) {
                throw new RuntimeException("Saved Super Objects already contains Saved Super Object \"" + super_object_type + "\" with saved ID \"" + saved_id + "\"!");
            }
            saved_super_objects.put(pair, Utils.createSuperObject(node, super_object_type));
        });
    }

    private void createManagers() {
        try {
            Files.walk(managers_directory.toPath()).forEach(path -> {
                File manager_file = path.toFile();
                if (!manager_file.isDirectory()) {
                    try {
                        ConfigurationLoader<CommentedConfigurationNode> manager_configuration_loader =
                                HoconConfigurationLoader.builder().setFile(manager_file).build();
                        ConfigurationNode manager_node = manager_configuration_loader.load();
                        Manager manager = new Manager(manager_node);
                        created_managers.add(manager);
                        logger.info("Manager \"" + manager.getName() + "\" successfully created!");
                    } catch (Exception e) {
                        logger.info("Exception creating manager " + manager_file.getName() + "!", e);
                    }
                }
            });
        } catch (Exception e) {
            logger.warn("Exception creating managers!", e);
        }
    }

    private boolean loadEconomy() {
        optional_economy_service = Sponge.getServiceManager().provide(EconomyService.class);
        if (optional_economy_service.isPresent()) {
            logger.info("Economy Service found!");
            return true;
        }
        logger.warn("Economy Service does not found!");
        logger.info("Please install plugin that provides Economy Service, if you want use economical features.");
        return false;
    }

    private boolean loadHologramsService() {
        try {
            optional_holograms_service = Sponge.getServiceManager().provide(HologramsService.class);
            if (optional_holograms_service.isPresent()) {
                logger.info("Holograms Service found!");
                return true;
            }
        } catch (NoClassDefFoundError ignored) {}
        logger.warn("Holograms Service does not found!");
        logger.info("Please install \"Holograms\" plugin (https://ore.spongepowered.org/RandomByte/Holograms) if you want use holograms!");
        return false;
    }

    private void checkUpdates() {
        Sponge.getScheduler().createTaskBuilder().async().execute(() -> {
            try {
                InputStreamReader reader = new InputStreamReader(new URL("https://ore.spongepowered.org/api/projects/gwm_crates").openStream());
                JsonObject object = new Gson().fromJson(reader, JsonObject.class);
                double version = object.get("recommended").getAsJsonObject().get("name").getAsJsonPrimitive().getAsDouble();
                if (version > CURRENT_VERSION) {
                    logger.warn("New version (" + version + ") available on Ore!");
                }
            } catch (Exception e) {
                logger.warn("Exception checking plugin updates on Ore!", e);
            }
        }).submit(this);
    }

    public Cause getDefaultCause() {
        return default_cause;
    }

    public File getConfigDirectory() {
        return config_directory;
    }

    public File getManagersDirectory() {
        return managers_directory;
    }

    public Logger getLogger() {
        return logger;
    }

    public Optional<EconomyService> getEconomyService() {
        return optional_economy_service;
    }

    public Optional<HologramsService> getHologramsService() {
        return optional_holograms_service;
    }

    public PluginContainer getPluginContainer() {
        return plugin_container;
    }

    public HashSet<SuperObjectStorage> getSuperObjectStorage() {
        return super_objects;
    }

    public HashMap<Pair<SuperObjectType, String>, SuperObject> getSavedSuperObjects() {
        return saved_super_objects;
    }

    public HashSet<Manager> getCreatedManagers() {
        return created_managers;
    }

    public Config getConfig() {
        return config;
    }

    public Config getLanguageConfig() {
        return language_config;
    }

    public Config getVirtualCasesConfig() {
        return virtual_cases_config;
    }

    public Config getVirtualKeysConfig() {
        return virtual_keys_config;
    }

    public Config getTimedCasesDelaysConfig() {
        return timed_cases_delays_config;
    }

    public Config getTimedKeysDelaysConfig() {
        return timed_keys_delays_config;
    }

    public boolean isDebugEnabled() {
        return debug;
    }

    public boolean isCheckUpdates() {
        return check_updates;
    }

    public double getApiVersion() {
        return api_version;
    }

    public Vector3d getHologramOffset() {
        return hologram_offset;
    }
}
