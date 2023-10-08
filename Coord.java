public class Coord {
    public int r;
    public int c;

    public Coord(int r, int c) {
        this.r = r;
        this.c = c;
    }

    public Coord(Coord c) {
        this.r = c.r;
        this.c = c.c;
    }

    public String toString() {
        return String.format("(%d, %d)", r, c);
    }

    @Override
    public boolean equals(Object e) {
        Coord c2 = (Coord) e;
        return r == c2.r && c == c2.c;
    }

    @Override
    public int hashCode() {
        int result = r;
        result = 31 * result + c;
        return result;
    }

    public Coord[] getUDLRCoords() {
        Coord[] coords = {
            new Coord(r - 1, c),
            new Coord(r + 1, c),
            new Coord(r, c - 1),
            new Coord(r, c + 1)
        };

        return coords;
    }
    public static int manhattanDist(Coord c0, Coord c1){
        return manhattanDist(c0.r, c0.c, c1.r, c1.c);
    }

    public static int manhattanDist(int x0, int y0, int x1, int y1){
        return  Math.abs(x1-x0) + Math.abs(y1-y0);
    }
}