package account;

import java.util.ArrayList;
import java.util.Timer;

public class Account {
    private static final ArrayList<Account> All_ACCOUNTS = new ArrayList<>();

    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;

    private String token;
    private Timer tokenTimer;

    public Account(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.token = null;
        this.tokenTimer = null;
        tokenTimer = new Timer();
        All_ACCOUNTS.add(this);
    }

    public static Account getInstance(String firstName, String lastName, String username, String password,
                                      String passwordRepeat) throws UsernameException, PasswordNotMatchException {
        if (password.equals(passwordRepeat))
            throw new PasswordNotMatchException();
        if (getAccountByUsername(username) != null)
            throw new UsernameException();
        return new Account(firstName, lastName, username, password);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public static Account getAccountByUsername(String username) {
        for (Account account : All_ACCOUNTS) {
            if (username.equals(account.getUsername())) {
                return account;
            }
        }
        return null;
    }

    public static Account getAccountByToken(String token) {
        for (Account account : All_ACCOUNTS) {
            if (token.equals(account.getToken())) {
                return account;
            }
        }
        return null;
    }

    public boolean checkPassword(String password) {
        return password.equals(this.password);
    }


}
