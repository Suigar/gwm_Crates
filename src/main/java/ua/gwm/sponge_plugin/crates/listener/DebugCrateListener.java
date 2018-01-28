package ua.gwm.sponge_plugin.crates.listener;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.event.PlayerOpenedCrateEvent;
import ua.gwm.sponge_plugin.crates.manager.Manager;
import ua.gwm.sponge_plugin.crates.util.LanguageUtils;
import ua.gwm.sponge_plugin.crates.util.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DebugCrateListener {

    public static File LOG_FILE;
    public static BufferedWriter LOG_FILE_WRITER;

    static {
        new UpdateRunnable().run();
    }

    @Listener
    public void onOpened(PlayerOpenedCrateEvent event) {
        Player player = event.getPlayer();
        Manager manager = event.getManager();
        Drop drop = event.getDrop();
        if (manager.isSendOpenMessage()) {
            Optional<String> optional_custom_open_message = manager.getCustomOpenMessage();
            if (optional_custom_open_message.isPresent()) {
                player.sendMessage(TextSerializers.FORMATTING_CODE.
                        deserialize(optional_custom_open_message.get().
                                replace("%MANAGER%", manager.getName())));
            } else {
                player.sendMessage(LanguageUtils.getText("SUCCESSFULLY_OPENED_MANAGER",
                        new Pair<String, String>("%MANAGER%", manager.getName())));
            }
        }
        if (GWMCrates.getInstance().isLogOpenedCrates()) {
            try {
                String time = LocalTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_TIME);
                String player_name = player.getName();
                String player_uuid = player.getUniqueId().toString();
                String manager_name = manager.getName();
                String manager_id = manager.getId();
                String drop_name = drop == null ? "null" : drop.getId().orElse("Unknown ID");
                Location<World> location = player.getLocation();
                String player_location = location.getExtent().getName() + ' ' + location.getBlockX() + ' ' + location.getBlockY() + ' ' + location.getBlockZ();
                LOG_FILE_WRITER.write(LanguageUtils.getPhrase("MANAGER_OPENING_LOG_MESSAGE",
                        new Pair("%TIME%", time),
                        new Pair("%PLAYER%", player_name),
                        new Pair("%PLAYER_UUID%", player_uuid),
                        new Pair("%MANAGER_NAME%", manager_name),
                        new Pair("%MANAGER_ID%", manager_id),
                        new Pair("%DROP%", drop_name),
                        new Pair("%LOCATION%", player_location)) + '\n');
                LOG_FILE_WRITER.flush();
            } catch (Exception e) {
                GWMCrates.getInstance().getLogger().warn("Exception logging crate opening!", e);
            }
        }
    }

    static class UpdateRunnable implements Runnable {

        @Override
        public void run() {
            try {
                LOG_FILE = new File(GWMCrates.getInstance().getLogsDirectory(),
                        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".log");
                if (!LOG_FILE.exists()) {
                    LOG_FILE.createNewFile();
                }
                BufferedWriter old_file_writer = LOG_FILE_WRITER;
                LOG_FILE_WRITER = new BufferedWriter(new FileWriter(LOG_FILE, true));
                if (old_file_writer != null) {
                    old_file_writer.close();
                }
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime update_time = now.withNano(0).withSecond(0).withMinute(0).withHour(0).plusDays(1);
                Duration duration = Duration.between(now, update_time);
                scheduler.schedule(this, duration.toMillis(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                GWMCrates.getInstance().getLogger().warn("Exception updating log file/log file writer!", e);
            }
        }
    }
}
