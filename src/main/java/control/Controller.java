package control;

import account.Account;
import account.exceptions.PasswordMissMatchException;
import account.exceptions.UsernameException;

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
        return null;
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
