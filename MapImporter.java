import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class MapImporter {

    public char[][] mapData;
    public char[][] itemsData;

    private MapImporter(char[][] mapData, char[][] itemsData) {
        this.mapData = mapData;
        this.itemsData = itemsData;
    }

    public static MapImporter getDataFromFile(String mapName) throws FileNotFoundException {
        File mapFile = new File("./maps/" + mapName + ".txt");
        Scanner sc = new Scanner(mapFile);
        ArrayList<String> rows = new ArrayList<>();
        char[][][] data;
        int maxColsLen = 0;
        int maxRowsLen = 0;

        while (sc.hasNextLine()) {
            String currentRow = sc.nextLine();
            if (currentRow.length() > maxColsLen) 
                maxColsLen = currentRow.length();

            rows.add(currentRow);
            maxRowsLen++;
        }

        data = new char[2][maxRowsLen][maxColsLen];

        for (int i = 0; i < maxRowsLen; i++) {
            for (int j = 0 ; j < maxColsLen; j++) {
                data[0][i][j] = ' ';
                data[1][i][j] = ' ';
            }
        }

        for (int i = 0; i < maxRowsLen; i++) {
            String currentRow = rows.get(i);
            for (int j = 0 ; j < currentRow.length(); j++) {
                char tile = currentRow.charAt(j);
                if (tile == '#' || tile == '.')
                    data[0][i][j] = tile;
                else if (tile == '@' || tile == '$')
                    data[1][i][j] = tile;
                else if (tile == '+') {
                    data[0][i][j] = '.';
                    data[1][i][j] = '@';
                }
                else if (tile == '*') {
                    data[0][i][j] = '.';
                    data[1][i][j] = '$';
                }
            }
        }

        sc.close();
        return new MapImporter(data[0], data[1]);
    }
}