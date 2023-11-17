package hr.algebra;

import hr.algebra.rmi.JndiHelper;

import javax.naming.NamingException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerGameThread extends Thread {
    private static final String SERVER_GAME_PORT_KEY = "server.game.port";
    private static int SERVER_GAME_PORT;
    private static ServerGameThread instance;
    private static List<ClientGameThread> gameClients = new ArrayList<ClientGameThread>();

    private ServerGameThread() {}

    static {
        try {
            SERVER_GAME_PORT = Integer.parseInt(JndiHelper.getValueFromConfiguration(SERVER_GAME_PORT_KEY));
        } catch (IOException | NamingException ex) {
            Logger.getLogger(ServerGameThread.class.getName()).log(Level.SEVERE, "IOException | NamingException", ex);
        }
    }

    public static ServerGameThread getInstance() {
        if (instance == null) {
            instance = new ServerGameThread();
        }
        return instance;
    }

    @Override
    public void run() {
        System.out.println("ServerGameThread run fnc");
        try (ServerSocket serverGameSocket = new ServerSocket(SERVER_GAME_PORT)) {
            System.out.println("server game socket created");

            while (!serverGameSocket.isClosed()) {
                System.out.println("while loop");
                Socket socket = serverGameSocket.accept();
                System.out.println("client connected");

                ClientGameThread clientGameThread = new ClientGameThread(socket, gameClients);
                clientGameThread.start();
                System.out.println("clientGameThread started");

                gameClients.add(clientGameThread);
                System.out.println("client added");
            }
        } catch (Exception ex) {
            Logger.getLogger(ServerGameThread.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
    }
}