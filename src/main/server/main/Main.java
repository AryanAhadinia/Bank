package main;

import database.DataBase;
import server.Server;

import java.util.Scanner;

/**
 * @author Aryan Ahadinia
 * @since 1.0.0
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static boolean debugPrint;

    public static void main(String[] args) {
        DataBase.createNewTablesToStart();
        DataBase.importAllData();
//        TODO sort transactions
//        System.out.println(Transaction.getAllTransactions());
//        Transaction.sortAll();
//        System.out.println(Transaction.getAllTransactions());
        System.out.print("Do you want to print client actions? (Y for yes, any other phrase for no)\t");
        debugPrint = scanner.nextLine().equalsIgnoreCase("Y");
        try {
            System.out.print("Enter hosting port:\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t");
            int port = Integer.parseInt(scanner.nextLine());
            new Server(port).start();
        } catch (NumberFormatException e) {
            System.err.println("Invalid port");
            System.exit(1);
        }
    }
}
