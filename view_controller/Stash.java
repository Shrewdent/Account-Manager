// Shaun Wyllie
package view_controller;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.Serializable;
import java.util.Properties;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Stash extends Application implements Serializable {
	Main main;
	MenuBar menuBar;
	Menu optionMenu;
	MenuItem sendOption;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		main = new Main(); // Create an instance of the Main class

		createMenuBar();
		registerHandlers();

		var vbox = new VBox();
		vbox.getChildren().addAll(menuBar, main.gridPane); // Add the Main's GridPane and button to the VBox

		var scene = new Scene(vbox, 550, 500);
		stage.setTitle("Stash");
		stage.setScene(scene);
		stage.show();
	}

	private void createMenuBar() {
		menuBar = new MenuBar();
		optionMenu = new Menu("Options");
		sendOption = new MenuItem("Send");

		optionMenu.getItems().add(sendOption);
		menuBar.getMenus().add(optionMenu);
	}

	private void registerHandlers() {
		sendOption.setOnAction(new SendHandler());
	}

	private class SendHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			sendEmail();
		}
	}

	public void sendEmail() {
		// Sender's email address and password
		String senderEmail = "Your Email";
		String senderPassword = "Your Password";

		// Recipient's email address
		String recipientEmail = "Email You Want To Send To";

		// Email properties and authentication
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.mail.yahoo.com");
		props.put("mail.smtp.port", "587");

		// Create a Session object with the authentication information
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(senderEmail, senderPassword);
			}
		});

		try {
			// Create a MimeMessage object
			Message message = new MimeMessage(session);

			// Set the sender and recipient addresses
			message.setFrom(new InternetAddress(senderEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

			// Set the email subject
			message.setSubject("Stash Accounts");

			// Create the attachment MimeBodyPart
			MimeBodyPart attachmentPart = new MimeBodyPart();

			// Set the attachment file
			File accountsFile = new File("accounts.ser");
			FileDataSource source = new FileDataSource(accountsFile);
			attachmentPart.setDataHandler(new DataHandler(source));
			attachmentPart.setFileName(accountsFile.getName());

			// Create a Multipart object and add the attachment MimeBodyPart
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(attachmentPart);

			// Set the content of the email to the Multipart object
			message.setContent(multipart);

			// Send the email
			Transport.send(message);

			System.out.println("Email sent successfully!");
		} catch (MessagingException ex) {
			System.out.println("Failed to send email. Error: " + ex.getMessage());
		}
	}
}
