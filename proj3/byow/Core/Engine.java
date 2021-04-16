package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 100;
    public static final int HEIGHT = 50;
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        long seed = Long.parseLong(input.substring(1, input.length() - 1));
        Random random = new Random(seed);
        return generateWorld(random);
    }

    public static TETile[][] generateWorld(Random random) {
        TETile[][] world = initializeTiles();
        Position p = new byow.Core.Engine.Position(RandomUtils.uniform(random,
                WIDTH / 2 - 5, WIDTH / 2 + 5), RandomUtils.uniform(random,
                HEIGHT / 2 - 5, HEIGHT / 2 + 5));
        Room r = new byow.Core.Engine.Room(p, getRandomRoomLength(random),
                getRandomRoomLength(random));
        drawRandomRoom(world, r, random);
        fillWalls(world);
        return world;
    }





    /**
     * Draws rectangular region on array of TETiles. Position p is the bottom
     * left wall coordinate point, x_len is the width of the region, y_len is the
     * height of the region. Total area drawn by one call of drawRoom is x_len + 2
     * by y_len + 2, starting at (p.x, p.y) as bottom left.
     */
    public static void drawRoom(TETile[][] tiles, byow.Core.Engine.Room r, Random random) {
        for (int i = 0; i < r.width; i += 1) {
            for (int j = 0; j < r.height; j += 1) {
                tiles[r.p.x + i][r.p.y + j] = Tileset.FLOOR;
            }
        }
    }

    /** Examples of what can be done so far. Currently where testing is happening.*/
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = initializeTiles();
        Random random = new Random();
        byow.Core.Engine.Position p = new byow.Core.Engine.Position(RandomUtils.uniform(random,
                WIDTH / 2 - 5, WIDTH / 2 + 5), RandomUtils.uniform(random,
                HEIGHT / 2 - 5, HEIGHT / 2 + 5));
        byow.Core.Engine.Room r = new byow.Core.Engine.Room(p, getRandomRoomLength(random),
                getRandomRoomLength(random));
        drawRandomRoom(world, r, random);
        fillWalls(world);
        ter.renderFrame(world);
    }

    public static void fillWalls(TETile[][] tiles) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                if (tiles[x][y].equals(Tileset.NOTHING) && nearFloor(tiles, x, y)) {
                    tiles[x][y] = Tileset.WALL;
                }
            }
        }
    }

    public static boolean nearFloor(TETile[][] tiles, int x, int y) {
        if (x - 1 > 0) {
            if (tiles[x - 1][y].equals(Tileset.FLOOR)) {
                return true;
            }
            if (y - 1 > 0 && tiles[x - 1][y - 1].equals(Tileset.FLOOR)) {
                return true;
            }
            if (y + 1 < HEIGHT && tiles[x - 1][y + 1].equals(Tileset.FLOOR)) {
                return true;
            }
        }
        if (x + 1 < WIDTH) {
            if (tiles[x + 1][y].equals(Tileset.FLOOR)) {
                return true;
            }
            if (y - 1 > 0 && tiles[x + 1][y - 1].equals(Tileset.FLOOR)) {
                return true;
            }
            if (y + 1 < HEIGHT && tiles[x + 1][y + 1].equals(Tileset.FLOOR)) {
                return true;
            }
        }
        if (y + 1 < HEIGHT && tiles[x][y + 1].equals(Tileset.FLOOR)) {
            return true;
        }
        if (y - 1 > 0 && tiles[x][y - 1].equals(Tileset.FLOOR)) {
            return true;
        }
        return false;
    }

    /** Initializes the tiles to all contain Tileset.NOTHING. Helps with cleaning up main method. */
    public static TETile[][] initializeTiles() {
        TETile[][] tiles = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        return tiles;
    }

    public static void drawRandomRoom(TETile[][] tiles, byow.Core.Engine.Room r, Random random) {
        drawRoom(tiles, r, random);
        ArrayList<byow.Core.Engine.Room> surround = new ArrayList<>();
        if (RandomUtils.uniform(random) > 0.1) {
            surround.add(getTopNeighbor(r, 1, getRandomHallwayLength(random),
                    RandomUtils.uniform(random, 1, r.width - 1)));
        }
        if (RandomUtils.uniform(random) > 0.1) {
            surround.add(getRightNeighbor(r, getRandomHallwayLength(random), 1,
                    RandomUtils.uniform(random, 1, r.height - 1)));
        }
        if (RandomUtils.uniform(random) > 0.2) {
            surround.add(getBottomNeighbor(r, 1, getRandomHallwayLength(random),
                    RandomUtils.uniform(random, 1, r.width - 1)));
        }
        if (RandomUtils.uniform(random) > 0.2) {
            surround.add(getLeftNeighbor(r, getRandomHallwayLength(random), 1,
                    RandomUtils.uniform(random, 1, r.height - 1)));
        }
        for (byow.Core.Engine.Room room: surround) {
            if (!cannotDraw(tiles, room)) {
                drawRandomHallway(tiles, room, random);
            }
        }
    }

    public static void drawRandomHallway(TETile[][] tiles, byow.Core.Engine.Room h, Random random) {
        drawRoom(tiles, h, random);
        byow.Core.Engine.Room room1;
        byow.Core.Engine.Room room2;
        byow.Core.Engine.Room h1;
        byow.Core.Engine.Room h2;
        if (h.width < h.height) {
            room1 = getTopNeighbor(h, getRandomRoomLength(random), getRandomRoomLength(random),
                    RandomUtils.uniform(random, -4, 1));
            room2 = getBottomNeighbor(h, getRandomRoomLength(random), getRandomRoomLength(random),
                    RandomUtils.uniform(random, -4, 1));
            h1 = getLeftNeighbor(h, getRandomHallwayLength(random) / 2, 1, 0);
            h2 = getRightNeighbor(h, getRandomHallwayLength(random) / 2, 1, 0);
        } else {
            room1 = getLeftNeighbor(h, getRandomRoomLength(random), getRandomRoomLength(random),
                    RandomUtils.uniform(random, -4, 1));
            room2 = getRightNeighbor(h, getRandomRoomLength(random), getRandomRoomLength(random),
                    RandomUtils.uniform(random, -4, 1));
            h1 = getBottomNeighbor(h, 1, getRandomHallwayLength(random) / 2, 0);
            h2 = getTopNeighbor(h, 1, getRandomHallwayLength(random) / 2, 0);
        }
        if (RandomUtils.uniform(random) > 0.6) {
            if (!cannotDraw(tiles, h1)) {
                drawRandomHallway(tiles, h1, random);
            } else {
                if (!cannotDraw(tiles, room1)) {
                    drawRandomRoom(tiles, room1, random);
                }
            }
        } else {
            if (!cannotDraw(tiles, room1)) {
                drawRandomRoom(tiles, room1, random);
            } else {
                if (!cannotDraw(tiles, h1)) {
                    drawRandomHallway(tiles, h1, random);
                }
            }
        }
        if (RandomUtils.uniform(random) > 0.6) {
            if (!cannotDraw(tiles, h1)) {
                drawRandomHallway(tiles, h2, random);
            } else {
                if (!cannotDraw(tiles, room2)) {
                    drawRandomRoom(tiles, room2, random);
                }
            }
        } else {
            if (!cannotDraw(tiles, room2)) {
                drawRandomRoom(tiles, room2, random);
            } else {
                if (!cannotDraw(tiles, h1)) {
                    drawRandomHallway(tiles, h2, random);
                }
            }
        }
    }

    /** Random room length, from 3 to 8. */
    public static int getRandomRoomLength(Random r) {
        return RandomUtils.uniform(r, 6, 12);
    }

    /** Random hallway length, from 4 to 10. */
    public static int getRandomHallwayLength(Random r) {
        return RandomUtils.uniform(r, 6, 15);
    }

    private static class Position {
        int x;
        int y;
        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        byow.Core.Engine.Position shift(int xLen, int yLen) {
            return new byow.Core.Engine.Position(this.x + xLen, this.y + yLen);
        }
    }

    /**
     * Private class to contain rectangular region. Position p is the
     * coordinate of the bottom left wall.
     */
    private static class Room {
        byow.Core.Engine.Position p;
        int width;
        int height;

        Room(byow.Core.Engine.Position p, int width, int height) {
            this.p = p;
            this.width = width;
            this.height = height;
        }
    }

    /** Returns true if cannot draw room. */
    public static boolean cannotDraw(TETile[][] tiles, byow.Core.Engine.Room r) {
        return isOutOfBounds(r) || conflictDraw(tiles, r);
    }

    /**
     * Checks if current position is out of bounds or would result in
     * out of bounds with given x_len and y_len.
     */
    public static boolean isOutOfBounds(byow.Core.Engine.Room r) {
        return r.p.x <= 1 || r.p.y <= 1 || r.p.x >= WIDTH - 1 || r.p.y >= HEIGHT - 1
                || r.p.x + r.width >= WIDTH - 1 || r.p.y + r.height >= HEIGHT - 1;
    }

    /** Checks if draw would result in overlap. */
    public static boolean conflictDraw(TETile[][] tiles, byow.Core.Engine.Room r) {
        for (int x = 0; x < r.width; x += 1) {
            for (int y = 0; y < r.height; y += 1) {
                if (!tiles[r.p.x + x][r.p.y + y].equals(Tileset.NOTHING)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static byow.Core.Engine.Room getRightNeighbor(byow.Core.Engine.Room r, int width, int height, int offset) {
        byow.Core.Engine.Position newP =  r.p.shift(r.width, offset);
        return new byow.Core.Engine.Room(newP, width, height);
    }

    public static byow.Core.Engine.Room getTopNeighbor(byow.Core.Engine.Room r, int width, int height, int offset) {
        byow.Core.Engine.Position newP =  r.p.shift(offset, r.height);
        return new byow.Core.Engine.Room(newP, width, height);
    }

    public static byow.Core.Engine.Room getLeftNeighbor(byow.Core.Engine.Room r, int width, int height, int offset) {
        byow.Core.Engine.Position newP = r.p.shift(-width, offset);
        return new byow.Core.Engine.Room(newP, width, height);
    }

    public static byow.Core.Engine.Room getBottomNeighbor(byow.Core.Engine.Room r, int width, int height, int offset) {
        byow.Core.Engine.Position newP = r.p.shift(offset, -height);
        return new byow.Core.Engine.Room(newP, width, height);
    }

}

