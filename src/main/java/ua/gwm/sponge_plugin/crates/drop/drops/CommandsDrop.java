package ua.gwm.sponge_plugin.crates.drop.drops;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import ua.gwm.sponge_plugin.crates.drop.Drop;
import ua.gwm.sponge_plugin.crates.util.GWMCratesUtils;

import java.math.BigDecimal;
import java.util.*;

public class CommandsDrop extends Drop {

    private List<Command> commands;

    public CommandsDrop(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode commands_node = node.getNode("COMMANDS");
            if (commands_node.isVirtual()) {
                throw new RuntimeException("COMMANDS node does not exist!");
            }
            commands = new ArrayList<Command>();
            for (ConfigurationNode command_node : commands_node.getChildrenList()) {
                commands.add(GWMCratesUtils.parseCommand(command_node));
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Commands Drop!", e);
        }
    }

    public CommandsDrop(Optional<String> id, Optional<BigDecimal> price, Optional<ItemStack> drop_item, int level,
                        List<Command> commands) {
        super(id, price, drop_item, level);
        this.commands = commands;
    }

    @Override
    public void apply(Player player) {
        ConsoleSource console_source = Sponge.getServer().getConsole();
        for (Command command : commands) {
            String cmd = command.getCmd().replace("%PLAYER%", player.getName());
            boolean console = command.isConsole();
            Sponge.getCommandManager().process(console ? console_source : player, cmd);
        }
    }

    public static class Command {

        private String cmd;
        private boolean console;

        public Command(String cmd, boolean console) {
            this.cmd = cmd;
            this.console = console;
        }

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public boolean isConsole() {
            return console;
        }

        public void setConsole(boolean console) {
            this.console = console;
        }
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }
}
