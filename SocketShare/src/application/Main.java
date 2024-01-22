package application;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {

	File selectedFile;
	String fileName;
	String filePath;

	labelDisplayer LabelDisplay = new labelDisplayer();

	@Override
	public void start(Stage primaryStage) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
		try {
			Parent root = loader.load();
			Scene scene = new Scene(root);
			primaryStage.setTitle("Socket Share");
			root.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();

			primaryStage.getScene().getAccelerators().put(KeyCombination.keyCombination("CTRL+W"), new Runnable() {

				@Override
				public void run() {
					Platform.exit();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Starting Window controller
		MainWindowController controller = loader.getController();
		controller.getChooseFileButton().setOnAction(event -> {
			// Send file from computer button click listener
			if (controller.getSendToIp().isEmpty()) {
				controller.LabelDisplayer.displayLabel(controller.errorLabel, controller.crossMark,
						"Select A Server to Send");
				return;
			}
			openSendFileWindow(controller);
			controller.getChooseFileButton().setDisable(true);
		});

	}

	//method to open the file chooser 
	private void openFileChooserDialogBox(Stage stage, FileChooser fileChooser, FXMLLoader loader) {
		SendFileWindowController controller = loader.getController();
		selectedFile = fileChooser.showOpenDialog(stage);
		if (selectedFile != null) {
			fileName = selectedFile.getName();
			filePath = selectedFile.getAbsolutePath();
			controller.getFileNameText().setText(fileName);
			controller.getFilePathText().setText(filePath);
			controller.getFileReady().setVisible(true);
			controller.getServerReady().setVisible(true);
			controller.getSendButton().setVisible(true);
		}
	}

	//Code to make and open the select file and send window 
	private void openSendFileWindow(MainWindowController controller) {
		FXMLLoader SendFileLoader = new FXMLLoader(getClass().getResource("/SendFileWindow.fxml"));
		FileChooser fileChooser = new FileChooser();
		try {
			Stage SendFileStage = new Stage();
			Parent SendFileRoot = SendFileLoader.load();
			Scene SendFileScene = new Scene(SendFileRoot);

			SendFileStage.setScene(SendFileScene);
			SendFileStage.setTitle("Send File");
			SendFileStage.setResizable(false);
			SendFileStage.show();
			SendFileStage.setOnHidden(event -> {
				controller.getChooseFileButton().setDisable(false);
			});
			SendFileStage.getScene().getAccelerators().put(KeyCombination.keyCombination("CTRL+W"), new Runnable() {
				@Override
				public void run() {
					controller.getChooseFileButton().setDisable(false);
					SendFileStage.close();
				}
			});

			SendFileWindowController SendFileController = SendFileLoader.getController();

			SendFileController.getSelectFileButton().setOnAction(event -> {
				openFileChooserDialogBox(SendFileStage, fileChooser, SendFileLoader);
			});

			SendFileController.getSendButton().setOnAction(event -> {
				LabelDisplay.displayLabel(SendFileController.errorLabel, SendFileController.tickMark, "connecting");
				sendFileToServer(SendFileController, controller);
			});
		} catch (Exception e) {
		}
	}

	//code to send the content to server when the send button is clicked
	private void sendFileToServer(SendFileWindowController controller, MainWindowController mainWindowController) {
		Task<Void> SendTask = new Task<>() {

			@Override
			protected Void call() throws Exception {
				int port = 6961;
				System.out.println("sned to ip : " + mainWindowController.getSendToIp());
				InetSocketAddress serverAddress = new InetSocketAddress(mainWindowController.getSendToIp(), port);

				try {
					File fileToSend = selectedFile;
					byte[] fileBytes = Files.readAllBytes(fileToSend.toPath());
					Socket socket = new Socket();
					System.out.println("this is outer try");
					try {
						socket.connect(serverAddress);
						System.out.println("this is inner try");
					} catch (IOException e) {
						System.out.println("innner try failed");
						System.err.println("Failed to connect to " + serverAddress);
						e.printStackTrace();
					}

					PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
					output.println(fileName);
					output.flush();

					OutputStream outputStream = socket.getOutputStream();
					outputStream.write(fileBytes);
					outputStream.flush();

					System.out.println("this wont run if the sending has failed");

					System.out.println("Connected to: " + serverAddress);

					socket.close();
				} catch (UnknownHostException e) {
					System.out.println("this is first catch");
					LabelDisplay.displayLabel(controller.errorLabel, controller.crossMark,
							"Couldn't connect Server is Unavailable");
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("this is second catch");
					LabelDisplay.displayLabel(controller.errorLabel, controller.crossMark,
							"Couldn't connect Server is Unavailable");
					e.printStackTrace();
				}
				return null;
			}
		};
		Thread sendTaskThread = new Thread(SendTask);
		sendTaskThread.start();
		LabelDisplay.displayLabel(controller.errorLabel, controller.tickMark, "File Sent to Server");
	}

	public static void main(String[] args) {
		launch(args);
	}
}
