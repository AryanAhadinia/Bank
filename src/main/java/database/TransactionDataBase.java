package database;

import account.Account;
import transaction.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * @author rpirayadi
 * @since 0.0.3
 */
public class TransactionDataBase {
    public static void createNewTable() {
        HashMap<String, String> content = new HashMap<>();
        content.put("token", "String");
        content.put("receiptType", "String");
        content.put("money", "int");
        content.put("sourceId", "int");
        content.put("destinationId", "int");
        content.put("description", "String");
        content.put("payed", "boolean");
        content.put("identifier", "String");

        DataBase.createNewTable("Transactions", content);
    }


    public static void add(Transaction transaction) {
        if (DataBase.doesIdAlreadyExist("Transaction", "identifier", transaction.getIdentifier())) {
            return;
        }
        String sql = "INSERT into Transactions (token, receiptType, money, sourceId, destinationId, description, payed, identifier) " +
                "VALUES (?, ? , ? , ? , ?, ?, ? , ?)";
        try (PreparedStatement statement = DataBase.getConnection().prepareStatement(sql)) {
            statement.setString(1, transaction.getToken());
            statement.setString(2, transaction.getReceiptType());
            statement.setInt(3, transaction.getMoney());
            statement.setInt(4, transaction.getSourceID());
            statement.setInt(5, transaction.getDestinationID());
            statement.setString(6, transaction.getDescription());
            statement.setBoolean(7, transaction.isPayed());
            statement.setString(8, transaction.getIdentifier());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void update(Transaction transaction) {
        delete(transaction.getIdentifier());
        add(transaction);
    }

    public static void delete(String identifier) {
        DataBase.delete("Transactions", "identifier", identifier);
    }


    public static void importAllTransactions() {
        String sql = "SELECT *  FROM Transactions";

        try (Statement statement = DataBase.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String token = resultSet.getString("token");
                String receiptType = resultSet.getString("receiptType");
                int money = resultSet.getInt("money");
                int sourceId = resultSet.getInt("sourceId");
                int destinationId = resultSet.getInt("destinationId");
                String description = resultSet.getString("description");
                boolean payed = resultSet.getBoolean("payed");
                String identifier = resultSet.getString("identifier");

                new Transaction(token, receiptType, money, sourceId, destinationId, description, payed);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}

