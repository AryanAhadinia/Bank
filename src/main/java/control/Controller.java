package control;

import account.Account;
import account.exceptions.IllegalAccountAccessException;
import account.exceptions.PasswordMissMatchException;
import account.exceptions.TokenNotFoundException;
import account.exceptions.UsernameException;
import transaction.Transaction;
import transaction.exceptions.InvalidArgumentException;
import transaction.exceptions.MoneyValueException;
import transaction.exceptions.TransactionTypeException;

import java.util.Arrays;

public class Controller {

    public String controlCreateAccount(String[] requestElements) {
        try {
            return  Account.getInstance(requestElements[1], requestElements[2], requestElements[3], requestElements[4],
                    requestElements[5]);
        } catch (UsernameException e) {
            return "username is not available";
        } catch (PasswordMissMatchException e) {
            return "passwords do not match";
        }
    }

    public String controlGetToken(String[] requestElements) {
        try {
            return Account.assignToken(requestElements[1], requestElements[2]);
        } catch (UsernameException | PasswordMissMatchException e) {
            return "invalid username or password";
        }
    }

    public String controlCreateReceipt(String[] requestElements) {
        if (requestElements.length != 6 && requestElements.length != 7) {
            return "invalid parameters passed";
        }
        if (requestElements.length == 6) {
            requestElements = Arrays.copyOf(requestElements, 7);
            requestElements[6] = "";
        }
        try {
            return Transaction.getInstance(requestElements[1], requestElements[2], requestElements[3],
                    requestElements[4], requestElements[5], requestElements[6]);
        } catch (TransactionTypeException e) {
            return "invalid receipt type";
        } catch (MoneyValueException e) {
            return "invalid money";
        } catch (TokenNotFoundException e) {
            return "token expired";
        } catch (InvalidArgumentException e) {
            return e.getMessage();
        } catch (IllegalAccountAccessException e) {
            return "token is invalid";
        }
    }

    public String controlGetTransactions(String[] requestElements) {
        return null;
    }

    public String controlPay(String[] requestElements) {
        return null;
    }

    public String controlGetBalance(String[] requestElements) {
        return null;
    }
}
