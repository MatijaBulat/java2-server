package hr.algebra;

import hr.algebra.rmi.RemoteService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerChatThread extends Thread {
    private RemoteService remoteService;
    private PrintWriter printWriter;
    private int listTempLength = 0;

    public ServerChatThread(RemoteService remoteService) {
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
                Thread.sleep( 500);
            }
        }
        catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
    }

    private void sendMessageToAllClients(String message) throws RemoteException {
        var clients = remoteService.getChatClients();
        try {
            for (Socket clientSocket : clients) {
                printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                printWriter.println(message);
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "IOException", ex);
        }
    }
}