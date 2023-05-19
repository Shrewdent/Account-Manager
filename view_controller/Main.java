// Shaun Wyllie
package view_controller;

import java.io.File;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Account;
import model.Serialization;

public class Main implements Serializable {

	GridPane gridPane;
	private TableView<Account> tableView;
	private ObservableList<Account> accountList;

	public Main() {
		gridPane = new GridPane();
		layoutScene();
		loadAccountsFromFile();
	}

	private void layoutScene() {

		// Create a VBox to hold the TableView and buttons
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(20, 0, 0, 22));
		vbox.setSpacing(10);
		vbox.setAlignment(Pos.CENTER);

		// Create a TableView for the accounts
		tableView = new TableView<>();

		// Create columns for the TableView
		TableColumn<Account, String> accountColumn = new TableColumn<>("Account");
		accountColumn.setCellValueFactory(new PropertyValueFactory<>("accountName"));

		TableColumn<Account, String> passwordColumn = new TableColumn<>("Password");
		passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
		
		TableColumn<Account, String> platformColumn = new TableColumn<>("Platform");
		platformColumn.setCellValueFactory(new PropertyValueFactory<>("platform"));

		// Set a custom cell factory for the password column
		passwordColumn.setCellFactory(column -> {
		    return new TableCell<Account, String>() {
		        @Override
		        protected void updateItem(String item, boolean empty) {
		            super.updateItem(item, empty);

		            if (item == null || empty) {
		                setText(null);
		            } else {
		                // Display the password as dots
		                StringBuilder maskedPassword = new StringBuilder();
		                for (int i = 0; i < item.length(); i++) {
		                    maskedPassword.append("•");
		                }
		                setText(maskedPassword.toString());
		            }
		        }
		    };
		});

		tableView.getColumns().addAll(accountColumn, passwordColumn, platformColumn);

		// Set the preferred width of the TableView
		tableView.setPrefWidth(510); // Adjust the width as needed
		
		// Set the preferred width for each column
		accountColumn.setPrefWidth(tableView.getPrefWidth() / 3);
		passwordColumn.setPrefWidth(tableView.getPrefWidth() / 3);
		platformColumn.setPrefWidth(tableView.getPrefWidth() / 3);

		// Add the TableView to the VBox
		vbox.getChildren().add(tableView);

		// Create an HBox to hold the buttons
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(10);

		// Create an "Add" button
		Button addButton = new Button("Add");
		addButton.setOnAction(e -> {
			// Open a dialog to enter the account and password
			Dialog<Account> dialog = new Dialog<>();
			dialog.setTitle("Add Account");
			dialog.setHeaderText("Enter Account Details");

			// Set up the dialog content
			GridPane dialogPane = new GridPane();
			dialogPane.setHgap(10);
			dialogPane.setVgap(10);

			TextField accountNameField = new TextField();
			accountNameField.setPromptText("Email");

			PasswordField passwordField = new PasswordField();
			passwordField.setPromptText("Password");

			TextField platformField = new TextField(); // New platform text field
			platformField.setPromptText("Platform");

			dialogPane.add(new Label("Email:"), 0, 0);
			dialogPane.add(accountNameField, 1, 0);
			dialogPane.add(new Label("Password:"), 0, 1);
			dialogPane.add(passwordField, 1, 1);
			dialogPane.add(new Label("Platform:"), 0, 2);
			dialogPane.add(platformField, 1, 2); // Add the platform text field

			dialog.getDialogPane().setContent(dialogPane);

			// Add buttons to the dialog
			ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

			// Set the result converter for the dialog
			dialog.setResultConverter(dialogButton -> {
			    if (dialogButton == addButtonType) {
			        String accountName = accountNameField.getText();
			        String password = passwordField.getText();
			        String platform = platformField.getText(); // Get the platform value
			        return new Account(accountName, password, platform);
			    }
			    return null;
			});

			// Show the dialog and process the result
			dialog.showAndWait().ifPresent(newAccount -> {
				accountList.add(newAccount);
			});
		});
		buttonBox.getChildren().add(addButton);

