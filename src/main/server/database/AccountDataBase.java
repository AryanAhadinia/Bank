package database;

import account.Account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * @author rpirayadi
 * @since 0.0.3
 */
public class AccountDataBase {
    public static void createNewTable() {
        HashMap<String, String> content = new HashMap<>();
        content.put("firstName", "String");
        content.put("lastName", "String");
        content.put("username", "String");
        content.put("password", "String");
        content.put("accountNumber", "long");
        content.put("credit", "int");

        DataBase.createNewTable("Accounts", content);
    }


    public static void add(Account account) {
        if (DataBase.doesIdAlreadyExist("Accounts", "username", account.getUsername())) {
            return;
        }
        String sql = "INSERT into Accounts (firstName, lastName, username, password, accountNumber, credit) " +
                "VALUES (?, ? , ? , ? , ?, ?)";
        try (PreparedStatement statement = DataBase.getConnection().prepareStatement(sql)) {
            statement.setString(1, account.getFirstName());
            statement.setString(2, account.getLastName());
            statement.setString(3, account.getUsername());
            statement.setString(4, account.getPassword());
            statement.setLong(5, account.getAccountNumber());
            statement.setInt(6, account.getCredit());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void update(Account account) {
        delete(account.getUsername());
        add(account);
    }

    public static void delete(String username) {
        DataBase.delete("Accounts", "username", username);
    }


    public static void importAllAccounts() {
        String sql = "SELECT *  FROM Accounts";

        try (Statement statement = DataBase.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                long accountNumber = resultSet.getLong("accountNumber");
                int credit = resultSet.getInt("credit");

                new Account(firstName, lastName, username, password, accountNumber, credit);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}



