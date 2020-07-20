package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server extends Thread {
    private ServerSocket serverSocket;

    public Server(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("IOException, ServerSocket IOException");
            System.exit(1);
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        System.out.println("Bank is up");
        do {
            try {
                Socket socket = serverSocket.accept();
                try {
                    ClientThread.getInstance(socket).start();
                    System.out.println("hello");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (true);
    }
}
