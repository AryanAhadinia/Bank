package account;

import account.exceptions.PasswordMissMatchException;
import account.exceptions.UsernameException;

import java.util.*;

public class Account {
    private static final ArrayList<Account> All_ACCOUNTS = new ArrayList<>();
    private static final HashMap<String, Account> TOKEN_TO_ACCOUNT_HASH_MAP = new HashMap<>();

    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;

    private final long accountNumber;
    private int credit;

    private String token;
    private final Timer tokenTimer;

    public Account(String firstName, String lastName, String username, String password, int accountNumber, int credit) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.accountNumber = accountNumber;
        this.credit = credit;
        this.token = null;
        this.tokenTimer = new Timer();
        All_ACCOUNTS.add(this);
    }

    public static String getInstance(String firstName, String lastName, String username, String password,
                                      String passwordRepeat) throws UsernameException, PasswordMissMatchException {
        if (password.equals(passwordRepeat))
            throw new PasswordMissMatchException();
        if (getAccountByUsername(username) != null)
            throw new UsernameException();
        int accountNumber;
        do {
            accountNumber = generateAccountNumber();
        } while (getAccountByAccountNumber(accountNumber) != null);
        return String.valueOf((new Account(firstName, lastName, username, password, accountNumber, 0)).getAccountNumber());
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

    public long getAccountNumber() {
        return accountNumber;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public void deposit(int amount) {
        setCredit(credit + amount);
    }

    public void withdraw(int amount) {
        setCredit(credit - amount);
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

    public static Account getAccountByAccountNumber(int accountNumber) {
        for (Account account : All_ACCOUNTS) {
            if (accountNumber == account.getAccountNumber()) {
                return account;
            }
        }
        return null;
    }

    public boolean checkPassword(String password) {
        return password.equals(this.password);
    }

    public static String generateRandomToken() {
        final String LETTERS_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rand = new Random();
        int upperBound = LETTERS_SET.length()-1;
        StringBuilder code = new StringBuilder();
        for(int i = 0;i < 10;i++){
            code.append(LETTERS_SET.charAt(rand.nextInt(upperBound)));
        }
        return code.toString();
    }

    public static int generateAccountNumber() {
        Random rand = new Random();
        final int UPPER_BOUND = 899999;
        final int LOWER_BOUND = 100000;
        int accountNumber = rand.nextInt(UPPER_BOUND);
        return accountNumber + LOWER_BOUND;
    }

    private String assignToken() {
        TOKEN_TO_ACCOUNT_HASH_MAP.remove(token);
        do {
            token = generateRandomToken();
        } while (TOKEN_TO_ACCOUNT_HASH_MAP.containsKey(token));
        TOKEN_TO_ACCOUNT_HASH_MAP.put(token, this);
        TimerTask expireToken = new TimerTask() {
            @Override
            public void run() {
                TOKEN_TO_ACCOUNT_HASH_MAP.remove(token);
                token = null;
            }
        };
        tokenTimer.schedule(expireToken, new Date(System.currentTimeMillis() + 3600000));
        return token;
    }

    public static String assignToken(String username, String password) throws UsernameException,
            PasswordMissMatchException {
        Account account = getAccountByUsername(username);
        if (account == null)
            throw new UsernameException();
        if (account.checkPassword(password))
            throw new PasswordMissMatchException();
        return account.assignToken();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountNumber == account.accountNumber &&
                Objects.equals(username, account.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, accountNumber);
    }
}
