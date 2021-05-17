package com.comitative.pic.providers;

import com.comitative.pic.TimeRecord;
import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ImpactGutterMark implements GutterMark {
    private final String tooltipText;
    private final Icon icon;

    public ImpactGutterMark(long impactClass, @NotNull TimeRecord timeRecord) {
        double scaledRelativeTime = timeRecord.getRelativeTime() * 100.0;
        long sampleCount = timeRecord.getSampleCount();
        this.tooltipText = String.format(
                "%.02f%% (%d %s)",
                scaledRelativeTime,
                sampleCount,
                sampleCount == 1 ? "sample" : "samples");
        this.icon = renderIcon(String.format("%02d", impactClass));
    }

    @Override
    public @NotNull Icon getIcon() {
        return icon;
    }

    @Override
    public @Nullable
    @NlsContexts.Tooltip String getTooltipText() {
        return tooltipText;
    }

    private static Icon renderIcon(String text) {

        return new Icon() {
            @Override
            public void paintIcon(Component component, Graphics graphics, int x, int y) {
                graphics.setColor(JBColor.WHITE);
                graphics.fillRect(x, y, ICON_SIZE, ICON_SIZE);
                graphics.setColor(JBColor.RED);
                graphics.setFont(ICON_FONT);
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
        };
    }

    private static final int ICON_SIZE = 12;
    private static final int ICON_FONT_SIZE = 9;
    private static final Font ICON_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, ICON_FONT_SIZE);
}
