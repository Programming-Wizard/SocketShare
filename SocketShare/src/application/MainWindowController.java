package application;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class MainWindowController implements Initializable{
	
	@FXML
	private Button chooseFileButton;
	@FXML
	private Button CreateAndSendButton;
	
	private String fileName = "";
	private String fileContent = "";
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		CreateAndSendButton.setOnAction(event->{
			openWritingWindow();
		});
		
	}
	
	private void openWritingWindow() {
		FXMLLoader CreateFileLoader = new FXMLLoader(getClass().getResource("/CreateFileWindow.fxml"));
		try {
			Stage CreateFileStage = new Stage();
			Parent CreateFileRoot = CreateFileLoader.load();
			Scene CreateFileScene = new Scene(CreateFileRoot);
			
			CreateFileStage.setScene(CreateFileScene);
			CreateFileStage.setTitle("Send File");
			CreateFileStage.setMaximized(true);
			CreateFileStage.setResizable(true); 
			CreateFileStage.show();
			CreatefileWindowController controller = CreateFileLoader.getController();
			CreateFileStage.setOnHidden(event->{
				getChooseFileButton().setDisable(false);
	        });
			CreateFileStage.getScene().getAccelerators().put(KeyCombination.keyCombination("CTRL+W"), new Runnable() {
				@Override
				public void run() {
					getChooseFileButton().setDisable(false);
					CreateFileStage.close();
				}
			});
			controller.getSendButton().setOnAction(event->{
				sendFileToServer(controller);
			});
			
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}
	
	public Button getChooseFileButton() {
		return chooseFileButton;
	}	
	public Button getCreateAndSendButton() {
		return CreateAndSendButton;
	}
	private void sendFileToServer(CreatefileWindowController controller) {
		Task<Void> SendTask = new Task<>() {

			@Override
			protected Void call() throws Exception {
				IPS ip = new IPS();
				String[] serverIPs = {ip.getZorinIP(),ip.getXubuntuIP()};
		        int port = 6961;
//		        for (String serverIP : serverIPs) {
		            InetSocketAddress serverAddress = new InetSocketAddress(serverIPs[2], port);
		            try {
		                String fileNameRegex = "^[^\\s]+(?:[_-][^\\s]+)*(\\.[^.]+)$";
		                String contentToSend = controller.getFileContent().getText();
		                if(!controller.getFileName().getText().trim().matches(fileNameRegex) || controller.getFileName().getText().trim().isEmpty() || contentToSend.trim().isEmpty()) {
		                	return null;
		                }
		                
		                System.out.println("Sending ...");
		                byte[] fileBytes = contentToSend.getBytes();
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
		                output.println(controller.getFileName().getText());
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
	}
}
