package dev.defiled.map.coordination;

import dev.defiled.map.Data;

import java.awt.*;

public class Tile {

    private int x;
    private int y;
    private final int plane = 0;

    private Point positionOnMap = null;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point getPositionOnMap() {
        if (positionOnMap != null) return positionOnMap;
        int xDistance = x - Data.START_TILE_MAP.x;
        int yDistance = Math.abs(Data.START_TILE_MAP.y - y); //67
        return new Point(xDistance * Data.TILE_SIZE, (yDistance * Data.TILE_SIZE));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPlane() {
        return plane;
    }

    @Override
    public String toString() {
        return "Tile [ X: " + x + " Y: " + y + " pX: " + getPositionOnMap().x + " pY: " + getPositionOnMap().y + " ]";
    }

    @Override
    public boolean equals(Object ob) {
        if (ob instanceof Tile) {
            Tile t = (Tile) ob;
            return t.x == x && t.y == y;
        }
        return false;
    }

}
