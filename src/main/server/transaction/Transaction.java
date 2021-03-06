package transaction;

import account.Account;
import account.exceptions.AccountNotFoundException;
import account.exceptions.IllegalAccountAccessException;
import account.exceptions.TokenExpiryException;
import account.exceptions.TokenNotFoundException;
import database.TransactionDataBase;
import transaction.exceptions.InvalidArgumentException;
import transaction.exceptions.MoneyValueException;
import transaction.exceptions.TransactionTypeException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Transaction implements Comparable<Transaction>, Serializable {
    private static final ArrayList<Transaction> ALL_TRANSACTIONS = new ArrayList<>();

    private final String token;
    private final String receiptType;
    private final int money;
    private final int sourceID;
    private final int destinationID;
    private final String description;

    private boolean payed;

    private final String identifier;

    private final Account source;
    private final Account destination;

    public Transaction(String token, String receiptType, int money, int sourceID, int destinationID, String description,
                       boolean payed, String identifier) {
        this.token = token;
        this.receiptType = receiptType;
        this.money = money;
        this.sourceID = sourceID;
        this.destinationID = destinationID;
        this.description = description;
        this.payed = payed;
        this.identifier = identifier;
        this.source = Account.getAccountByAccountNumber(sourceID);
        this.destination = Account.getAccountByAccountNumber(destinationID);
        ALL_TRANSACTIONS.add(this);
    }

    public static String getInstance(String token, String receiptType, String moneyStr, String sourceIDStr,
                                     String destinationIDStr, String description) throws TransactionTypeException,
            MoneyValueException, TokenNotFoundException, InvalidArgumentException, IllegalAccountAccessException,
            TokenExpiryException {
        if (!(receiptType.equals("deposit") || receiptType.equals("withdraw") || receiptType.equals("move"))) {
            throw new TransactionTypeException();
        }
        int money;
        try {
            money = Integer.parseInt(moneyStr);
            if (money <= 0)
                throw new MoneyValueException();
        } catch (NumberFormatException e) {
            throw new MoneyValueException();
        }
        if (!description.matches("\\w*")) {
            throw new InvalidArgumentException("your input contains invalid characters");
        }
        if (Account.getAccountByToken(token) == null) {
            throw new TokenNotFoundException();
        }
        if (isAccountNumberUnexpected(sourceIDStr)) {
            throw new InvalidArgumentException("source account id is invalid");
        }
        if (isAccountNumberUnexpected(destinationIDStr)) {
            throw new InvalidArgumentException("dest account id is invalid");
        }
        int sourceId = Integer.parseInt(sourceIDStr);
        int destinationId = Integer.parseInt(destinationIDStr);
        if (receiptType.equals("move") && !(sourceId != -1 && destinationId != -1)) {
            throw new InvalidArgumentException("invalid account id");
        }
        if (receiptType.equals("deposit")) {
            if (sourceId != -1)
                throw new InvalidArgumentException("source account id is invalid");
            if (destinationId == -1)
                throw new InvalidArgumentException("invalid account id");
        }
        if (receiptType.equals("withdraw")) {
            if (sourceId == -1)
                throw new InvalidArgumentException("invalid account id");
            if (destinationId != -1)
                throw new InvalidArgumentException("dest account id is invalid");
        }
        if (sourceId == destinationId) {
            throw new InvalidArgumentException("equal source and dest account");
        }
        Account account = Account.getAccountByAccountNumber(sourceId);
        if ((receiptType.equals("move") || receiptType.equals("withdraw"))) {
            if (account == null) {
                throw new InvalidArgumentException("invalid account id");
            }
            if (!token.equals(account.getToken())) {
                throw new IllegalAccountAccessException();
            }
        }
        Transaction transaction = new Transaction(token, receiptType, money, sourceId, destinationId, description,
                false, "TR" + receiptType.substring(0, 3).toUpperCase() + String.format("%015d",
                ALL_TRANSACTIONS.size() + 1));
        TransactionDataBase.add(transaction);
        return transaction.getIdentifier();
    }

    public static void sortAll() {
        ALL_TRANSACTIONS.sort(Transaction::compareTo);
    }

    public static ArrayList<Transaction> getAllTransactions() {
        return ALL_TRANSACTIONS;
    }

    public String getToken() {
        return token;
    }

    public String getReceiptType() {
        return receiptType;
    }

    public int getMoney() {
        return money;
    }

    public int getSourceID() {
        return sourceID;
    }

    public int getDestinationID() {
        return destinationID;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPayed() {
        return payed;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Account getSource() {
        return source;
    }

    public Account getDestination() {
        return destination;
    }

    public static Transaction getTransactionByIdentifier(String identifier) {
        for (Transaction transaction : ALL_TRANSACTIONS) {
            if (identifier.equals(transaction.getIdentifier())) {
                return transaction;
            }
        }
        return null;
    }

    public boolean pay() throws MoneyValueException, AccountNotFoundException {
        if (!payed) {
            if (receiptType.equals("deposit")) {
                if (destination == null) {
                    throw new AccountNotFoundException();
                }
                destination.deposit(money);
            }
            if (receiptType.equals("withdraw")) {
                if (source == null) {
                    throw new AccountNotFoundException();
                }
                source.withdraw(money);
            }
            if (receiptType.equals("move")) {
                Account source = Account.getAccountByAccountNumber(sourceID);
                Account destination = Account.getAccountByAccountNumber(destinationID);
                if (source == null) {
                    throw new AccountNotFoundException();
                }
                if (destination == null) {
                    throw new AccountNotFoundException();
                }
                source.withdraw(money);
                destination.deposit(money);
            }
            payed = true;
            TransactionDataBase.update(this);
            return true;
        }
        return false;
    }

    public static boolean pay(String identifier) throws InvalidArgumentException, MoneyValueException,
            AccountNotFoundException {
        Transaction transaction = getTransactionByIdentifier(identifier);
        if (transaction == null) {
            throw new InvalidArgumentException();
        }
        return transaction.pay();
    }

    public static String getTransactions(String token, String type) throws TokenExpiryException, TokenNotFoundException,
            InvalidArgumentException, IllegalAccountAccessException {
        Account account = Account.getAccountByToken(token);
        if (account == null)
            throw new TokenNotFoundException();
        if (type.matches("TR(DEP|WIT|MOV)\\d{15}")) {
            Transaction transaction = Transaction.getTransactionByIdentifier(type);
            if (transaction == null)
                throw new InvalidArgumentException();
            if (!account.equals(transaction.getSource()) && !account.equals(transaction.getDestination()))
                throw new IllegalAccountAccessException();
            return transaction.toString();
        }
        boolean toTokenAccount = (type.equals("*") || type.equals("+"));
        boolean fromTokenAccount = (type.equals("*") || type.equals("-"));
        if (!toTokenAccount && !fromTokenAccount)
            throw new InvalidArgumentException("invalid input");
        ArrayList<String> transactions = new ArrayList<>();
        for (Transaction transaction : ALL_TRANSACTIONS) {
            if ((account.equals(transaction.getSource()) && fromTokenAccount) ||
                    (account.equals(transaction.getDestination()) && toTokenAccount)) {
                transactions.add(transaction.toString());
            }
        }
        return String.join("*\n", transactions);
    }

    private static boolean isAccountNumberUnexpected(String accountNumberStr) {
        int accountNumber;
        try {
            accountNumber = Integer.parseInt(accountNumberStr);
        } catch (NumberFormatException e) {
            return true;
        }
        if (accountNumber == -1)
            return false;
        else
            return Account.getAccountByAccountNumber(accountNumber) == null;
    }

    @Override
    public String toString() {
        return "{" +
                "\"receiptType\":\"" + receiptType + "\",\n" +
                "\"money\":" + money + ",\n" +
                "\"sourceAccountID\":" + sourceID + ",\n" +
                "\"destAccountID\":" + destinationID + ",\n" +
                "\"description\":\"" + description + "\",\n" +
                "\"id\":\"" + identifier + "\",\n" +
                "\"paid\":" + (isPayed() ? "1" : "0") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public int compareTo(Transaction transaction) {
        int thisIdentifier = Integer.parseInt(this.getIdentifier().substring(5));
        int otherIdentifier = Integer.parseInt(transaction.getIdentifier().substring(5));
        return thisIdentifier - otherIdentifier;
    }
}
