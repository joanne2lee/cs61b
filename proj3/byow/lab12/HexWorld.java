package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);


    private static class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position shift(int sx, int sy) {
            return new Position(this.x + sx, this.y + sy);
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(3);
        switch (tileNum) {
            case 0: return Tileset.SAND;
            case 1: return Tileset.GRASS;
            case 2: return Tileset.FLOWER;
            case 3: return Tileset.TREE;
            case 4: return Tileset.MOUNTAIN;
            default: return Tileset.NOTHING;
        }
    }



    /**
     * Fills the given world with blank tiles.
     */
    public static void fillWithNothing(TETile[][] world) {
        int height = world[0].length;
        int width = world.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }


    /**
     * Draw a row of N tiles starting at the given position.
     */
    public static void drawRow(TETile[][] world, TETile tile, Position p, int n) {
        for (int i = 0; i < n; i++) {
            world[p.x + i][p.y] = tile;
        }
    }


    /**
     * Adds a hexagon of tile-type TYPE and size S to the world at position P.
     */
    public static void addHexagon(TETile[][] world, TETile type, Position p, int s) {
        // Cannot draw hexagons smaller than size 2
        if (s < 2) System.exit(0);

        addHexHelper(world, type, p, s - 1, s);
    }

    /**
     * Helper function using blank spaces B and tiles-to-draw T.
     */
    private static void addHexHelper(TETile[][] world, TETile type, Position p, int b, int t) {
        // Draw this row
        Position startOfRow = p.shift(b, 0);
        drawRow(world, type, startOfRow, t);

        // Draw remaining rows recursively
        if (b > 0) {
            Position nextRow = p.shift(0, -1);
            addHexHelper(world, type, nextRow, b - 1, t + 2);
        }

        // Draw row again (reflection)
        Position startOfReflectedRow = startOfRow.shift(0, -(2*b + 1));
        drawRow(world, type, startOfReflectedRow, t);
    }


    /**
     * Draws the hexagons.
     */
    public static void drawHexWorld(TETile[][] world) {
        fillWithNothing(world);
        Position p = new Position(20,20);
        addHexagon(world, randomTile(), p, 4);
    }



    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        drawHexWorld(world);

        ter.renderFrame(world);

    }
}
