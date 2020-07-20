package account;

import account.exceptions.PasswordMissMatchException;
import account.exceptions.TokenExpiryException;
import account.exceptions.TokenNotFoundException;
import account.exceptions.UsernameException;
import database.AccountDataBase;
import transaction.exceptions.MoneyValueException;

import java.util.*;

public class Account {
    private static final ArrayList<Account> All_ACCOUNTS = new ArrayList<>();
    private static final HashMap<String, Account> TOKEN_TO_ACCOUNT_HASH_MAP = new HashMap<>();
    private static final ArrayList<String> EXPIRED_TOKENS = new ArrayList<>();

    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;

    private final long accountNumber;
    private int credit;

    private String token;
    private Timer tokenTimer;

    public Account(String firstName, String lastName, String username, String password, long accountNumber, int credit) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.accountNumber = accountNumber;
        this.credit = credit;
        this.token = null;
        this.tokenTimer = new Timer();
        Account.All_ACCOUNTS.add(this);
    }

    public static String getInstance(String firstName, String lastName, String username, String password,
                                     String passwordRepeat) throws UsernameException, PasswordMissMatchException {
        if (!password.equals(passwordRepeat))
            throw new PasswordMissMatchException();
        if (getAccountByUsername(username) != null)
            throw new UsernameException();
        int accountNumber;
        do {
            accountNumber = generateAccountNumber();
        } while (getAccountByAccountNumber(accountNumber) != null);
        Account account = new Account(firstName, lastName, username, password, accountNumber, 0);
        AccountDataBase.add(account);
        return String.valueOf(accountNumber);
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
        AccountDataBase.update(this);
    }

    public void deposit(int amount) {
        setCredit(credit + amount);
    }

    public void withdraw(int amount) throws MoneyValueException {
        if (credit < amount)
            throw new MoneyValueException();
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

    public static Account getAccountByToken(String token) throws TokenExpiryException {
        if (EXPIRED_TOKENS.contains(token))
            throw new TokenExpiryException();
        return TOKEN_TO_ACCOUNT_HASH_MAP.get(token);
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
        int upperBound = LETTERS_SET.length() - 1;
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            code.append(LETTERS_SET.charAt(rand.nextInt(upperBound)));
        }
        return "TOKEN_" + code.toString();
    }

    public static int generateAccountNumber() {
        Random rand = new Random();
        final int UPPER_BOUND = 899999;
        final int LOWER_BOUND = 100000;
        int accountNumber = rand.nextInt(UPPER_BOUND);
        return accountNumber + LOWER_BOUND;
    }

    private String assignToken() {
        String previousToken = token;
        do {
            token = generateRandomToken();
        } while (TOKEN_TO_ACCOUNT_HASH_MAP.containsKey(token) || EXPIRED_TOKENS.contains(token));
        TOKEN_TO_ACCOUNT_HASH_MAP.remove(previousToken);
        EXPIRED_TOKENS.add(previousToken);
        tokenTimer.cancel();
        TOKEN_TO_ACCOUNT_HASH_MAP.put(token, this);
        TimerTask expireToken = new TimerTask() {
            @Override
            public void run() {
                TOKEN_TO_ACCOUNT_HASH_MAP.remove(token);
                EXPIRED_TOKENS.add(token);
                token = null;
            }
        };
        tokenTimer = new Timer();
        tokenTimer.schedule(expireToken, new Date(System.currentTimeMillis() + 3600000));
        return token;
    }

    public static String assignToken(String username, String password) throws UsernameException,
            PasswordMissMatchException {
        Account account = getAccountByUsername(username);
        if (account == null)
            throw new UsernameException();
        if (!account.checkPassword(password))
            throw new PasswordMissMatchException();
        return account.assignToken();
    }

    public static int getCredit(String token) throws TokenNotFoundException, TokenExpiryException {
        Account account = getAccountByToken(token);
        if (account == null) {
            throw new TokenNotFoundException();
        }
        return account.getCredit();
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

    @Override
    public String toString() {
        return "Account{" +
                "\"firstName:\"" + firstName + '\'' +
                ", \"lastName:\"\"" + lastName + '\'' +
                ", \"username\"" + username + '\'' +
                ", \"password:\"" + password + '\'' +
                ", \"accountNumber:\"" + accountNumber +
                ", \"credit\"=" + credit +
                '}';
    }
}
