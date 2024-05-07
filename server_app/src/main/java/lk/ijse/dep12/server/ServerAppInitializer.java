package lk.ijse.dep12.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerAppInitializer {
    private static final List<Socket> CLIENT_LIST = new CopyOnWriteArrayList<>();

    // ArrayList<>() are not tread safe that is why we have changed it.

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(5100)) {
            while (true) {
                Socket localSocket = serverSocket.accept();
                localSocket.setKeepAlive(true);
                CLIENT_LIST.add(localSocket);
                 InputStream lis =localSocket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(lis);
                new Thread(() -> {
                    while(true) {
                        try {
                            Object o = ois.readObject();
                            System.out.println("Sever received object");
                            broadcastMessage(localSocket, o);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        }
    }

    private static void broadcastMessage(Socket cleint, Object o) {
        new Thread(() -> {
            for (Socket socket : CLIENT_LIST) {
                if (socket == cleint) continue;

                try {
                    if (socket.isConnected()) {
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        System.out.println(o);
                        oos.writeObject(o);
                        System.out.println("Sever Send the object");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();

    }
}
