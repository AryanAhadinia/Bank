import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class handles initiating connection to bankAPI ,sending requests to Bank server
 * and also responses from Bank server.
 */
public class BankAPI {
    public static int PORT;
    public static String IP;

    private static DataOutputStream outputStream;
    private static DataInputStream inputStream;

    /**
     * This method is used to add initiating socket and IN/OUT data stream .
     *
     * @throws IOException when IP/PORT hasn't been set up properly.
     */
    public static void ConnectToBankServer() throws IOException {
        try {
            Socket socket = new Socket(IP, PORT);
            outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException e) {
            throw new IOException("Exception while initiating connection:");
        }
    }

    /**
     * This method is used to start a Thread ,listening on IN data stream.
     */
    public static void StartListeningOnInput() {
        new Thread(() -> {
            while (true) {
                try {
                    System.out.println(inputStream.readUTF());
                } catch (IOException e) {
                    System.out.println("disconnected");
                    System.exit(0);
                }
            }
        }).start();
    }

    /**
     * This method is used to send message with value
     *
     * @param msg to Bank server.
     * @throws IOException when OUT data stream been interrupted.
     */
    public static void SendMessage(String msg) throws IOException {
        try {
            outputStream.writeUTF(msg);
            outputStream.flush();
        } catch (IOException e) {
            throw new IOException("Exception while sending message:");
        }
    }

    /**
     * This method is used to illustrate an example of using methods of this class.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter server ip:\t");
        IP = scanner.nextLine();
        try {
            System.out.print("Enter server port:\t");
            PORT = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Invalid port, port must be an integer");
        }
        try {
            ConnectToBankServer();
            StartListeningOnInput();
            while (true) {
                System.out.println("Here");
                SendMessage(scanner.nextLine());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
