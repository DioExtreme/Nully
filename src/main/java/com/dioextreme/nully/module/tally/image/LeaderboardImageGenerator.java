package com.dioextreme.nully.module.tally.image;

import com.dioextreme.nully.module.tally.entity.RankedMember;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

public class LeaderboardImageGenerator
{
    private final int imageWidth = 230;
    private final int imageHeight = 230;
    private final int rankX = 10;
    private final int nameX = 50;
    private final int pointsX = 180;
    private final int firstRowY = 20;
    private final int perRowY = 40;

    public BufferedImage generate(@Nonnull List<RankedMember> leaderboardPage, int page, Color foregroundColor)
    {
        int width = imageWidth;
        int height = imageHeight;

        Color backgroundColor = new Color(32, 34, 37);
        Color defaultWordColor = Color.WHITE;

        Font wordFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        Color wordColor = Objects.requireNonNullElse(foregroundColor, defaultWordColor);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Maximize quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // Background equivalent to Discord's code color
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, width, height);

        g2d.setFont(wordFont);
        g2d.setColor(wordColor);

        // First line
        g2d.drawString("Rank", rankX, firstRowY);
        g2d.drawString("Name", nameX, firstRowY);
        g2d.drawString("Points", pointsX, firstRowY);

        int drawIndex = 0;
        int rankIndex = (page - 1) * 10 + 1;

        FontMetrics fontMetrics = g2d.getFontMetrics();

        for (RankedMember member : leaderboardPage)
        {
            String name = member.getName();
            String nameTrimmed = name.substring(0, Math.min(name.length(), 16));
            String points = String.valueOf(member.getRankedPoints());

            int rowY = perRowY + drawIndex * firstRowY;
            g2d.drawString(String.valueOf(rankIndex), rankX, rowY);
            g2d.drawString(nameTrimmed, nameX, rowY);
            g2d.drawString(points, pointsX + 35 - fontMetrics.stringWidth(points), rowY);
            ++drawIndex;
            ++rankIndex;
        }

        g2d.dispose();

        return bufferedImage;
    }
}
