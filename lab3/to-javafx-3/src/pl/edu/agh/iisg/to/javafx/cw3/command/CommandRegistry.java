package pl.edu.agh.iisg.to.javafx.cw3.command;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CommandRegistry {

	private ObservableList<Command> commandStack = FXCollections.observableArrayList();
    private ObservableList<Command> undoedCommandStack = FXCollections.observableArrayList();

    public void executeCommand(Command command) {
		command.execute();
		commandStack.add(command);
		undoedCommandStack = FXCollections.observableArrayList();
	}

	public void redo() {
        if (undoedCommandStack.isEmpty())
            return;

        Command command = undoedCommandStack.remove(undoedCommandStack.size()-1);
        command.redo();
        commandStack.add(command);
	}

	public void undo() {
		if (commandStack.isEmpty())
		    return;

		Command command = commandStack.remove(commandStack.size()-1);
		command.undo();
		undoedCommandStack.add(command);
	}

	public ObservableList<Command> getCommandStack() {
		return commandStack;
	}
}
