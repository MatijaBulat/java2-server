package hr.algebra;

import hr.algebra.client.model.Player;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientGameThread extends Thread {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private static List<ClientGameThread> gameClients;

    public ClientGameThread(Socket socket, List<ClientGameThread> gameClients) {
        this.socket = socket;
        this.gameClients = gameClients;
        
        try {
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientGameThread.class.getName()).log(Level.SEVERE, "IOException", ex);
            closeEverything();
        }
    }

    @Override
    public void run() {
        System.out.println("ClientGameThread run fnc");
        while (socket.isConnected()) {
            System.out.println("while");
            try {
                Player player = (Player) objectInputStream.readObject();
                System.out.println("player received: " + player);
                broadcastPlayerState(player);
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(ClientGameThread.class.getName()).log(Level.SEVERE, "IOException | ClassNotFoundException", ex);
                closeEverything();
                break;
            }
        }
    }

    private void broadcastPlayerState(Player player) {
        System.out.println("broadcastPlayerState fnc");
        for (ClientGameThread clientGameThread : gameClients) {
            System.out.println("for loop");
            try {
                if (clientGameThread.socket != socket) {
                    clientGameThread.objectOutputStream.writeObject(player);
                    clientGameThread.objectOutputStream.flush();
                    System.out.println("player sent");
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientGameThread.class.getName()).log(Level.SEVERE, "IOException", ex);
                closeEverything();
            }
        }
    }

    private void removeClient() {
        gameClients.remove(this);
    }

    private void closeEverything() {
        removeClient();
        try {
            if (this.objectOutputStream != null) this.objectOutputStream.close();
            if (this.objectInputStream != null) this.objectInputStream.close();
            if (this.socket != null) this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientGameThread.class.getName()).log(Level.SEVERE, "IOException", ex);
        }
    }
}
