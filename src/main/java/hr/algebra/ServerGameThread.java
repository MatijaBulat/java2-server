package hr.algebra;

import hr.algebra.rmi.JndiHelper;

import javax.naming.NamingException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerGameThread extends Thread {
    private static final String SERVER_GAME_PORT_KEY = "server.game.port";
    private static int SERVER_GAME_PORT;
    private static ServerGameThread instance;
    private static List<ClientGameThread> gameClients = Collections.synchronizedList(new ArrayList<ClientGameThread>());

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
        try (ServerSocket serverGameSocket = new ServerSocket(SERVER_GAME_PORT)) {

            while (!serverGameSocket.isClosed()) {
                Socket socket = serverGameSocket.accept();

                ClientGameThread clientGameThread = new ClientGameThread(socket, gameClients);
                clientGameThread.start();

                gameClients.add(clientGameThread);

                broadcastExistingClientsPlayerState();
            }
        } catch (Exception ex) {
            Logger.getLogger(ServerGameThread.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
    }

    private static void broadcastExistingClientsPlayerState() {
        synchronized (gameClients) {
            if (gameClients.size() > 1) {
                for (int i = 0; i < gameClients.size() - 1; i++) {
                    ClientGameThread existingClient = gameClients.get(i);
                    existingClient.broadcastPlayerState(existingClient.getPlayerLastState());
                }
            }
        }
    }
}