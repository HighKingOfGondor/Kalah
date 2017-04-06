import static java.lang.System.*;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by jaredwheeler on 4/4/17.
 */
public class SocketServer {

    Socket s;
    BufferedReader in;
    PrintWriter out;

    SocketServer() {
        try {
            s = new Socket("kalah.cf", 42374);
            in = new BufferedReader((new InputStreamReader(s.getInputStream())));
            out = new PrintWriter(s.getOutputStream(), true);


            if(s == null || in == null || out == null) {
                throw new IOException("null data in socket/input/output");
            }
        } catch (UnknownHostException e) {
            System.err.println("Could not connect: unknown host");
        } catch (IOException e) {
            System.err.println("Could not connect: " + e);
        }
    }

    public void communicate() {
        try {
            System.out.println("Attempting to connect...");
            String response = in.readLine();

            messageRecieved(response);

            response = in.readLine();
            messageRecieved(response);

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                String userInput;
                while ((userInput = stdIn.readLine()) != null) {
                    out.println(userInput);

                    String rr;
                    while((rr = in.readLine()) != null) {
                        System.out.println("echo: " + rr);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void messageRecieved(String m) {
        System.out.println("Server says: " + m);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        s.close();
        in.close();
        out.close();
    }
}