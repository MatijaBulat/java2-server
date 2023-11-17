package hr.algebra;

import hr.algebra.rmi.RemoteService;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageBroadcastThread extends Thread {
    private RemoteService remoteService;
    private PrintWriter printWriter;
    private int listTempLength = 0;

    public MessageBroadcastThread(RemoteService remoteService) {
        this.remoteService = remoteService;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (remoteService.getChatMessage().size() > listTempLength) {
                    listTempLength = remoteService.getChatMessage().size();
                    sendMessageToAllClients(remoteService.getChatMessage().get(remoteService.getChatMessage().size() - 1));
                }
                Thread.sleep(300);
            }
        } catch (Exception ex) {
            Logger.getLogger(MessageBroadcastThread.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
    }

    private void sendMessageToAllClients(String message) throws RemoteException {
        var clients = remoteService.getChatClients();
        for (Socket clientSocket : clients) {
            try {
                printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                printWriter.println(message);
            } catch (IOException ex) {
                Logger.getLogger(MessageBroadcastThread.class.getName()).log(Level.SEVERE, "IOException", ex);
                closeConnection();
            }
        }
    }

    private void closeConnection() {
        if (this.printWriter != null) this.printWriter.close();
    }
}