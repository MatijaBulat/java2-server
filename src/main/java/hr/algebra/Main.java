package hr.algebra;

import hr.algebra.client.model.Player;
import hr.algebra.rmi.JndiHelper;
import hr.algebra.rmi.RemoteService;
import hr.algebra.rmi.RemoteServiceImpl;
import javax.naming.NamingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final String RMI_PORT_KEY = "rmi.port";
    private static final String SERVER_CHAT_PORT_KEY = "server.chat.port";
    private static final String SERVER_GAME_PORT_KEY = "server.game.port";
    private static int SERVER_CHAT_PORT;
    private static int SERVER_GAME_PORT;
    private static final int RANDOM_PORT_HINT = 0;
    private static RemoteService remoteService;
    private static List<ClientGameHandler> gameClients;

    public static void main(String[] args) {
        gameClients = new ArrayList<ClientGameHandler>();

        try {
            SERVER_CHAT_PORT = Integer.parseInt(JndiHelper.getValueFromConfiguration(SERVER_CHAT_PORT_KEY));
            SERVER_GAME_PORT = Integer.parseInt(JndiHelper.getValueFromConfiguration(SERVER_GAME_PORT_KEY));
        } catch (IOException | NamingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "IOException | NamingException", ex);
        }

        try {
            String rmiPortString = JndiHelper.getValueFromConfiguration(RMI_PORT_KEY);
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(rmiPortString));

            remoteService = new RemoteServiceImpl();

            RemoteService stub = (RemoteService) UnicastRemoteObject
                    .exportObject(remoteService, RANDOM_PORT_HINT);

            registry.rebind(RemoteService.REMOTE_OBJECT_NAME, stub);
            System.err.println("Object registered in RMI registry");
        } catch (RemoteException | NamingException | FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "RemoteException | NamingException | FileNotFoundException", ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "IOException", ex);
        }

        new Thread(() -> {
            try (ServerSocket serverChatSocket = new ServerSocket(SERVER_CHAT_PORT)) {
                ServerChatThread serverChatThread = new ServerChatThread(remoteService);
                serverChatThread.setDaemon(true);
                serverChatThread.start();

                while (true) {
                    Socket socket = serverChatSocket.accept();
                    remoteService.addChatClient(socket);
                }
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Exception", ex);
            }
        }).start();

        ServerGameThread serverGameThread = new ServerGameThread(SERVER_GAME_PORT, gameClients);
        serverGameThread.start();
    }
}