package transaction;

import account.Account;
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

    private boolean status;

    public Transaction(String token, String receiptType, int money, int sourceID, int destinationID, String description,
                       boolean status) {
        this.token = token;
        this.receiptType = receiptType;
        this.money = money;
        this.sourceID = sourceID;
        this.destinationID = destinationID;
        this.description = description;
        this.status = status;
        ALL_TRANSACTIONS.add(this);
    }

    public static void getInstance(String token, String receiptType, String moneyStr, String sourceIDStr,
                                   String destinationIDStr, String description) throws TransactionTypeException,
            MoneyValueException, TokenNotFoundException, InvalidArgumentException {
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
        if (!isAccountNumberExpected(sourceIDStr)) {
            throw new InvalidArgumentException("source account id is invalid");
        }
        if (!isAccountNumberExpected(sourceIDStr)) {
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
    }

    public static void getInstance(String token, String receiptType, String moneyStr, String sourceIDStr,
                                   String destinationIDStr) throws TransactionTypeException, MoneyValueException,
            TokenNotFoundException, InvalidArgumentException {
        getInstance(token, receiptType, moneyStr, sourceIDStr, destinationIDStr, "");
    }

    public void pay() {
        status = true;
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

    private static boolean isAccountNumberExpected(String accountNumberStr) {
        int accountNumber;
        try {
            accountNumber = Integer.parseInt(accountNumberStr);
        } catch (NumberFormatException e) {
            return false;
        }
        return accountNumber >= -1;
    }
}
