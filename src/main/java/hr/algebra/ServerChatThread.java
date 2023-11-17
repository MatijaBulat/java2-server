package hr.algebra;

import hr.algebra.rmi.JndiHelper;
import hr.algebra.rmi.RemoteService;
import hr.algebra.rmi.RemoteServiceImpl;

import javax.naming.NamingException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerChatThread extends Thread {
    private static final String SERVER_CHAT_PORT_KEY = "server.chat.port";
    private static int SERVER_CHAT_PORT;
    private static ServerChatThread instance;
    private final RemoteService remoteService;

    private ServerChatThread() {
        this.remoteService = RemoteServiceImpl.getInstance();
    }

    static {
        try {
            SERVER_CHAT_PORT = Integer.parseInt(JndiHelper.getValueFromConfiguration(SERVER_CHAT_PORT_KEY));
        } catch (IOException | NamingException ex) {
            Logger.getLogger(ServerChatThread.class.getName()).log(Level.SEVERE, "IOException | NamingException", ex);
        }
    }

    public static ServerChatThread getInstance() {
        if (instance == null) {
            instance = new ServerChatThread();
        }
        return instance;
    }

    @Override
    public void run() {
        try (ServerSocket serverChatSocket = new ServerSocket(SERVER_CHAT_PORT)) {
            MessageBroadcastThread messageBroadcastThread = new MessageBroadcastThread(remoteService);
            messageBroadcastThread.setDaemon(true);
            messageBroadcastThread.start();

            while (true) {
                Socket socket = serverChatSocket.accept();
                remoteService.addChatClient(socket);
            }
        } catch (Exception ex) {
            Logger.getLogger(ServerChatThread.class.getName()).log(Level.SEVERE, "Exception", ex);
        }
    }
}
