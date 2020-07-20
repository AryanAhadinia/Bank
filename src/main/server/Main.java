import database.DataBase;
import server.Server;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DataBase.createNewTablesToStart();
        DataBase.importAllData();
        final Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter hosting port:\t");
            int port = Integer.parseInt(scanner.nextLine());
            new Server(port).start();
        } catch (NumberFormatException e) {
            System.err.println("Invalid port");
            System.exit(1);
        }
    }
}
