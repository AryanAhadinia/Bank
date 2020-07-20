import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Aryan Ahadinia
 * @since 1.0.0
 */
public class Banker {
    public final static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Connection connection = establishConnection();
        System.out.println("Connection established");
        String command;
        while (!((command = scanner.nextLine()).equals("exit"))) {
            String response = connection.getResponse(command);
            if (response.startsWith("Error,"))
                System.err.println(response);
            else
                System.out.println(response);
        }
        connection.getResponse("exit");
        connection.disconnect();
    }

    public static Connection establishConnection() {
        System.out.print("Server ip:\t\t\t");
        String ip = scanner.nextLine();
        System.out.print("Server port:\t\t");
        String port = scanner.nextLine();
        if (!port.matches("\\d+")) {
            System.out.println("invalid port");
            return establishConnection();
        }
        try {
            return new Connection(ip, Integer.parseInt(port));
        } catch (IOException e) {
            System.out.println("connection cannot established");
            return establishConnection();
        }
    }

    static class Connection {
        public final Socket socket;
        public final DataOutputStream outputStream;
        public final DataInputStream inputStream;

        public Connection(String ip, int port) throws IOException {
            this.socket = new Socket(ip, port);
            this.outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            this.inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        }

        public void disconnect() {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket");
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                System.err.println("Error closing outputStream");
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                System.err.println("Error closing inputStream");
            }
        }

        public String getResponse(String request) {
            try {
                outputStream.writeUTF(request);
                outputStream.flush();
                try {
                    return inputStream.readUTF();
                } catch (IOException e) {
                    return "Error, OutputStream";
                }
            } catch (IOException e) {
                try {
                    if (inputStream.read() == -1) {
                        disconnect();
                        return "Error, Server disconnected";
                    }
                } catch (IOException ioException) {
                    return "Error, reading InputStream status";
                }
                return "Error, InputStream";
            }
        }
    }
}
