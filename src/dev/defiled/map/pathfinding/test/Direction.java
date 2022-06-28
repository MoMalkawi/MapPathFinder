package dev.defiled.map.test;


public enum Direction {

    NORTH(0, 1),
    EAST(1, 0),
    SOUTH(0, -1),
    WEST(-1, 0),
    NORTH_EAST(1, 1),
    SOUTH_EAST(1, -1),
    NORTH_WEST(-1, 1),
    SOUTH_WEST(-1, -1);

    int x, y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public AStarNode getNeighbor(AStarNode node, ClientAStar c) {
        return c.loadGetNode(node.getX() + x, node.getY() + y, node.getPlane());
    }

    public boolean isValidDirection(AStarNode node, ClientAStar a) {
        AStarNode nodeN = a.loadGetNode(node.getX(), node.getY() + 1, node.getPlane());
        AStarNode nodeE = a.loadGetNode(node.getX() + 1, node.getY(), node.getPlane());
        AStarNode nodeW = a.loadGetNode(node.getX() - 1, node.getY(), node.getPlane());
        AStarNode nodeS = a.loadGetNode(node.getX(), node.getY() - 1, node.getPlane());
        try {
            switch (this) {
                case NORTH:
                    return !node.blockedNorth();
                case EAST:
                    return !node.blockedEast();
                case SOUTH:
                    return !node.blockedSouth();
                case WEST:
                    return !node.blockedWest();
                case NORTH_EAST:
                    if (node.blockedNorth() || node.blockedEast()) {
                        return false;
                    }
                    if (nodeE != null && !nodeE.isFlagWalkable()) {
                        return false;
                    }
                    if (nodeN != null && !nodeN.isFlagWalkable()) {
                        return false;
                    }
                    if (nodeE != null && nodeE.blockedNorth()) {
                        return false;
                    }
                    return nodeN != null && !nodeN.blockedEast();
                case NORTH_WEST:
                    if (node.blockedNorth() || node.blockedWest()) {
                        return false;
                    }
                    if (nodeW != null && !nodeW.isFlagWalkable()) {
                        return false;
                    }
                    if (nodeN != null && !nodeN.isFlagWalkable()) {
                        return false;
                    }
                    if (nodeW != null && nodeW.blockedNorth()) {
                        return false;
                    }
                    return nodeN != null && !nodeN.blockedWest();
                case SOUTH_EAST:
                    if (node.blockedSouth() || node.blockedEast()) {
                        return false;
                    }
                    if (nodeE != null && !nodeE.isFlagWalkable()) {
                        return false;
                    }
                    if (nodeS != null && !nodeS.isFlagWalkable()) {
                        return false;
                    }
                    if (nodeE != null && nodeE.blockedSouth()) {
                        return false;
                    }
                    return nodeS != null && !nodeS.blockedEast();
                case SOUTH_WEST:
                    if (node.blockedSouth() || node.blockedWest()) {
                        return false;
                    }
                    if (nodeW != null && !nodeW.isFlagWalkable()) {
                        return false;
                    }
                    if (nodeS != null && !nodeS.isFlagWalkable()) {
                        return false;
                    }
                    if (nodeW != null && nodeW.blockedSouth()) {
                        return false;
                    }
                    return nodeS != null && !nodeS.blockedWest();
                default:
                    return false;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
}
