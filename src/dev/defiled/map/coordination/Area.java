package dev.defiled.map.coordination;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Area {

    private Tile t1;
    private Tile t2;

    private List<Tile> tiles = new ArrayList<>();

    public Area(Tile t1, Tile t2, int deviation) {
        this.t1 = t1;
        this.t2 = t2;
        generateTiles(deviation);
    }

    public Area(int x, int y, int x1, int y1) {
        this.t1 = new Tile(x, y);
        this.t2 = new Tile(x1, y1);
    }

    public void generateTiles(int deviation) {
        Tile NW;
        Tile SE;
        boolean reverse = false;
        if (t1.getY() > t2.getY()) {
            NW = t1;
            SE = t2;
        } else {
            NW = t2;
            SE = t1;
            reverse = true;
        }
        for (int i = NW.getX() + (-deviation); i <= SE.getX() + deviation; i++) {
            for (int j = SE.getY() + (-deviation); j <= NW.getY() + deviation; j++) tiles.add(new Tile(i, j));
        }
        if (reverse) Collections.reverse(tiles);
    }

    public static Pair<Tile, Tile> sorted(Tile t1, Tile t2) {
        Tile NW;
        Tile SE;
        if (t1.getY() > t2.getY()) {
            NW = t1;
            SE = t2;
        } else {
            NW = t2;
            SE = t1;
        }
        return new Pair<>(NW, SE);
    }

    public Tile getT1() {
        return t1;
    }

    public Tile getT2() {
        return t2;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    @Override
    public String toString() {
        return "Area [ " + t1.toString() + " ] [ " + t2.toString() + " ]";
    }

    @Override
    public boolean equals(Object ob) {
        if (ob instanceof Area) {
            Area a = (Area) ob;
            return (a.t1 == t1 && a.t2 == t2) || (a.t1 == t2 && a.t2 == t1);
        }
        return false;
    }
}
