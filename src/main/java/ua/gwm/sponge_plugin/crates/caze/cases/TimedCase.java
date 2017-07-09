package ua.gwm.sponge_plugin.crates.caze.cases;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import ua.gwm.sponge_plugin.crates.GWMCrates;
import ua.gwm.sponge_plugin.crates.caze.Case;

import java.math.BigDecimal;
import java.util.Optional;

public class TimedCase extends Case {

    private String virtual_name;
    private long delay;

    public TimedCase(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode virtual_name_node = node.getNode("VIRTUAL_NAME");
            ConfigurationNode delay_node = node.getNode("DELAY");
            if (virtual_name_node.isVirtual()) {
                throw new RuntimeException("VIRTUAL_NAME node does not exist!");
            }
            if (delay_node.isVirtual()) {
                throw new RuntimeException("DELAY node does not exist!");
            }
            virtual_name = virtual_name_node.getString();
            delay = delay_node.getLong();
        } catch (Exception e) {
            throw new RuntimeException("Exception creating Timed Case!", e);
        }
    }

    public TimedCase(Optional<BigDecimal> price, String virtual_name, long delay) {
        super(price);
        this.virtual_name = virtual_name;
        this.delay = delay;
    }

    @Override
    public void add(Player player, int amount) {
        ConfigurationNode delay_node = GWMCrates.getInstance().getTimedCasesDelaysConfig().
                getNode(player.getUniqueId().toString(), virtual_name);
        if (amount > 0) {
            delay_node.setValue(null);
        } else if (amount < 0) {
            delay_node.setValue(System.currentTimeMillis() + delay);
        }
    }

    @Override
    public int get(Player player) {
        ConfigurationNode delay_node = GWMCrates.getInstance().getTimedCasesDelaysConfig().
                getNode(player.getUniqueId().toString(), virtual_name);
        if (delay_node.isVirtual()) {
            return Integer.MAX_VALUE;
        }
        long delay = delay_node.getLong();
        if (System.currentTimeMillis() >= delay) {
            delay_node.setValue(null);
            return Integer.MAX_VALUE;
        } else {
            return 0;
        }
    }

    public String getVirtualName() {
        return virtual_name;
    }

    public void setVirtualName(String virtual_name) {
        this.virtual_name = virtual_name;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}
