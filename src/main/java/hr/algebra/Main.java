package hr.algebra;

import hr.algebra.rmi.JndiHelper;
import hr.algebra.rmi.RemoteService;
import hr.algebra.rmi.RemoteServiceImpl;

import javax.naming.NamingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final String RMI_PORT_KEY = "rmi.port";
    private static final int RANDOM_PORT_HINT = 0;
    public static void main(String[] args) {

        try {
            String rmiPortString = JndiHelper.getValueFromConfiguration(RMI_PORT_KEY);
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(rmiPortString));

            RemoteService chatServer = new RemoteServiceImpl();

            RemoteService stub = (RemoteService) UnicastRemoteObject
                    .exportObject(chatServer, RANDOM_PORT_HINT);

            registry.rebind(RemoteService.REMOTE_OBJECT_NAME, stub);

            System.err.println("Object registered in RMI registry");
        } catch (RemoteException | NamingException | FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}