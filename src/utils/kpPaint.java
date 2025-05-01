package utils;

import org.rspeer.game.position.Position;
import org.rspeer.game.position.area.Area;
import org.rspeer.game.scene.Projection;

import java.awt.*;

public class kpPaint
{

    // Paint

    public static void DrawString(Graphics2D g2d, String string, int x, int y, Color color)
    {
        DrawString(g2d, string, x, y, color, false); // Don't center
    }

    public static void DrawString(Graphics2D g2d, String string, int x, int y, Color color, boolean center)
    {
        Color oldColor = g2d.getColor();

        if (center)
        {
            FontMetrics fm = g2d.getFontMetrics();
            int stringWidth = fm.stringWidth(string);
            int stringHeight = fm.getHeight();

            x = x - (stringWidth / 2);
            y = y - (stringHeight / 2) + fm.getAscent();
        }

        // Shadow
        g2d.setColor(Color.BLACK);
        g2d.drawString(string, x + 1, y + 1);

        g2d.setColor(color);
        g2d.drawString(string, x, y);

        g2d.setColor(oldColor);

        return;
    }

    public static void DrawTile(Graphics2D g2d, Position position, Color color)
    {
        Color oldColor = g2d.getColor();

        g2d.setColor(color);

        Polygon positionPolygon = Projection.getPositionPolygon(Projection.Canvas.VIEWPORT, position);

        // Fill

        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 24));
        g2d.fillPolygon(positionPolygon);
        g2d.setColor(color);

        //

        g2d.drawPolygon(positionPolygon);

        g2d.setColor(oldColor);

        return;
    }

    public static void DrawTileLabel(Graphics2D g2d, Position position, String label, Color color)
    {
        DrawTileLabel(g2d, position, label, color, false);
    }

    public static void DrawTileLabel(Graphics2D g2d, Position position, String label, Color color, boolean drawTile)
    {
        Color oldColor = g2d.getColor();

        g2d.setColor(color);

        Polygon tilePolygon = Projection.getPositionPolygon(Projection.Canvas.VIEWPORT, position);

        /*
            xpoints/ypoints index
            1 = nw
            0 = sw
            3 = se
            2 = ne
        */

        int nwX = tilePolygon.xpoints[1];
        int nwY = tilePolygon.ypoints[1];

        int seX = tilePolygon.xpoints[3];
        int seY = tilePolygon.ypoints[3];

        int x = (nwX + seX) / 2;
        int y = (nwY + seY) / 2;

        if (drawTile)
        {
            g2d.drawPolygon(Projection.getPositionPolygon(Projection.Canvas.VIEWPORT, position));
        }

        // The whole point of this function is to draw the label, so it should never be null
        DrawString(g2d, label, x, y, color, true);

        g2d.setColor(oldColor);
    }

    public static void DrawArea(Graphics2D g2d, Area area, Color color, boolean drawSouthWestTile)
    {
        DrawArea(g2d, area, null, color, drawSouthWestTile);
    }

    public static void DrawArea(Graphics2D g2d, Area area, String label, Color color, boolean drawSouthWestTile)
    {
        // Calculate corners

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Position tile : area.getTiles())
        {
            int x = tile.getX();
            int y = tile.getY();

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }

        Polygon nwPolygon = Projection.getPositionPolygon(Projection.Canvas.VIEWPORT, new Position(minX, maxY, area.getFloorLevel()));
        Polygon nePolygon = Projection.getPositionPolygon(Projection.Canvas.VIEWPORT, new Position(maxX, maxY, area.getFloorLevel()));
        Polygon swPolygon = Projection.getPositionPolygon(Projection.Canvas.VIEWPORT, new Position(minX, minY, area.getFloorLevel()));
        Polygon sePolygon = Projection.getPositionPolygon(Projection.Canvas.VIEWPORT, new Position(maxX, minY, area.getFloorLevel()));

        // Draw

        Color oldColor = g2d.getColor();

        g2d.setColor(color);

        /*
            xpoints/ypoints index
            1 = nw
            0 = sw
            3 = se
            2 = ne
        */

        int nwVertexX = nwPolygon.xpoints[1];
        int nwVertexY = nwPolygon.ypoints[1];

        int neVertexX = nePolygon.xpoints[2];
        int neVertexY = nePolygon.ypoints[2];

        int swVertexX = swPolygon.xpoints[0];
        int swVertexY = swPolygon.ypoints[0];

        int seVertexX = sePolygon.xpoints[3];
        int seVertexY = sePolygon.ypoints[3];

        if (!IsValidVertexPosition(nwVertexX, nwVertexY, neVertexX, neVertexY, swVertexX, swVertexY, seVertexX, seVertexY))
        {
            return;
        }

        g2d.drawLine(nwVertexX, nwVertexY, neVertexX, neVertexY);
        g2d.drawLine(neVertexX, neVertexY, seVertexX, seVertexY);
        g2d.drawLine(seVertexX, seVertexY, swVertexX, swVertexY);
        g2d.drawLine(swVertexX, swVertexY, nwVertexX, nwVertexY);

        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 24));

        g2d.fillPolygon(new int[]{nwVertexX, neVertexX, seVertexX, swVertexX}, new int[]{nwVertexY, neVertexY, seVertexY, swVertexY}, 4);

        g2d.setColor(color);

        int labelX;
        int labelY;

        if (drawSouthWestTile)
        {
            int nwX = swPolygon.xpoints[1];
            int nwY = swPolygon.ypoints[1];

            int seX = swPolygon.xpoints[3];
            int seY = swPolygon.ypoints[3];

            labelX = (nwX + seX) / 2;
            labelY = (nwY + seY) / 2;

            g2d.drawPolygon(swPolygon);
        }
        else
        {
            labelX = (nwVertexX + seVertexX) / 2;
            labelY = (nwVertexY + seVertexY) / 2;
        }

        if (label != null)
        {
            DrawString(g2d, label, labelX, labelY, color, true);
        }

        g2d.setColor(oldColor);
    }

    private static boolean IsValidVertexPosition(int ... points)
    {
        for (int point : points)
        {
            if (point <= 0)
                return false;
        }

        return true;
    }
}
