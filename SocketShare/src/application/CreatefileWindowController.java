package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class CreatefileWindowController implements Initializable{
	
	@FXML
	private TextField fileName;
	@FXML
	private Button SendButton;
	@FXML
	private TextArea FileContent;
	@FXML
	Text crossMark;
	@FXML
	Text tickMark;
	@FXML
	Label errorLabel;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		errorLabel.setOpacity(0);
		crossMark.setOpacity(0);
		tickMark.setOpacity(0);
			
	}
	public TextArea getFileContent() {
		return FileContent;
	}
	public TextField getFileName() {
		return fileName;
	}
	public Button getSendButton() {
		return SendButton;
	}

}
