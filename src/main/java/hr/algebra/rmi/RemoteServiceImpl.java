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
    private List<Socket> chatClients = new ArrayList<Socket>();

    @Override
    public void sendMessage(String message) throws RemoteException  {
        chatMessagesList.add(message);
    }

    @Override
    public List<String> getChatMessage() throws RemoteException {
        return chatMessagesList;
    }

    @Override
    public void addChatClient(Socket clientSocket) throws RemoteException {
        chatClients.add(clientSocket);
    }

    @Override
    public List<Socket> getChatClients() throws RemoteException {
        return chatClients;
    }
}