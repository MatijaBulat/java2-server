package hr.algebra;

import hr.algebra.client.model.Player;

import javax.swing.plaf.ActionMapUIResource;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerGameThread extends Thread {
    private final int SERVER_GAME_PORT;
    private List<ClientGameHandler> gameClients;

    public ServerGameThread(int serverGamePort, List<ClientGameHandler> gameClients) {
        this.SERVER_GAME_PORT = serverGamePort;
        this.gameClients = gameClients;
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

                ClientGameHandler clientGameHandler = new ClientGameHandler(socket, gameClients);
                clientGameHandler.start();
                System.out.println("clientGameHandler started");

                gameClients.add(clientGameHandler);
                System.out.println("client added");
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
    }
}