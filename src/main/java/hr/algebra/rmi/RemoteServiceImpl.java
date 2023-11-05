package hr.algebra.rmi;

import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RemoteServiceImpl implements RemoteService {
    private List<String> chatMessagesList = new ArrayList<String>();
    private List<Socket> clients = new ArrayList<>();

    @Override
    public void sendMessage(String message) throws RemoteException  {
        chatMessagesList.add(message);
    }

    @Override
    public List<String> getChatMessage() throws RemoteException {
        return chatMessagesList;
    }

    @Override
    public void addClient(Socket clientSocket) throws RemoteException {
        clients.add(clientSocket);
    }

    @Override
    public List<Socket> getClients() throws RemoteException {
        return clients;
    }
}