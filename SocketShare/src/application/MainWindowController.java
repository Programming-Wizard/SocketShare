package application;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainWindowController implements Initializable {

	@FXML
	private Button chooseFileButton;
	@FXML
	private Button CreateAndSendButton;
	@FXML
	private SplitMenuButton ServerMenu;
	@FXML
	MenuItem menuItem1;
	@FXML
	MenuItem menuItem2;
	@FXML
	MenuItem menuItem3;
	@FXML
	Text crossMark;
	@FXML
	Label errorLabel;
	@FXML
	ImageView background;
//	private String ipv4Regex = "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])";
	IPS ip = new IPS();
	private String SendToIp = "";
	labelDisplayer LabelDisplayer = new labelDisplayer();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
//		Image mainWindowBackground = new Image("/mainWindowBackground.png");
//		background.setImage(mainWindowBackground);
		errorLabel.setOpacity(0);
		crossMark.setOpacity(0);
		
		//making and opening Window which takes the IP of other open Servers 
		FXMLLoader CustomServerWindowLoader = new FXMLLoader(
				getClass().getResource("/CustomServerSelectionWindow.fxml"));
		Stage CSWindowStage = new Stage();

		try {
			Parent newroot = CustomServerWindowLoader.load();
			Scene CSWindowScene = new Scene(newroot, Color.TRANSPARENT);
			CSWindowStage.setScene(CSWindowScene);
			CSWindowStage.setResizable(false);
			Image icon = new Image("/logo.png");
			CSWindowStage.getIcons().add(icon);
			CSWindowStage.initStyle(StageStyle.TRANSPARENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		Create and send file button listener on the starting window
		CreateAndSendButton.setOnAction(event -> {
			if (SendToIp.isEmpty()) {
				LabelDisplayer.displayLabel(errorLabel, crossMark, "Select A Server to Send");
				return;
			}
			openWritingWindow();
		});
		menuItem1.setOnAction(e1 -> {
			ServerMenu.setText(menuItem1.getText());
			SendToIp = ip.getZorinIP();
		});
		menuItem2.setOnAction(e1 -> {
			ServerMenu.setText(menuItem2.getText());
			SendToIp = ip.getXubuntuIP();
		});
//		opening the window which takes IP address to other servers 
		menuItem3.setOnAction(e1 -> {
			CSWindowStage.show();

			CSSelectWindowController controller = CustomServerWindowLoader.getController();
			controller.getCloseButton().setOnMouseClicked(event -> {
				CSWindowStage.close();
			});
			controller.getSelectIpButton().setOnAction(event -> {
				SendToIp = controller.getIpText().getText().trim();
				if (SendToIp.isEmpty()) {
					LabelDisplayer.displayLabel(controller.getErrorLabel(), controller.getCrossMark(),
							"Enter The Ip-Address");
					return;
				}
				System.out.println("sending to ip : " + SendToIp);
				ServerMenu.setText(SendToIp);
				CSWindowStage.close();
			});
		});
	}

//	method to open Create and send file window
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
			Image icon = new Image("/logo.png");
			CreateFileStage.getIcons().add(icon);
			CreateFileStage.show();
			CreatefileWindowController controller = CreateFileLoader.getController();
			CreateFileStage.setOnHidden(event -> {
				getChooseFileButton().setDisable(false);
			});
			CreateFileStage.getScene().getAccelerators().put(KeyCombination.keyCombination("CTRL+W"), new Runnable() {
				@Override	
				public void run() {
					getChooseFileButton().setDisable(false);
					CreateFileStage.close();
				}
			});
			controller.getSendButton().setOnAction(event -> {
				sendFileToServer(controller);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	method which reads the content from the TextArea and sends to the selected server

	private void sendFileToServer(CreatefileWindowController controller) {
		Task<Void> SendTask = new Task<>() {

			@Override
			protected Void call() throws Exception {
				int port = 6961;
				InetSocketAddress serverAddress = new InetSocketAddress(SendToIp, port);
				try {
					String fileNameRegex = "^[^\\s]+(?:[_-][^\\s]+)*(\\.[^.]+)$";
					String contentToSend = controller.getFileContent().getText();
					if (!controller.getFileName().getText().trim().matches(fileNameRegex)
							|| controller.getFileName().getText().trim().isEmpty() || contentToSend.trim().isEmpty()) {	
						LabelDisplayer.displayLabel(controller.errorLabel, controller.crossMark,
								"Enter File Name correctly");
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
						// continue;
					}

					// BufferedReader input = new BufferedReader(new
					// InputStreamReader(socket.getInputStream()));
					PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
					output.println(controller.getFileName().getText());
					output.flush();

					OutputStream outputStream = socket.getOutputStream();
					outputStream.write(fileBytes);
					outputStream.flush();

					System.out.println("Connected to: " + serverAddress);

					socket.close();
					// break;
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// }
				return null;
			}
		};
		Thread sendTaskThread = new Thread(SendTask);
		sendTaskThread.start();
	}

	public String getSendToIp() {
		return SendToIp;
	}

	public Button getChooseFileButton() {
		return chooseFileButton;
	}

	public Button getCreateAndSendButton() {
		return CreateAndSendButton;
	}

	public SplitMenuButton getServerMenu() {
		return ServerMenu;
	}
}
