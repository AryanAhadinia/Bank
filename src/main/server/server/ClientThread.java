package server;

import control.Controller;
import main.Main;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Aryan Ahadinia
 * @since 1.0.0
 */
public class ClientThread extends Thread {
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final Controller controller;

    private ClientThread(Socket socket, DataInputStream inputStream, DataOutputStream outputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.controller = new Controller();
    }

    public static ClientThread getInstance(Socket socket) throws IOException {
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream outputStream = new DataOutputStream(new DataOutputStream(socket.getOutputStream()));
        return new ClientThread(socket, inputStream, outputStream);
    }

    @Override
    public void run() {
        if (Main.debugPrint)
            System.out.println(Thread.currentThread().getName() + "\t" + "Connected");
        while (true) {
            try {
                String request = inputStream.readUTF();
                String[] requestElements = request.split("\\s");
                String command = requestElements[0];
                StringBuilder response = new StringBuilder();
                if ("create_account".equals(command) && requestElements.length == 6) {
                    response.append(controller.controlCreateAccount(requestElements));
                } else if ("get_token".equals(command) && requestElements.length == 3) {
                    response.append(controller.controlGetToken(requestElements));
                } else if ("create_receipt".equals(command) /* && (requestElements.length == 6 || requestElements.length == 7)*/) {
                    response.append(controller.controlCreateReceipt(requestElements));
                } else if ("get_transactions".equals(command) && requestElements.length == 3) {
                    response.append(controller.controlGetTransactions(requestElements));
                } else if ("pay".equals(command) && requestElements.length == 2) {
                    response.append(controller.controlPay(requestElements));
                } else if ("get_balance".equals(command) && requestElements.length == 2) {
                    response.append(controller.controlGetBalance(requestElements));
                } else if ("exit".equals(command) && requestElements.length == 1) {
                    disconnect();
                    break;
                } else {
                    response.append("invalid input");
                }
                if (Main.debugPrint)
                    System.out.println(Thread.currentThread().getName() + "\t" + "Request:\t" + request + "\n\t\t\t" +
                            "Response:\t" + response);
                try {
                    outputStream.writeUTF(response.toString());
                } catch (IOException e) {
                    System.err.println("Error, OutputStream");
                    break;
                }
            } catch (IOException e) {
                try {
                    if (inputStream.read() == -1) {
                        boolean status = disconnect();
                        System.err.println("InputStream IOException, user eventually disconnected, Thread disconnected with status " + status);
                        break;
                    }
                } catch (IOException ioException) {
                    System.err.println("Error reading InputStream status");
                    break;
                }
                System.err.println("Error, InputStream");
            }
        }
        if (Main.debugPrint)
            System.out.println(Thread.currentThread().getName() + "\t" + "Disconnected");
    }

    private boolean disconnect() {
        boolean status = true;
        try {
            socket.close();
        } catch (IOException e) {
            status = false;
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            status = false;
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            status = false;
        }
        return status;
    }
}
