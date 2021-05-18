package com.comitative.pic.providers;

import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * An icon with a custom text on it. We use this class to dynamically generate icons
 * with approximate relative time values.
 */
class ImpactIcon implements Icon {
    private final String text;
    private final JBColor fontColor;

    /**
     *
     * @param text text to display on an icon
     * @param fontColor the font color (defined as an argument to make it easy to color-code impact values)
     */
    ImpactIcon(@NotNull String text, @NotNull JBColor fontColor) {
        this.text = text;
        this.fontColor = fontColor;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        graphics.setColor(fontColor);
        graphics.setFont(ICON_TEXT_FONT);
        graphics.drawString(text, x + 1, y + 1 + ICON_FONT_SIZE);
    }

    @Override
    public int getIconWidth() {
        return ICON_SIZE;
    }

    @Override
    public int getIconHeight() {
        return ICON_SIZE;
    }

    private static final int ICON_SIZE = 12; // The documented size for gutter icons
    private static final int ICON_FONT_SIZE = 9;
    private static final Font ICON_TEXT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, ICON_FONT_SIZE);
}
