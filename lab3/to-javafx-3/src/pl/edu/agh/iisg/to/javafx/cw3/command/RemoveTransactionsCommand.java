package pl.edu.agh.iisg.to.javafx.cw3.command;

import pl.edu.agh.iisg.to.javafx.cw3.model.Account;
import pl.edu.agh.iisg.to.javafx.cw3.model.Transaction;

import java.util.List;

public class RemoveTransactionsCommand implements Command {

    private Account account;
    private List<Transaction> transactionsToRemove;

    public RemoveTransactionsCommand(Account account, List<Transaction> transactionsToRemove) {
        this.account = account;
        this.transactionsToRemove = transactionsToRemove;
    }

    @Override
    public void execute() {

        for (Transaction transaction : transactionsToRemove) {
            account.removeTransaction(transaction);
        }
    }

    @Override
    public void undo() {
        for (Transaction transaction : transactionsToRemove) {
            account.addTransaction(transaction);
        }
    }

    @Override
    public void redo() {
        for (Transaction transaction : transactionsToRemove) {
            account.removeTransaction(transaction);
        }
    }

    @Override
    public String getName() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Transaction transaction : transactionsToRemove){
            stringBuilder.append("Removed transaction: " + transaction.toString() + "\n");
        }

        return stringBuilder.toString();
    }
}
