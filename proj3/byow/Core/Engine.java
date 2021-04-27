package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.Font;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;


public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 100;
    public static final int HEIGHT = 50;
    public static final ArrayList<Room> ROOMS = new ArrayList<>();
    Player p1;
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        mainMenu();
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
        input = input.toUpperCase();
        TETile[][] world = null;

        if (input.charAt(0) == 'N') {
            int endOfSeed = input.indexOf('S');
            Long seed = Long.parseLong(input.substring(1, endOfSeed));
            Random r = new Random(seed);
            world = generateWorld(r);
            saveSeed(input.substring(1, endOfSeed));
            Room start = ROOMS.get(0);
            Position startPos = new Position(start.p.x + RandomUtils.uniform(r, start.width),
                    start.p.y + RandomUtils.uniform(r, start.height));
            p1 = new Player(startPos, Tileset.AVATAR, world);
            char[] moves = input.substring(endOfSeed + 1).toCharArray();
            for (char m : moves) {
                if (m == ':') {
                    int quit = input.indexOf(':');
                    if (input.substring(quit).length() > 3) {
                        world = interactWithInputString(input.substring(quit + 2));
                    }
                    break;
                }
                p1.move(m, world);
            }
            savePosition(p1.position);

        } else if (input.charAt(0) == 'L') {
            world = loadWorld();
            p1 = loadPlayer(world);
            char[] moves = input.substring(1).toCharArray();
            for (char m : moves) {
                if (m == ':') {
                    break;
                }
                p1.move(m, world);
            }
            savePosition(p1.position);
        }
        return world;
    }


    public void saveSeed(String seed) {
        File savedWorld = new File("./savedWorld.txt");
        if (!savedWorld.exists()) {
            try {
                savedWorld.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Utils.writeContents(savedWorld, seed);
    }


    public void savePosition(Position pos) {
        File savedPosition = new File("./savedPosition.txt");
        if (!savedPosition.exists()) {
            try {
                savedPosition.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Utils.writeObject(savedPosition, pos);
    }

    public TETile[][] loadWorld() {
        File savedWorld = new File("./savedWorld.txt");
        if (!savedWorld.exists()) {
            System.exit(0);
        }
        String seed = Utils.readContentsAsString(savedWorld);
        Long s = Long.parseLong(seed);
        Random r = new Random(s);
        TETile[][] world = generateWorld(r);
        return world;
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(100, 50);
        Engine e = new Engine();
        TETile[][] world = e.interactWithInputString("n7142401535173564015sadaadsddsswsa");
        ter.renderFrame(world);

        // Engine e = new Engine();
        // e.interactWithKeyboard();
    }

    /** New game option, with ability to enter seed. */
    public void newGame() {
        String seed = "";
        boolean start = false;
        Font font = new Font("AvantGarde", Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.enableDoubleBuffering();
        Font sub = new Font("AvantGarde", Font.BOLD, 30);
        StdDraw.setFont(sub);
        StdDraw.text(400, 450, "Enter a seed. Type S to finish");
        StdDraw.text(400, 420, "seed and start game.");
        StdDraw.text(400, 350, seed);
        StdDraw.show();
        while (!start) {
            if (StdDraw.hasNextKeyTyped()) {
                char dig = StdDraw.nextKeyTyped();
                if (isNumber(dig)) {
                    seed += dig;
                    StdDraw.clear(Color.BLACK);
                    StdDraw.text(400, 450, "Enter a seed. Type S to finish");
                    StdDraw.text(400, 420, "seed and start game.");
                    StdDraw.text(400, 350, seed);
                    StdDraw.show();
                } else if (Character.toUpperCase(dig) == 'S') {
                    saveSeed(seed);
                    start = true;
                    namePlayer();
                    createGame(Long.parseLong(seed));
                }
            }
        }
    }


    public void namePlayer() {
        String name = "";
        boolean start = false;
        Font font = new Font("AvantGarde", Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.enableDoubleBuffering();
        Font sub = new Font("AvantGarde", Font.BOLD, 30);
        StdDraw.setFont(sub);
        StdDraw.text(400, 450, "Enter player name followed by a comma.");
        StdDraw.text(400, 350, name);
        StdDraw.show();
        while (!start) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c != ',') {
                    name += c;
                    StdDraw.clear(Color.BLACK);
                    StdDraw.text(400, 450, "Enter player name followed by a comma.");
                    StdDraw.text(400, 350, name);
                    StdDraw.show();
                } else {
                    saveName(name);
                    start = true;
                }
            }
        }
    }

    public void saveName(String name) {
        File savedName = new File("./savedName.txt");
        if (!savedName.exists()) {
            try {
                savedName.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Utils.writeContents(savedName, name);
    }





    public Player loadPlayer(TETile[][] world) {
        File savedPosition = new File("./savedPosition.txt");
        Position oldPos = Utils.readObject(savedPosition, Position.class);
        return new Player(oldPos, Tileset.AVATAR, world);
    }



    /** Creates and runs game. */
    public void createGame(long l) {
        ter.initialize(WIDTH, HEIGHT);
        Random r = new Random(l);
        TETile[][] world = generateWorld(r);
        if (ROOMS.size() < 12) {
            ROOMS.removeAll(ROOMS);
            createGame(l + 1);

        } else {
            Room start = ROOMS.get(0);
            Position startingPosition = new Position(start.p.x + RandomUtils.uniform(r,
                    start.width), start.p.y + RandomUtils.uniform(r, start.height));
            p1 = new Player(startingPosition, Tileset.AVATAR, world);
            ter.renderFrame(world);
            runGame(world);
        }
    }

    public void runGame(TETile[][] world) {
        boolean fieldOfViewTurnedOn = true;
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(fieldOfView(world));
        double currX = StdDraw.mouseX();
        double currY = StdDraw.mouseY();
        String message = "";
        if (currX < WIDTH && currY < HEIGHT) {
            message = world[(int) currX][(int) currY].description();
        }
        StdDraw.setPenColor(StdDraw.WHITE);
        headsUpDisplay(message);
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File("calm.wav")));
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == ':') {
                    boolean wait = true;
                    while (wait) {
                        if (StdDraw.hasNextKeyTyped()) {
                            if (Character.toUpperCase(StdDraw.nextKeyTyped()) == 'Q') {
                                System.exit(0);
                            } else {
                                wait = false;
                            }
                        }
                    }
                }
                if (Character.toUpperCase(c) == 'T') {
                    fieldOfViewTurnedOn = !fieldOfViewTurnedOn;
                }
                p1.move(c, world);
                if (fieldOfViewTurnedOn) {
                    ter.renderFrame(fieldOfView(world));
                } else {
                    ter.renderFrame(world);
                }
                headsUpDisplay(message);
            }
            if (StdDraw.mouseX() != currX || StdDraw.mouseY() != currY
                    && StdDraw.mouseX() < WIDTH && StdDraw.mouseY() < HEIGHT) {
                currX = StdDraw.mouseX();
                currY = StdDraw.mouseY();
                message = world[(int) currX][(int) currY].description();
                headsUpDisplay(message);
            }
        }
    }

    public TETile[][] fieldOfView(TETile[][] world) {
        TETile[][] field = initializeTiles();
        Position p = p1.position;
        for (int x = 0; x < 7; x += 1) {
            for (int y = 0; y < 7; y += 1) {
                if (validLoc(p.x - 3 + x, p.y - 3 + y)) {
                    field[p.x - 3 + x][p.y - 3 + y] = world[p.x - 3 + x][p.y - 3 + y];
                }
            }
        }
        return field;
    }

    public void headsUpDisplay(String message) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(5, HEIGHT - 1.7, 5, 1.3);
        File savedName = new File("./savedName.txt");
        String name = Utils.readContentsAsString(savedName);
        StdDraw.filledRectangle(30, HEIGHT - 0.5, 15, 1.1);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(30, HEIGHT - 1, name);
        StdDraw.text(4, HEIGHT - 1, message);
        StdDraw.line(0, HEIGHT - 2, WIDTH, HEIGHT - 2);
        StdDraw.show();
    }

    /** Checks if input is number. */
    public static boolean isNumber(Character dig) {
        for (Integer i = 0; i < 10; i += 1) {
            if (dig.toString().equals(i.toString())) {
                return true;
            }
        }
        return false;
    }

    /** Main menu of game. */
    public void mainMenu() {
        StdDraw.setCanvasSize(800, 800);
        Font font = new Font("AvantGarde", Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, 800);
        StdDraw.setYscale(0, 800);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.enableDoubleBuffering();
        String title = "The Mountains are Calling";
        StdDraw.text(400, 600, title);
        Font sub = new Font("AvantGarde", Font.BOLD, 30);
        StdDraw.setFont(sub);
        StdDraw.text(400, 400, "New Game (N)");
        StdDraw.text(400, 350, "Load Game (L)");
        StdDraw.text(400, 300, "Quit (Q)");
        StdDraw.show();
        boolean optionChosen = false;
        while (!optionChosen) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.toUpperCase(c) == 'N') {
                    optionChosen = true;
                    newGame();
                } else if (Character.toUpperCase(c) == 'L') {
                    TETile[][] world = loadWorld();
                    p1 = loadPlayer(world);
                    runGame(world);
                } else if (Character.toUpperCase(c) == 'Q') {
                    System.exit(0);
                }
            }
        }
    }

    /** Generate world at beginning of game. */
    public static TETile[][] generateWorld(Random random) {
        TETile[][] world = initializeTiles();
        Position p = new Position(RandomUtils.uniform(random,
                WIDTH / 2 - 5, WIDTH / 2 + 5), RandomUtils.uniform(random,
                HEIGHT / 2 - 5, HEIGHT / 2 + 5));
        Room r = new Room(p, getRandomRoomLength(random),
                getRandomRoomLength(random));
        drawRandomRoom(world, r, random);
        fillWalls(world, random);
        return world;
    }

    public static boolean validLoc(int x, int y) {
        return x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT;
    }

    /**
     * Draws rectangular region on array of TETiles. Position p is the bottom
     * left wall coordinate point, x_len is the width of the region, y_len is the
     * height of the region. Total area drawn by one call of drawRoom is x_len + 2
     * by y_len + 2, starting at (p.x, p.y) as bottom left.
     */
    public static void drawRoom(TETile[][] tiles, Room r, Random random) {
        for (int i = 0; i < r.width; i += 1) {
            for (int j = 0; j < r.height; j += 1) {
                tiles[r.p.x + i][r.p.y + j] = Tileset.FLOOR;
            }
        }
    }

    /** Fills walls at world generation. */
    public static void fillWalls(TETile[][] tiles, Random r) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                if (tiles[x][y].equals(Tileset.NOTHING) && nearFloor(tiles, x, y)) {
                    tiles[x][y] = Tileset.MOUNTAIN;
                }
            }
        }
    }

    /** Helper method to determine where to put walls. */
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

    /** Draws ROOMS */
    public static void drawRandomRoom(TETile[][] tiles, Room r, Random random) {
        drawRoom(tiles, r, random);
        ROOMS.add(r);
        ArrayList<Room> surround = new ArrayList<>();
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
        for (Room room: surround) {
            if (!cannotDraw(tiles, room)) {
                drawRandomHallway(tiles, room, random);
            }
        }
    }
    /** Draw random room. */
    public static void drawRandomHallway(TETile[][] tiles, Room h, Random random) {
        drawRoom(tiles, h, random);
        Room room1;
        Room room2;
        Room h1;
        Room h2;
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
        if (RandomUtils.uniform(random) > 0.85) {
            if (!cannotDraw(tiles, h2)) {
                drawRandomHallway(tiles, h2, random);
            } else {
                if (!cannotDraw(tiles, room2)) {
                    drawRandomRoom(tiles, room2, random);
                }
            }
        } else {
            if (!cannotDraw(tiles, room2)) {
                drawRandomRoom(tiles, room2, random);
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

    /** Helper class for positions in world. */
    private static class Position implements Serializable {
        int x;
        int y;
        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Position shift(int xLen, int yLen) {
            return new Position(this.x + xLen, this.y + yLen);
        }
    }

    /** Helper class for player that moves around map. */
    private class Player {
        Position position;
        TETile tile;
        TETile old;
        ArrayList<Character> commands = new ArrayList<>();

        Player(Position p, TETile t, TETile[][] tiles) {
            position = p;
            tile  = t;
            old = Tileset.FLOOR;
            commands.add('W');
            commands.add('A');
            commands.add('S');
            commands.add('D');
            draw(tiles);
        }

        void draw(TETile[][] tiles) {
            tiles[position.x][position.y] = tile;
        }

        void move(Character c, TETile[][] tiles) {
            c = Character.toUpperCase(c);
            if (commands.contains(c)) {
                if (c.equals('W') && tiles[position.x][position.y + 1].equals(Tileset.FLOOR)) {
                    tiles[position.x][position.y] = old;
                    position = position.shift(0, 1);
                    savePosition(position);
                    draw(tiles);
                } else if (c.equals('A')
                        && tiles[position.x - 1][position.y].equals(Tileset.FLOOR)) {
                    tiles[position.x][position.y] = old;
                    position = position.shift(-1, 0);
                    savePosition(position);
                    draw(tiles);
                } else if (c.equals('S')
                        && tiles[position.x][position.y - 1].equals(Tileset.FLOOR)) {
                    tiles[position.x][position.y] = old;
                    position = position.shift(0, -1);
                    savePosition(position);
                    draw(tiles);
                } else if (c.equals('D')
                        && tiles[position.x + 1][position.y].equals(Tileset.FLOOR)) {
                    tiles[position.x][position.y] = old;
                    position = position.shift(1, 0);
                    savePosition(position);
                    draw(tiles);
                }
            }
        }
    }

    /**
     * Private class to contain rectangular region. Position p is the
     * coordinate of the bottom left wall.
     */
    private static class Room {
        Position p;
        int width;
        int height;

        Room(Position p, int width, int height) {
            this.p = p;
            this.width = width;
            this.height = height;
        }
    }

    /** Returns true if cannot draw room. */
    public static boolean cannotDraw(TETile[][] tiles, Room r) {
        return isOutOfBounds(r) || conflictDraw(tiles, r);
    }

    /**
     * Checks if current position is out of bounds or would result in
     * out of bounds with given x_len and y_len.
     */
    public static boolean isOutOfBounds(Room r) {
        return r.p.x <= 0 || r.p.y <= 0 || r.p.x >= WIDTH || r.p.y >= HEIGHT - 3
                || r.p.x + r.width >= WIDTH || r.p.y + r.height >= HEIGHT - 3;
    }

    /** Checks if draw would result in overlap. */
    public static boolean conflictDraw(TETile[][] tiles, Room r) {
        for (int x = 0; x < r.width; x += 1) {
            for (int y = 0; y < r.height; y += 1) {
                if (!tiles[r.p.x + x][r.p.y + y].equals(Tileset.NOTHING)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Room getRightNeighbor(Room r, int width, int height, int offset) {
        Position newP =  r.p.shift(r.width, offset);
        return new Room(newP, width, height);
    }

    public static Room getTopNeighbor(Room r, int width, int height, int offset) {
        Position newP =  r.p.shift(offset, r.height);
        return new Room(newP, width, height);
    }

    public static Room getLeftNeighbor(Room r, int width, int height, int offset) {
        Position newP = r.p.shift(-width, offset);
        return new Room(newP, width, height);
    }

    public static Room getBottomNeighbor(Room r, int width, int height, int offset) {
        Position newP = r.p.shift(offset, -height);
        return new Room(newP, width, height);
    }

}


