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
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
	
	File selectedFile;
	String fileName;
	String filePath;
	
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
		MainWindowController controller = loader.getController();
		controller.getChooseFileButton().setOnAction(event->{
			//Send file from computer button click listener
			openSendFileWindow(controller);
			controller.getChooseFileButton().setDisable(true);
		});
		
//		controller.getSendButton().setOnAction(event->{
//			sendFileToServer();
//			System.out.println("send button clicked");
//		});
		
	}
	
	private void openFileChooserDialogBox(Stage stage, FileChooser fileChooser, FXMLLoader loader) {
		SendFileWindowController controller = loader.getController();
		selectedFile = fileChooser.showOpenDialog(stage);
		if(selectedFile != null) {
			fileName = selectedFile.getName();
			filePath = selectedFile.getAbsolutePath();
			controller.getFileNameText().setText(fileName);
			controller.getFilePathText().setText(filePath);
			controller.getFileReady().setVisible(true);
			controller.getServerReady().setVisible(true);
			controller.getSendButton().setVisible(true);
		}
	}
	
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
	        SendFileStage.setOnHidden(event->{
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
			
			SendFileController.getSelectFileButton().setOnAction(event->{
				openFileChooserDialogBox(SendFileStage, fileChooser, SendFileLoader);
			});
			
			SendFileController.getSendButton().setOnAction(event->{
				sendFileToServer();
			});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void sendFileToServer() {
		Task<Void> SendTask = new Task<>() {

			@Override
			protected Void call() throws Exception {
				IPS ip = new IPS();
				String[] serverIPs = {ip.getZorinIP(),ip.getXubuntuIP()};
		        int port = 6961;
//		        for (String serverIP : serverIPs) {
		            InetSocketAddress serverAddress = new InetSocketAddress(serverIPs[2], port);

		            try {
		                File fileToSend = selectedFile;
		                byte[] fileBytes = Files.readAllBytes(fileToSend.toPath());
		                Socket socket = new Socket();

		                try {
		                    socket.connect(serverAddress);
		                } catch (IOException e) {
		                    System.err.println("Failed to connect to " + serverAddress);
		                    e.printStackTrace();
//		                    continue;
		                }

//		                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
		                output.println(fileName);
		                output.flush();

		                OutputStream outputStream = socket.getOutputStream();
		                outputStream.write(fileBytes);
		                outputStream.flush();

		                System.out.println("Connected to: " + serverAddress);

		                socket.close();
//		                break;
		            } catch (UnknownHostException e) {
		                e.printStackTrace();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
//		        }
				return null;
			}};
			Thread sendTaskThread = new Thread(SendTask);
			sendTaskThread.start();
		
        
//        }
	}

	public static void main(String[] args) {
		launch(args);
	}
}
