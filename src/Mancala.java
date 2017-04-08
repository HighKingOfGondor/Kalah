import org.apache.commons.cli.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

/**
 * Created by jaredwheeler on 4/6/17.
 */
public class Mancala {

    private int[][] houses = new int[2][7];

    Thread thread;
    Mancala.Player player;
    Vector<Mancala.Player> players;

    String convert = "";
    int seeds = 0;
    int time = 0;
    boolean random = false;
    boolean first = true;


    Mancala (Object seeds, Object time, Object random, Thread thread) {
        convert = seeds.toString();
        this.seeds = Integer.parseInt(convert);
        convert = time.toString();
        this.time = Integer.parseInt(convert);
        convert = random.toString();
        if (convert == "S")
            this.random = false;
        else if (convert == "R")
            this.random = true;
        else {
            this.random = false;
        }
        this.thread = thread;
    }

    private void initBoard() {
        if(random){ //init seed count irrelevant
            for (int i = 0; i < 6; i++) {
                Random rand = new Random();
                int n = rand.nextInt(3) + 0;
                houses[0][i]= n;
                houses[1][i]= n;
            }
        } else {
            int seedsLeft = seeds;
            for (int i = 0; i < houses[0].length; ++i) {
                if (i == houses[0].length - 1) {
                    houses[0][i] = 0;
                    houses[1][i] = 0;
                } else {
                    houses[0][i] = 6;
                    houses[1][i] = 6;
                }
            }
        }
    }

    public char getWinner() {
        if (houses[0][7] > houses[1][7])
            return 'F';
        else if (houses[0][7] < houses[1][7])
            return 'S';
        else
            return 'T';
    }

    public String getGameInfo(char paramChar) {
        if (random == false) {
            String str = String.format("INFO %d %d %d %c %s", new Object[] {seeds, time, "S"});
            return str;
        }
        return "";
    }

    public int[][] updateBoard (int[][] houses, int selector, char player) {
        int side = 2;
        if (player == 'F') {
            side = 0;
        } else if (player == 'S') {
            side = 1;
        } else {
            System.err.println("Bad things just happened. Player char was wrong");
        }
        if(selector == 7) { //cannot move seeds from home pit
            return null;
        }
        int distribute = houses[side][selector];
        int pit = selector + 1;
        houses[side][selector] = 0;

        //loop through to spread seeds
        //check final seed location to possibly collect opposite seeds
        //check final seed location if home pit, if so take turn again

        while(distribute > 0) { //loop until all seeds are spread
            while (pit < 7 && distribute > 0) { //loop through current side
                //if(side == getTurn()) { //depositing on our side
                ++houses[side][pit];
                --distribute;
                ++pit;
				/*} else { //depositing on enemy side
					if (pit == 6) { //don't put seed in enemy home bin
						++pit;
					} else {
						++houses[side][pit].integer;
						--distribute;
						++pit;
					}
				}*/
            }

            //reset pit if not done distributing
            if (distribute > 0)
                pit = 0;

            side = 1 - side; //alternate 0,1
        }
        print();
        return houses;
    }

    public int[][] getBoard() {
        return houses;
    }

    public void print() {
        System.out.print("\t");
        for (int i = houses[0].length-2; i >= 0; i--)
            System.out.print(houses[0][i] + "\t");

        System.out.println("\n"+ houses[0][6] + "\t\t\t\t\t\t\t" + houses[1][6]);

        System.out.print("\t");
        for (int i = 0; i < houses[1].length-1; i++)
            System.out.print(houses[1][i] + "\t");

        System.out.print("\n------------------------------------------------------------\n");
    }

    class Player extends Thread {
        boolean isAI = false;
        boolean firstOrSecond = false;
        int playerID = 0;
        Player player;
        String convert = "";
        Socket socket;
        BufferedReader in;
        PrintWriter out;
        public Player (Socket socket, boolean firstOrSecond, Object convert) {
            this.socket = socket;
            this.playerID = playerID;
            this.firstOrSecond = firstOrSecond;
            this.convert = convert.toString();
            this.playerID = Integer.parseInt(this.convert);
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("WELCOME");
            } catch (IOException e) {
                System.out.println("Player" + playerID + " disconnected" + e);
            }
        }
        public void setOpponent (Player player) {
            this.player = player;
        }
        public void message (String string) {
            out.println(string);
        }
        public void notify (String string) {
            player.message(string);
            if (player.isAI == true) {
                player.message("BOARD" + getBoard());
            }
        }
    }
}
