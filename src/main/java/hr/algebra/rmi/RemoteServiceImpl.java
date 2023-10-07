package hr.algebra.rmi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class RemoteServiceImpl implements RemoteService {
    List<String> chatMessagesList = new ArrayList<String>();

    @Override
    public void sendMessage(String message) throws RemoteException  {
        chatMessagesList.add(message);
    }

    @Override
    public String getMessage() throws RemoteException {
        return chatMessagesList.get(chatMessagesList.size() - 1);
    }
}
