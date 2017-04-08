/**
 * Created by jaredwheeler on 4/7/17.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

public class Client
{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    int seeds = 0;
    public Client(String paramString, int paramInt) throws Exception
    {
        socket = new Socket(paramString, paramInt);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
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
        if(selector == 7) //cannot move seeds from home pit
            return null;
        int distribute = houses[side][selector];
        int pit = selector + 1;
        houses[side][selector] = 0;
        //loop through to spread seeds
        //check final seed location to possibly collect opposite seeds
        //check final seed location if home pit, if so take turn again
        while(distribute > 0) { //loop until all seeds are spread
            while (pit < 7 && distribute > 0) { //loop through current side
                ++houses[side][pit];
                --distribute;
                ++pit;
            }
            if (distribute > 0)
                pit = 0;
            side = 1 - side; //alternate 0,1
        }
        return houses;
    }
    public void play() throws Exception
    {
        try
        {
            for (;;) {
                String str1 = this.in.readLine();
                System.out.println("SERVER MESSAGE: " + str1);
                String[] arrayOfString;
                if (str1.startsWith("INFO")) {
                    this.out.println("READY");
                    this.out.println("I'M_SERVERSIDE_AI");

                    arrayOfString = str1.split(" ");
                    this.seeds = Integer.parseInt(arrayOfString[2]);
                    System.out.println("Seeds per pocket: " + String.valueOf(this.seeds));

                } else if (str1.startsWith("BOARD")) {
                    arrayOfString = str1.split(" ");
                    int[][] arr = new int[2][7];
                    for (int i = 0; i < 2; i++) {
                        //arrayOfInt[(i - 1)] = Integer.parseInt(arrayOfString[i]);
                        for (int j = 0; j < 7; j++) {
                            arr[i][j] = Integer.parseInt(arrayOfString[j + (i * j)]);
                        }
                    }
                    String str2 = "";
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 7; j++) {
                            if (arr[0][j] > 0) {
                                str2 = str2 + " " + String.valueOf(j + 1);
                                int[][] houses = updateBoard(arr, j, 'F');
                            }
                        }
                    }
                    this.out.println(str2.trim());
                } else if (!str1.startsWith("OK")) {
                    if ((str1.startsWith("ILLEGAL")) ||
                            (str1.startsWith("WINNER")) ||
                            (str1.startsWith("LOSER"))) {
                        break;
                    }
                    this.out.println("OK");
                }
            }
        } finally {
            this.socket.close();
        }
    }

    public static void main(String[] paramArrayOfString) throws Exception {
        for (;;)
        {
            Client localKalahClient = new Client(paramArrayOfString[0], Integer.parseInt(paramArrayOfString[1]));
            localKalahClient.play();
        }
    }
}
