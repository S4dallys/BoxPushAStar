import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;

public class BoxMove {

    char dir;
    Coord coord;

    // public static void main(String[] args) {
    //     try {

    //         MapImporter mi = MapImporter.getDataFromFile("threeboxes1");
    //         char[][] mapData = mi.mapData;
    //         char[][] itemsData = mi.itemsData;

    //         for (BoxMove bm : generateBoxMoves(mapData, itemsData)) {
    //             System.out.println(bm);
    //         }

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    public BoxMove(char dir, Coord coord) {
        this.dir = dir;
        this.coord = coord;
    }

    public static ArrayList<BoxMove> generateBoxMoves(char[][] mapData, char[][] itemsData, Coord player, Coord[] allBoxCoords, boolean[][] deadlock) {
        /// return this
        ArrayList<BoxMove> boxPushes = new ArrayList<>();

        // for each box coordinate
        // filter NWSE for either 
        // BLOCKED or opposite of a blocked tile
        HashMap<BoxMove, Coord> possibleBoxPushes = new HashMap<>();
        Coord c1, c2;

        for (Coord box : allBoxCoords) {
            Coord[] UDLR = box.getUDLRCoords();
            
            c1 = UDLR[0];
            c2 = UDLR[1];
            if (isOpen(mapData[c1.r][c1.c]) && isOpen(itemsData[c1.r][c1.c])
            && isOpen(mapData[c2.r][c2.c]) && isOpen(itemsData[c2.r][c2.c])) {
                if (!deadlock[c2.r][c2.c])
                    possibleBoxPushes.put(new BoxMove('d', box), c1);
                if (!deadlock[c1.r][c1.c])
                    possibleBoxPushes.put(new BoxMove('u', box), c2);
            }

            c1 = UDLR[2];
            c2 = UDLR[3];
            if (isOpen(mapData[c1.r][c1.c]) && isOpen(itemsData[c1.r][c1.c])
            && isOpen(mapData[c2.r][c2.c]) && isOpen(itemsData[c2.r][c2.c])) {
                if (!deadlock[c2.r][c2.c])
                    possibleBoxPushes.put(new BoxMove('r', box), c1);
                if (!deadlock[c1.r][c1.c])
                    possibleBoxPushes.put(new BoxMove('l', box), c2);
            }
        }

        // bfs to get all walkable coordinates
        ArrayDeque<Coord> queue = new ArrayDeque<>();
        HashSet<Coord> visited = new HashSet<>();

        queue.add(player);

        while (!queue.isEmpty()) {            
            Coord curr = queue.removeLast();
            visited.add(curr);

            for (Coord c : curr.getUDLRCoords()) {
                if (isOpen(mapData[c.r][c.c]) && isOpen(itemsData[c.r][c.c]) && !visited.contains(c))
                    queue.add(c);
            }
        }

        // filter box pushes if not reachable
        for (BoxMove bm : possibleBoxPushes.keySet()) {
            if (visited.contains(possibleBoxPushes.get(bm)))
                boxPushes.add(bm);
        }

        return boxPushes;
    }

    public static boolean isOpen(char c) {
        return (c != '$' && c != '#');
    }

    public static Coord getPlayerLocation(char[][] itemsData) {
        for (int i = 0; i < itemsData.length; i++) {
            for (int j = 0; j < itemsData[0].length; j++) {
                if (itemsData[i][j] == '@')
                    return new Coord(i, j);
            }
        }

        return null;
    }

    public static void getAllItemsCoordinates(char[][] mapData, char[][] itemsData, Coord[] boxes, Coord[] goals) {
        
        int b = 0;
        int g = 0;

        for (int i = 0; i < itemsData.length; i++) {
            for (int j = 0; j < itemsData[0].length; j++) {
                if (itemsData[i][j] == '$')
                    boxes[b++] = new Coord(i, j);
                if (mapData[i][j] == '.')
                    goals[g++] = new Coord(i, j);
            }
        }
    }

    public static int getNumberOfBoxes(char[][] itemsData) {
        int amt = 0;
        for (int i = 0; i < itemsData.length; i++) {
            for (int j = 0; j < itemsData[0].length; j++) {
                if (itemsData[i][j] == '$')
                    amt++;
            }
        }
        return amt;
    }

    public String toString() {
        return String.format("%s %c", coord.toString(), dir);
    }
}


