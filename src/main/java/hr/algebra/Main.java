package hr.algebra;

import hr.algebra.rmi.JndiHelper;
import hr.algebra.rmi.RemoteService;
import hr.algebra.rmi.RemoteServiceImpl;
import javax.naming.NamingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final String RMI_PORT_KEY = "rmi.port";
    private static final String SERVER_CHAT_PORT_KEY = "server.chat.port";
    private static int SERVER_CHAT_PORT;
    private static final int RANDOM_PORT_HINT = 0;
    private static RemoteService remoteService;

    public static void main(String[] args) {
        try {
            SERVER_CHAT_PORT = Integer.parseInt(JndiHelper.getValueFromConfiguration(SERVER_CHAT_PORT_KEY));

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

        try (ServerSocket serversocket = new ServerSocket(SERVER_CHAT_PORT)) {
            ServerChatThread threadServer = new ServerChatThread(remoteService);
            threadServer.start();

            while (true) {
                Socket socket = serversocket.accept();
                remoteService.addClient(socket);
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
    }
}