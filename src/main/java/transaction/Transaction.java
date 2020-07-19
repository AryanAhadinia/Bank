package transaction;

import account.Account;
import account.exceptions.AccountNotFoundException;
import account.exceptions.IllegalAccountAccessException;
import account.exceptions.TokenNotFoundException;
import transaction.exceptions.InvalidArgumentException;
import transaction.exceptions.MoneyValueException;
import transaction.exceptions.TransactionTypeException;

import java.util.ArrayList;

public class Transaction {
    private static final ArrayList<Transaction> ALL_TRANSACTIONS = new ArrayList<>();

    private final String token;
    private final String receiptType;
    private final int money;
    private final int sourceID;
    private final int destinationID;
    private final String description;

    private boolean payed;

    private final String identifier;

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
        ALL_TRANSACTIONS.add(this);
    }

    public static String getInstance(String token, String receiptType, String moneyStr, String sourceIDStr,
                                   String destinationIDStr, String description) throws TransactionTypeException,
            MoneyValueException, TokenNotFoundException, InvalidArgumentException, IllegalAccountAccessException {
        if (!(receiptType.equals("deposit") || receiptType.equals("withdraw") || receiptType.equals("move"))) {
            throw new TransactionTypeException();
        }
        int money;
        try {
            money = Integer.parseInt(moneyStr);
        } catch (NumberFormatException e) {
            throw new MoneyValueException();
        }
        if (Account.getAccountByToken(token) == null) {
            throw new TokenNotFoundException();
        }
        if (isAccountNumberUnexpected(sourceIDStr)) {
            throw new InvalidArgumentException("source account id is invalid");
        }
        if (isAccountNumberUnexpected(sourceIDStr)) {
            throw new InvalidArgumentException("dest account id is invalid");
        }
        int sourceId = Integer.parseInt(sourceIDStr);
        int destinationId = Integer.parseInt(destinationIDStr);
        if (sourceId == destinationId) {
            throw new InvalidArgumentException("equal source and dest account");
        }
        if (receiptType.equals("move") && !(sourceId != -1 && destinationId != -1)) {
            throw new InvalidArgumentException("invalid account id");
        }
        if (receiptType.equals("deposit") && sourceId != -1) {
            throw new InvalidArgumentException("invalid account id");
        }
        if (receiptType.equals("withdraw") && destinationId != -1) {
            throw new InvalidArgumentException("invalid account id");
        }
        if (!description.matches("\\w*")) {
            throw new InvalidArgumentException("your input contains invalid characters");
        }
        Account account = Account.getAccountByAccountNumber(sourceId);
        if ((receiptType.equals("move") || receiptType.equals("withdraw"))) {
            if (account == null) {
                throw new InvalidArgumentException("invalid account id");
            }
            if (!account.getToken().equals(token)) {
                throw new IllegalAccountAccessException();
            }
        }
        return new Transaction(token, receiptType, money, sourceId, destinationId, description, false,
                "TR" + receiptType.substring(0, 3).toUpperCase() + String.format("%015d",
                        ALL_TRANSACTIONS.size() + 1)).getIdentifier();
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
                Account source = Account.getAccountByAccountNumber(sourceID);
                if (source == null) {
                    throw new AccountNotFoundException();
                }
                source.deposit(money);
            }
            if (receiptType.equals("withdraw")) {
                Account destination = Account.getAccountByAccountNumber(destinationID);
                if (destination == null) {
                    throw new AccountNotFoundException();
                }
                destination.withdraw(money);
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
                source.deposit(money);
                destination.withdraw(money);
            }
            payed = true;
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

    @Override
    public String toString() {
        return "{" +
                "\"token\":\"" + token + '\"' +
                ", \"receiptType\":\"" + receiptType + '\"' +
                ", \"money\":" + money +
                ", \"sourceID\":" + sourceID +
                ", \"destinationID\":" + destinationID +
                ", \"description\":\"" + description + '\"' +
                '}';
    }

    public static String getTransactions(String token, String type) {
        boolean toTokenAccount = (type.equals("*") || type.equals("-"));
        boolean fromTokenAccount = (type.equals("*") || type.equals("+"));
        String transactions = "";
        return transactions;
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
}
