package dev.defiled.map.test;

import dev.defiled.map.Data;
import dev.defiled.map.coordination.Tile;
import dev.defiled.map.test.AStarNode;
import dev.defiled.map.test.Direction;
import dev.defiled.map.frame.Markings;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ClientAStar {

    private Map<Integer, dev.defiled.map.test.AStarNode> nodes = new HashMap<>();

    private List<String> LOADED_REGIONS = new ArrayList<>();

    public static final String flagsFolder = "/Users/defiled/Desktop/Clients/Resources/flags/regionFlags/";

    private AStarNode target;
    private Tile start;

    public ClientAStar(Tile start, Tile target) {
        this.start = start;
        this.target = loadGetNode(target.getX(), target.getY(), target.getPlane());
    }

    public dev.defiled.map.test.AStarNode findPath() {
        AStarNode start = loadGetNode(this.start.getX(), this.start.getY(), this.start.getPlane());
        if (start != null && target != null) {
            nodes.clear();
            LOADED_REGIONS.clear();

            Queue<AStarNode> nodes = new LinkedList<>();
            Queue<AStarNode> furtherNodes = new LinkedList<>();

            start.setG(0);
            start.setH(start.calculateHeuristic(target));
            start.setVisited(true);
            nodes.add(start);

            AStarNode curr = null;

            while (!nodes.isEmpty()) {

                curr = nodes.poll();

                Markings.savedTiles.add(new Tile(curr.getX(), curr.getY()));

                if (curr.equals(target)) return curr;


                for (Direction d : Direction.values()) {

                    AStarNode neighbor;

                    if (!d.isValidDirection(curr, this) || (neighbor = d.getNeighbor(curr, this)) == null
                            || neighbor.isVisited()) continue;

                    neighbor.setG(curr.calculateMoveCost(neighbor));
                    neighbor.setH(neighbor.calculateHeuristic(target));
                    neighbor.setVisited(true);
                    neighbor.setPrev(curr);

                    if (neighbor.getF() <= curr.getF()) nodes.add(neighbor);
                    else furtherNodes.add(neighbor);

                    //If you remove the if-else statement and just have nodes.add(neighbor); it'll become Dijkstra
                }

                if (nodes.isEmpty() && !furtherNodes.isEmpty())
                    nodes.add(furtherNodes.poll());

            }

            return curr;
        }
        return null;
    }

    public AStarNode loadGetNode(int x, int y, int plane) {
        if (x > 1000 && y > 1000) {
            String regionFileName = getRegion63X(x) +
                    " " + getRegion63Y(y) +
                    " " + plane;
            if (LOADED_REGIONS.contains(regionFileName)) {
                int tileKey = Integer.parseInt(x + "" + y + "" + plane);
                if (nodes.containsKey(tileKey)) return nodes.get(tileKey);
            } else if (loadRegion(regionFileName)) return loadGetNode(x, y, plane);
        }
        return null;
    }

    private boolean loadRegion(String regionFileName) {
        File f = new File(flagsFolder + regionFileName);
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.isEmpty()) {
                        int x = Integer.parseInt(line.substring(0, 4));
                        int y = Integer.parseInt(line.substring(5, 9));
                        int plane = Integer.parseInt(regionFileName.substring(regionFileName.length() - 1));
                        nodes.put(Integer.parseInt(x + "" + y + "" + plane), new AStarNode(x, y, plane,
                                Integer.parseInt(line.substring(10)), this));
                    }
                }
                LOADED_REGIONS.add(regionFileName);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static int getRegion63X(int x) {
        return Data.START_TILE_MAP.getX() +
                (64 * ((x - Data.START_TILE_MAP.getX()) / 64));
    }

    public static int getRegion63Y(int y) {
        return Data.FIRST_BASE_Y - (64 * ((Data.START_TILE_MAP.getY() - y) / 64));
    }


    private double distance(int x1, int y1, int x2, int y2) {
        return new Point(x1, y1).distance(x2, y2);
    }

}
