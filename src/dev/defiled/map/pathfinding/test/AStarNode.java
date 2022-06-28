package dev.defiled.map.test;

import dev.defiled.map.pathfinding.data.Flags;

import java.awt.*;

public class AStarNode {

    private AStarNode prev;

    private static final double D2 = 1.4142135623730951;

    private float g; // distance or move-cost
    private int h; // heuristic

    private int x;
    private int y;
    private int plane;

    private int flag;

    private boolean visited;

    private ClientAStar cas;

    public AStarNode(int x, int y, int plane, int flag, ClientAStar cas) {
        this.x = x;
        this.y = y;
        this.plane = plane;
        this.flag = flag;
        this.cas = cas;
    }

    public double distanceTo(AStarNode node) {
        return distanceTo(node.x, node.y);
    }

    public double distanceTo(int dx, int dy) {
        return new Point(x, y).distance(dx, dy);
    }

    public float calculateMoveCost(AStarNode aStarNode) {
        /*int moveCost = (int)(Math.sqrt(Math.pow((aStarNode.x - x) * 10, 2) + (Math.pow((aStarNode.y - y) * 10, 2))));
        if (prev != null){
            moveCost += prev.getG();
        }
        return moveCost;*/
        return calculateMoveCost(aStarNode.x, aStarNode.y);
    }

    public float calculateMoveCost(int nx, int ny) {
        float moveCost = (float) Math.sqrt(Math.pow((nx - x), 2) + (Math.pow((ny - y), 2)));
        //int moveCost = (int) distanceTo(nx,ny);
        if (prev != null) {
            moveCost += prev.getG();
        }
        return moveCost;
    }

    public int calculateHeuristic(AStarNode destination) {
        /*return (Math.abs(x-destination.x) + Math.abs(y-destination.y)) * 10;*/
        return calculateHeuristic(destination.x, destination.y);
    }

    public int calculateHeuristic(int nx, int ny) {
        int dx = Math.abs(x - nx);
        int dy = Math.abs(y - ny);
        return (int) (Math.max(dx, dy) + (D2 - 1) * Math.min(dx, dy));
    }

    public boolean blockedNorth() {
        return Flags.checkFlag(flag, Flags.NORTH)
                || Flags.checkFlag(flag, Flags.BLOCKED_NORTH_WALL);
    }

    public boolean blockedEast() {
        return Flags.checkFlag(flag, Flags.EAST)
                || Flags.checkFlag(flag, Flags.BLOCKED_EAST_WALL);
    }

    public boolean blockedSouth() {
        return Flags.checkFlag(flag, Flags.SOUTH)
                || Flags.checkFlag(flag, Flags.BLOCKED_SOUTH_WALL);
    }

    public boolean blockedWest() {
        return Flags.checkFlag(flag, Flags.WEST)
                || Flags.checkFlag(flag, Flags.BLOCKED_WEST_WALL);
    }

    public boolean isFlagWalkable() {
        return !(Flags.checkFlag(flag, Flags.OCCUPIED)
                || Flags.checkFlag(flag, Flags.SOLID)
                || Flags.checkFlag(flag, Flags.BLOCKED)
                || Flags.checkFlag(flag, Flags.CLOSED));
    }

    public AStarNode getPrev() {
        return prev;
    }

    public float getG() {
        return g;
    }

    public float getF() {
        return g + h;
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

    public int getH() {
        return h;
    }

    public void setPrev(AStarNode prev) {
        this.prev = prev;
    }

    public void setG(float g) {
        this.g = g;
    }

    public void setH(int h) {
        this.h = h;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean equals(Object o) {
        if (o instanceof AStarNode) {
            AStarNode node = (AStarNode) o;
            return node.x == x && node.y == y && node.plane == plane;
        }
        return false;
    }

    public String toString() {
        return "X: " + x + " Y: " + y + " Plane: " + plane + " G: " + g + " H: " + h;
    }

}
