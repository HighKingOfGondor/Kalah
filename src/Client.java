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

    public Client(String paramString, int paramInt) throws Exception
    {
        socket = new Socket(paramString, paramInt);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    /*public Pair<int[], Boolean> updateBoard(int[][] paramArrayOfInt, int paramInt, char paramChar) {
        int currentPlayer = 'N';
        if (paramChar == 'F')
            currentPlayer = 0;
        else
            currentPlayer = 1;
        int j = num_holes;
        int k = num_holes * 2 + 1;
        int[] arrayOfInt = Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
        int m = arrayOfInt[paramInt];
        int n = arrayOfInt.length;
        arrayOfInt[paramInt] = 0;
        int i1 = paramInt;
        int i2 = paramChar == 'F' ? k : j;
        int i3 = paramChar == 'F' ? j : k;
        boolean bool = false;
        while (m-- > 0) {
            i1++;
            int i4 = i1 % n;
            if (i4 == i2)
            {
                m++;
            }
            else if (m == 0)
            {
                if (i4 == i3) {
                    arrayOfInt[i4] += 1;
                    for (i5 = 0; i5 < num_holes; i5++) {
                        if (arrayOfInt[i5] > 0) bool = true;
                    }
                    break;
                }
                int i5 = (arrayOfInt[i4] == 0) && (Math.floor(i4 / (n / 2)) == i) ? 1 : 0;

                if (i5 != 0) {
                    arrayOfInt[i3] += 1;
                    int i6 = 2 * num_holes - i4;
                    arrayOfInt[i3] += arrayOfInt[i6];
                    arrayOfInt[i6] = 0;
                }
                else
                {
                    arrayOfInt[i4] += 1;
                }
            } else {
                arrayOfInt[i4] += 1;
            }
        }
        return new Pair(arrayOfInt, Boolean.valueOf(bool));
    }
    */

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
                String str1 = in.readLine();
                System.out.println("SERVER MESSAGE: " + str1);
                String[] arrayOfString;
                if (str1.startsWith("INFO")) {
                    out.println("READY");
                    out.println("I'M_SERVERSIDE_AI");
                } /*else if (str1.startsWith("BOARD")) {
                    arrayOfString = str1.split(" ");
                    int[] arrayOfInt = new int[6 * 2 + 2];
                    for (int i = 0; i < arrayOfString.length/2; i++) {
                        arrayOfInt[(i - 1)] = Integer.parseInt(arrayOfString[i]);
                    }
                    String str2 = "";
                    boolean bool = true;
                    while (bool) {
                        int j = Math.abs(new Random().nextInt()) % 6;
                        if (arrayOfInt[j] > 0) {
                            str2 = str2 + " " + String.valueOf(j + 1);
                            int[][] houses = updateBoard(houses, j, 'F');
                        }
                    }

                    out.println(str2.trim());
                } */else if (!str1.startsWith("OK")) {
                    if ((str1.startsWith("ILLEGAL")) ||
                            (str1.startsWith("WINNER")) ||
                            (str1.startsWith("LOSER"))) {
                        break;
                    }
                    out.println("OK");
                }
            }
        } finally {
            socket.close();
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
