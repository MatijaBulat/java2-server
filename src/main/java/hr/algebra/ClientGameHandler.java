package hr.algebra;

import hr.algebra.client.model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientGameHandler extends Thread {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private static List<ClientGameHandler> gameClients;

    public ClientGameHandler(Socket socket, List<ClientGameHandler> gameClients) {
        try {
            this.socket = socket;
            this.gameClients = gameClients;

            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "IOException", ex);
            closeEverything();
        }
    }

    @Override
    public void run() {
        System.out.println("ClientGameHandler run fnc");
        while (socket.isConnected()) {
            System.out.println("while");
            try {
                Player player = (Player) objectInputStream.readObject();
                System.out.println("player received: " + player);
                broadcastPlayerState(player);
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "IOException | ClassNotFoundException", ex);
                closeEverything();
                break;
            }
        }
    }

    private void broadcastPlayerState(Player player) {
        System.out.println("broadcastPlayerState fnc");
        for (ClientGameHandler clientGameHandler : gameClients) {
            System.out.println("for loop");
            try {
                if (clientGameHandler.socket != socket) {
                    clientGameHandler.objectOutputStream.writeObject(player);
                    clientGameHandler.objectOutputStream.flush();
                    System.out.println("player sent");
                }
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "IOException", ex);
                closeEverything();
            }
        }
    }

    private void removeClientHandler() {
        gameClients.remove(this);
    }

    private void closeEverything() {
        removeClientHandler();
        try {
            if (this.objectOutputStream != null) this.objectOutputStream.close();
            if (this.objectInputStream != null) this.objectInputStream.close();
            if (this.socket != null) this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "IOException", ex);
        }
    }
}
