package ua.gwm.sponge_plugin.crates.gui;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.StringWriter;

public class GUIOutput {

    private JTextArea area;
    private JScrollPane pane;
    private HoconConfigurationLoader loader;

    public GUIOutput() {
        area = new JTextArea();
        pane = new JScrollPane(area);
        pane.setSize(460, 535);
        pane.setLocation(330, 30);
        loader = HoconConfigurationLoader.builder().
                setDefaultOptions(ConfigurationOptions.defaults()).setSink(() ->
                new BufferedWriter(new GWMStringWriter())).build();
    }

    public void setText(String text) {
        area.setText(text);
    }

    public void setText(ConfigurationNode node) {
        try {
            loader.save(node);
        } catch (Exception e) {

        }
    }

    public String getText() {
        return area.getText();
    }

    public JTextArea getArea() {
        return area;
    }

    public JScrollPane getPane() {
        return pane;
    }

    public HoconConfigurationLoader getLoader() {
        return loader;
    }

    public class GWMStringWriter extends StringWriter {

        @Override
        public void write(@NotNull char[] chars, int i, int i1) {
            StringBuilder builder = new StringBuilder();
            while (i < i1) {
                builder.append(chars[i]);
                i++;
            }
            area.setText(builder.toString());
        }
    }
}
