import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Comparator;

public class Solver {
    public static void main(String[] args) {
        try {
            MapImporter mi = MapImporter.getDataFromFile("fourboxes2");

            char[][] mapData = mi.mapData;
            char[][] itemsData = mi.itemsData;

            for (BoxMove bm : AStar(mapData, itemsData)) {
                System.out.println(bm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<BoxMove> AStar(char[][] mapData, char[][] itemsData) {
        // create root node ready to branch out
        Coord player = BoxMove.getPlayerLocation(itemsData);
        int nBoxes = BoxMove.getNumberOfBoxes(itemsData);
        Coord[] boxes = new Coord[nBoxes];
        Coord[] goals = new Coord[nBoxes];
        BoxMove.getAllItemsCoordinates(mapData, itemsData, boxes, goals);
        Node root = new Node (
            boxes, null, new BoxMove(' ', player), 
            itemsData, 0, HFunction(boxes, goals)
        );

        // important colelctions and lists
        PriorityQueue<Node> open = new PriorityQueue<>(new NodeComparator());
        HashSet<Node> closed = new HashSet<>();
        ArrayList<BoxMove> backtrack = new ArrayList<>();

        open.add(root);

        while (!open.isEmpty()) {
            Node current = open.poll();
            closed.add(current);

            // check if goal node? might not work
            // backtracks so sequence of boxmoves is in reverse order
            // current.hCost == 0 ??
            if (current.hCost == 0) {
                while (current.gCost != 0) {
                    backtrack.add(current.move);
                    current = current.parent;
                }

                return backtrack;
            }

            // generate children
            for (BoxMove move : BoxMove.generateBoxMoves(mapData, current.itemsData, current.move.coord, current.boxes)) {
                char[][] newState = newState(current.itemsData, move);
                Coord[] newBoxes = new Coord[boxes.length];
                Coord prevBox = move.coord;
                Coord boxMoved = null;
                for (int i = 0; i < boxes.length; i++) {
                    newBoxes[i] = new Coord(current.boxes[i]);

                    if (newBoxes[i].equals(prevBox)) {
                        switch (move.dir) {
                            case 'u':
                            newBoxes[i].r -= 1;
                            break;
                            case 'd':
                            newBoxes[i].r += 1;
                            break;
                            case 'l':
                            newBoxes[i].c -= 1;
                            break;
                            case 'r':
                            newBoxes[i].c += 1;
                            break;
                        }

                        boxMoved = newBoxes[i];
                    }
                }

                Node child = new Node(newBoxes, current, move, newState, current.gCost + 1, HFunction(newBoxes, goals));

                // see if child is open or closed already
                if (open.contains(child) || closed.contains(child)) {
                    continue;
                }

                open.add(child);
            }
        }

        return backtrack;
    }

    private static int HFunction(Coord[] boxes, Coord[] goals) {
        //replace by passing list of items and list and maps
        int sum =0;
        for (Coord box : boxes) {
            int lowest = Coord.manhattanDist(box, goals[0]);
            for (Coord goal : goals) {
                int cur = Coord.manhattanDist(box, goal);
                if(cur<lowest)lowest=cur;
            }
            sum+=lowest;
        }


        return sum;
    }

    private static char[][] newState(char[][] previousState, BoxMove move) {
        char[][] newState = new char[previousState.length][previousState[0].length];

        for (int i = 0; i < previousState.length; i++) {
            newState[i] = previousState[i].clone();
        }

        newState[move.coord.r][move.coord.c] = '@';
        
        switch (move.dir) {
            case 'u':
            newState[move.coord.r - 1][move.coord.c] = '$';
            break;
            case 'd':
            newState[move.coord.r + 1][move.coord.c] = '$';
            break;
            case 'l':
            newState[move.coord.r][move.coord.c - 1] = '$';
            break;
            case 'r':
            newState[move.coord.r][move.coord.c + 1] = '$';
            break;
        }

        return newState;
    }

    public static boolean isGoal(char[][] mapData, Node n) {
        for (int i = 0; i < n.itemsData.length; i++) {
            for (int j = 0; j < n.itemsData[0].length; j++) {
                if (n.itemsData[i][j] == '$' && mapData[i][j] != '.') {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isDeadlock(char[][] mapData, char[][] itemsData, Coord box, Coord ignore, int depth) {
        if (depth == 3) return false;

        Coord[] dirs = box.getUDLRCoords();

        Coord temp = dirs[1];
        dirs[1] = dirs[2];
        dirs[2] = temp;

        boolean[] dirBlocked = new boolean[5];
        int dbInd = 0;

        for (Coord dir : dirs) {
            char md = mapData[dir.r][dir.c];
            char id = itemsData[dir.r][dir.c];
            if (dir.equals(ignore) || md == '#') {
                dirBlocked[dbInd] = false;
            }
            else if (id == '$') {
                dirBlocked[dbInd] = isDeadlock(mapData, itemsData, dir, box, depth + 1);
            } else {
                dirBlocked[dbInd] = true;
            }
            dbInd++;
        }

        dirBlocked[dbInd++] = dirBlocked[0];

        int consecutiveFalses = 0;
        
        for (boolean b : dirBlocked) {
            if (b == false) consecutiveFalses++;
            else consecutiveFalses = 0;

            if (consecutiveFalses >= 2) {
                return true;
            }
        }

        return false;
    }

    public static class Node {
        public Coord[] boxes;
        public Node parent;
        public BoxMove move;
        public char[][] itemsData;
        public int gCost;
        public int hCost;

        public Node(Coord[] boxes, Node parent, BoxMove move, char[][] itemsData, int gCost, int hCost) {
            this.boxes = boxes;
            this.parent = parent;
            this.move = move;
            this.itemsData = itemsData;
            this.gCost = gCost;
            this.hCost = hCost;
        }

        public int getFCost() {
            return gCost + hCost;
        }

        @Override
        public boolean equals(Object e) {
            Node n2 = (Node) e;

            for (int i = 0; i < itemsData.length; i++) {
                if (!Arrays.equals(itemsData[i], n2.itemsData[i]) || move.dir != n2.move.dir)
                    return false;
            }


            return true;
        }

        @Override
        public int hashCode() {
            return 31 + 7 * Arrays.deepHashCode(itemsData) + 11 * Character.hashCode(move.dir);
        }
    }

    public static class NodeComparator implements Comparator<Node> {
        public int compare(Node n1, Node n2) {
            return n1.getFCost() - n2.getFCost();
        }
    }
}
