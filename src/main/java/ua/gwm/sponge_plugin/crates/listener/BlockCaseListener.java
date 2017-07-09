package ua.gwm.sponge_plugin.crates.listener;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.caze.Case;
import ua.gwm.sponge_plugin.crates.caze.cases.BlockCase;
import ua.gwm.sponge_plugin.crates.key.Key;
import ua.gwm.sponge_plugin.crates.manager.Manager;
import ua.gwm.sponge_plugin.crates.open_manager.OpenManager;
import ua.gwm.sponge_plugin.crates.preview.Preview;
import ua.gwm.sponge_plugin.crates.util.LanguageUtils;
import ua.gwm.sponge_plugin.crates.util.Pair;

import java.util.Optional;

public class BlockCaseListener {

    @Listener(order = Order.LATE)
    public void openBlockCase(InteractBlockEvent.Secondary.MainHand event) {
        Optional<Player> optional_player = event.getCause().first(Player.class);
        if (!optional_player.isPresent()) return;
        Player player = optional_player.get();
        Optional<Location<World>> optional_location = event.getTargetBlock().getLocation();
        if (!optional_location.isPresent()) return;
        Location<World> location = optional_location.get();
        for (Manager manager : GWMCrates.getInstance().getCreatedManagers()) {
            Case caze = manager.getCase();
            if (!(caze instanceof BlockCase)) continue;
            Location<World> block_case_location = ((BlockCase) caze).getLocation();
            if (!block_case_location.equals(location)) continue;
            event.setCancelled(true);
            OpenManager open_manager = manager.getOpenManager();
            if (!open_manager.canOpen(player, manager)) {
                player.sendMessage(LanguageUtils.getText("CAN_NOT_OPEN_CASE"));
                return;
            }
            Key key = manager.getKey();
            if (key.get(player) < 1) {
                player.sendMessage(LanguageUtils.getText("HAVE_NOT_KEY"));
                return;
            }
            key.add(player, -1);
            manager.getOpenManager().open(player, manager);
            break;
        }
    }

    @Listener(order = Order.LATE)
    public void startBlockCasePreview(InteractBlockEvent.Primary.MainHand event) {
        Optional<Player> optional_player = event.getCause().first(Player.class);
        if (!optional_player.isPresent()) return;
        Player player = optional_player.get();
        Optional<Location<World>> optional_location = event.getTargetBlock().getLocation();
        if (!optional_location.isPresent()) return;
        Location<World> location = optional_location.get();
        for (Manager manager : GWMCrates.getInstance().getCreatedManagers()) {
            String manager_id = manager.getId();
            Case caze = manager.getCase();
            if (!(caze instanceof BlockCase)) continue;
            Location<World> block_case_location = ((BlockCase) caze).getLocation();
            if (!block_case_location.equals(location)) continue;
            event.setCancelled(true);
            if (!((BlockCase) caze).isStartPreviewOnLeftClick()) return;
            Optional<Preview> optional_preview = manager.getPreview();
            if (!optional_preview.isPresent()) {
                player.sendMessage(LanguageUtils.getText("PREVIEW_NOT_AVAILABLE",
                        new Pair<String, String>("%MANAGER%", manager.getName())));
                return;
            }
            Preview preview = optional_preview.get();
            if (!player.hasPermission("gwm_crates.preview." + manager_id)) {
                player.sendMessage(LanguageUtils.getText("HAVE_NOT_PERMISSION"));
                return;
            }
            preview.preview(player, manager);
            player.sendMessage(LanguageUtils.getText("PREVIEW_STARTED",
                    new Pair<String, String>("%MANAGER%", manager.getName())));
            break;
        }
    }
}
