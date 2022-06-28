package dev.defiled.map.frame;

import dev.defiled.map.Data;
import dev.defiled.map.coordination.Area;
import dev.defiled.map.coordination.Tile;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Markings {

    public static List<Tile> currentlyMarked = new ArrayList<>();

    public static List<Area> currentlyMarkedAreas = new ArrayList<>();

    public static List<Tile> savedTiles = new ArrayList<>();

    public static Point current = null;

    private MapFrame frame;

    public Markings(MapFrame frame) {
        this.frame = frame;
    }

    public static void mark(int x, int y) {
        Tile p = new Tile(x, y);
        currentlyMarked.add(p);
    }

    public void clearAll() {
        currentlyMarkedAreas.clear();
        currentlyMarked.clear();
    }

    public void highlightHovered(Graphics2D g) {
        if (current != null) {
            Tile c = tileFromPoint(current);
            g.setColor(Color.CYAN);
            g.fillRect(c.getPositionOnMap().x, c.getPositionOnMap().y, Data.TILE_SIZE, Data.TILE_SIZE);
        }
    }

    public static Tile tileFromPoint(Point p) {
        return new Tile((p.x / Data.TILE_SIZE) + Data.START_TILE_MAP.getX(),
                Data.START_TILE_MAP.getY() - (p.y / Data.TILE_SIZE));
    }
}
