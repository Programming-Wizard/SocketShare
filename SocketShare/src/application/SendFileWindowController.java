package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class SendFileWindowController implements Initializable {
	@FXML
	private Button selectFileButton;
	@FXML
	private Button sendButton;
	@FXML
	private Text FileNameText;
	@FXML
	private Text FilePathText;
	@FXML
	private ImageView FileReady;
	@FXML
	private ImageView ServerReady;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		FileReady.setVisible(false);
		ServerReady.setVisible(false);
		sendButton.setVisible(false);
		
		
	}
	public Button getSelectFileButton() {
		return selectFileButton;
	}
	public Button getSendButton() {
		return sendButton;
	}
	public Text getFileNameText() {
		return FileNameText;
	}
	public Text getFilePathText() {
		return FilePathText;
	}
	public ImageView getFileReady() {
		return FileReady;
	}
	public ImageView getServerReady() {
		return ServerReady;
	}
	
	
}
