package database;

import account.Account;
import transaction.Transaction;

import java.sql.*;
import java.util.HashMap;

/**
 * @author rpirayadi
 * @since 0.0.3
 */
public class DataBase {

    private  final static String url = "jdbc:sqlite:.\\src\\t";
    private final static Connection connection = connect();

    private static Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("bar");
            System.out.println(e.getMessage());
        }
        return connection;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void createNewTable(String nameOfTable, HashMap<String,String> content) {

        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sql.append(nameOfTable).append("(\n");
        for (String columnName : content.keySet()) {
            sql.append(columnName).append(" ").append(content.get(columnName)).append(",\n");
        }
        sql.delete(sql.length()-2,sql.length());
        sql.append(" \n )");
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(String.valueOf(sql));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void delete(String nameOfTable, String nameOfColumn , String identifier) {
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(nameOfTable).append(" WHERE ").append(nameOfColumn).append("=?");

        try (PreparedStatement preparedStatement = DataBase.getConnection().prepareStatement(String.valueOf(sql))) {

            preparedStatement.setString(1, identifier);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteAll(String nameOfTable){
        String sql = "DELETE FROM " + nameOfTable;
        try (PreparedStatement preparedStatement = DataBase.getConnection().prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static boolean doesIdAlreadyExist(String nameOfTable, String nameOfColumn , String identifier){
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(nameOfColumn).append(" FROM ").append(nameOfTable);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(String.valueOf(sql))) {
            while (resultSet.next()) {
                if(resultSet.getString(nameOfColumn).equals(identifier))
                    return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    public static void createNewTablesToStart(){
        AccountDataBase.createNewTable();
        TransactionDataBase.createNewTable();
    }
}