		// Create a "Delete" button
		Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> {
			// Get the selected account
			Account selectedAccount = tableView.getSelectionModel().getSelectedItem();
			if (selectedAccount != null) {
				// Remove the selected account from the list
				accountList.remove(selectedAccount);
			}
		});
		buttonBox.getChildren().add(deleteButton);

		// Create an "Edit" button
		Button editButton = new Button("Edit");
		editButton.setOnAction(e -> {
		    // Get the selected account
		    Account selectedAccount = tableView.getSelectionModel().getSelectedItem();
		    if (selectedAccount != null) {
		        // Open a dialog to edit the account details
		        Dialog<Account> dialog = new Dialog<>();
		        dialog.setTitle("Edit Account");
		        dialog.setHeaderText("Edit Account Details");

		        // Set up the dialog content
		        GridPane dialogPane = new GridPane();
		        dialogPane.setHgap(10);
		        dialogPane.setVgap(10);

		        TextField accountNameField = new TextField(selectedAccount.getAccountName());
		        accountNameField.setPromptText("Email");

		        TextField passwordField = new TextField(selectedAccount.getPassword());
		        passwordField.setPromptText("Password");

		        TextField platformField = new TextField(selectedAccount.getPlatform());
		        platformField.setPromptText("Platform");

		        dialogPane.add(new Label("Email:"), 0, 0);
		        dialogPane.add(accountNameField, 1, 0);
		        dialogPane.add(new Label("Password:"), 0, 1);
		        dialogPane.add(passwordField, 1, 1);
		        dialogPane.add(new Label("Platform:"), 0, 2);
		        dialogPane.add(platformField, 1, 2);

		        dialog.getDialogPane().setContent(dialogPane);

		        // Add buttons to the dialog
		        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		        // Set the result converter for the dialog
		        dialog.setResultConverter(dialogButton -> {
		            if (dialogButton == saveButtonType) {
		                String newAccountName = accountNameField.getText();
		                String newPassword = passwordField.getText();
		                String newPlatform = platformField.getText();
		                selectedAccount.setAccountName(newAccountName);
		                selectedAccount.setPassword(newPassword);
		                selectedAccount.setPlatform(newPlatform);
		                return selectedAccount;
		            }
		            return null;
		        });

		        // Show the dialog and process the result
		        dialog.showAndWait().ifPresent(editedAccount -> {
		            tableView.refresh();
		        });
		    }
		});
		buttonBox.getChildren().add(editButton);

		// Create a "Copy" button
		Button copyButton = new Button("Copy");
		copyButton.setOnAction(e -> {
			// Get the selected account
			Account selectedAccount = tableView.getSelectionModel().getSelectedItem();
			if (selectedAccount != null) {
				// Copy the password to the clipboard
				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent content = new ClipboardContent();
				content.putString(selectedAccount.getPassword());
				clipboard.setContent(content);
			}
		});
		buttonBox.getChildren().add(copyButton);

		Button generateButton = new Button("Generate");
		generateButton.setOnAction(e -> {
		    // Get the selected account
		    Account selectedAccount = tableView.getSelectionModel().getSelectedItem();
		    if (selectedAccount != null) {
		        // Generate a new password
		        String newPassword = generatePassword();
		        selectedAccount.setPassword(newPassword);
		        tableView.refresh();
		    }
		});
		buttonBox.getChildren().add(generateButton);
		// Create a "Save" button
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveAccountsToFile());
        buttonBox.getChildren().add(saveButton);

		// Enable the copy button when an account is selected
		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				copyButton.setDisable(false);
				generateButton.setDisable(false);
			} else {
				copyButton.setDisable(true);
				generateButton.setDisable(true);
			}
		});

		// Add the buttonBox to the VBox
		vbox.getChildren().add(buttonBox);

		// Add the VBox to the grid pane
		gridPane.add(vbox, 0, 0);
	}
	
	private void loadAccountsFromFile() {
	    File file = new File("accounts.ser");
	    if (file.exists()) {
	        Serialization serialization = new Serialization();
	        accountList = FXCollections.observableArrayList(serialization.loadDecryptedAccountsFromFile("accounts.ser"));
	        tableView.setItems(accountList);
	    } else {
	        accountList = FXCollections.observableArrayList();
	        tableView.setItems(accountList);
	    }
	}

    private void saveAccountsToFile() {
        Serialization serialization = new Serialization();
        serialization.saveEncryptedAccountsToFile("accounts.ser", accountList);
    }

	private String generatePassword() {
		// Define the password criteria
		int length = 10;
		boolean containsUpperCase = true;
		boolean containsLowerCase = true;
		boolean containsDigits = true;
		boolean containsSymbols = true;
		int minUpperCase = 1;
		int minLowerCase = 1;
		int minDigits = 1;
		int minSymbols = 1;

		// Define the character sets for each type of character
		String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
		String digits = "0123456789";
		String symbols = "!@#$%^&*()_-+=<>?";

		// Create a set to store the generated password characters
		Set<Character> passwordSet = new HashSet<>();

		// Create a random number generator
		Random random = new SecureRandom();

		// Generate at least one character from each type
		addRandomCharacter(passwordSet, upperCaseLetters, minUpperCase, random);
		addRandomCharacter(passwordSet, lowerCaseLetters, minLowerCase, random);
		addRandomCharacter(passwordSet, digits, minDigits, random);
		addRandomCharacter(passwordSet, symbols, minSymbols, random);

		// Generate remaining characters to reach the desired length
		while (passwordSet.size() < length) {
			String characterSet = "";

			if (containsUpperCase) {
				characterSet += upperCaseLetters;
			}
			if (containsLowerCase) {
				characterSet += lowerCaseLetters;
			}
			if (containsDigits) {
				characterSet += digits;
			}
			if (containsSymbols) {
				characterSet += symbols;
			}

			char randomCharacter = characterSet.charAt(random.nextInt(characterSet.length()));
			passwordSet.add(randomCharacter);
		}

		// Convert the set of characters to a string
		StringBuilder passwordBuilder = new StringBuilder();
		for (Character character : passwordSet) {
			passwordBuilder.append(character);
		}

		return passwordBuilder.toString();
	}

	private void addRandomCharacter(Set<Character> passwordSet, String characterSet, int count, Random random) {
		for (int i = 0; i < count; i++) {
			char randomCharacter = characterSet.charAt(random.nextInt(characterSet.length()));
			passwordSet.add(randomCharacter);
		}
	}
}