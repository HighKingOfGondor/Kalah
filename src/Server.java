/**
 * Created by jaredwheeler on 4/6/17.
 */
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    Server () {}

    private static Options initOptions() {
        Options options = new Options();
        options.addOption(new Option("help", "print this message"));
        options.addOption(Option.builder("port").hasArg().required().desc("port number of the server").build());
        options.addOption(Option.builder("houses").desc("six per side").build());
        options.addOption(Option.builder("playerIDOne").hasArg().required().desc("id of the first player").build());
        options.addOption(Option.builder("playerIDTwo").hasArg().required().desc("id of the second player").build());
        options.addOption(Option.builder("seeds").hasArg().required().desc("seeds per house").build());
        options.addOption(Option.builder("time").hasArg().required().desc("time per turn").build());
        options.addOption(Option.builder("singlePlayer").desc("single or multi player").build());
        options.addOption(Option.builder("random").hasArg().desc("random init").build());
        return options;
    }

    private static void startServer(CommandLine commandLine)
    {
        try
        {
            new Thread()
            {
                public void run()
                {
                    ServerSocket localServerSocket = null;
                    try
                    {
                        localServerSocket = new ServerSocket(Integer.parseInt(commandLine.getOptionValue("port")));
                        for (;;)
                        {
                            Mancala mancala;
                            if (commandLine.getParsedOptionValue("random") == null) {
                                mancala = new Mancala(commandLine.getParsedOptionValue("seeds"), commandLine.getParsedOptionValue("time"), "S", this);
                            } else {
                                mancala = new Mancala(commandLine.getParsedOptionValue("seeds"), commandLine.getParsedOptionValue("time"), commandLine.getParsedOptionValue("random"), this);
                            }
                            Mancala kek = mancala;
                            kek.getClass();
                            Mancala.Player localPlayer1 = kek.new Player(localServerSocket.accept(), true, commandLine.getParsedOptionValue("playerIDOne"));
                            Mancala lol = mancala;
                            lol.getClass();
                            Mancala.Player localPlayer2 = lol.new Player(localServerSocket.accept(), false, commandLine.getParsedOptionValue("playerIDTwo"));
                            localPlayer1.setOpponent(localPlayer2);
                            localPlayer2.setOpponent(localPlayer1);
                            mancala.player = localPlayer1;
                            mancala.players.add(localPlayer2);
                            mancala.players.add(localPlayer1);
                            localPlayer1.start();
                            localPlayer2.start();
                        }
                    } catch (IOException localIOException) {
                        localIOException.printStackTrace();
                    } catch (Exception localException) {
                        localException.printStackTrace();
                        System.exit(0);
                    }
                }
            }.start();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Options options = initOptions();
        DefaultParser defaultParser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = defaultParser.parse(options, args);
            startServer(commandLine);
        } catch (ParseException pe) {
            System.err.println("Parse failed: " + pe);
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java Server", options);
            System.exit(1);
        }

    }
}
